package dt.vista;

import dt.controlador.ControladorSGSI;
import dt.modelo.Paciente;
import dt.modelo.Turno;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Pantalla completa para la gestión de la Historia Clínica (Alta y Consulta).
 */
public class PantallaHistoriaClinica extends JFrame {

    private final ControladorSGSI controlador;

    // Componentes Pestaña 1: Nueva Evolución
    private JList<Turno> listaTurnos;
    private JTextArea areaObservaciones;
    private JButton btnGuardarEvolucion;
    private DefaultListModel<Turno> listModel;

    // Componentes Pestaña 2: Consultar Historias
    private JComboBox<Paciente> cmbPacientes;
    private JTextArea areaHistoriasConsulta;

    // Renderizador para mostrar los turnos de forma legible en el JList
    private class TurnoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Turno) {
                Turno t = (Turno) value;
                String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(t.getFechaYHora());
                setText(String.format("Turno #%d: %s - Paciente: %s %s | Prof: %s %s",
                        t.getIdTurno(),
                        fecha,
                        t.getPaciente().getNombre(),
                        t.getPaciente().getApellido(),
                        t.getProfesional().getNombre(),
                        t.getProfesional().getApellido()));
            }
            return this;
        }
    }

    // Renderizador para el JComboBox de Pacientes
    private class PacienteListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Paciente) {
                Paciente p = (Paciente) value;
                setText(p.getNombre() + " " + p.getApellido());
            }
            return this;
        }
    }

    public PantallaHistoriaClinica() {
        this.controlador = new ControladorSGSI();

        setTitle("Gestión de Historia Clínica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear el JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Construir y agregar las pestañas
        tabbedPane.addTab("Nueva Evolución", crearPanelNuevaEvolucion());
        tabbedPane.addTab("Consultar Historias", crearPanelConsultaHistorias());

        add(tabbedPane);

        // Cargar datos iniciales
        cargarTurnos();
        cargarPacientesConsulta();
    }

    private JPanel crearPanelNuevaEvolucion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel Izquierdo: Lista de Turnos ---
        listModel = new DefaultListModel<>();
        listaTurnos = new JList<>(listModel);
        listaTurnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTurnos.setCellRenderer(new TurnoListCellRenderer());
        JScrollPane scrollLista = new JScrollPane(listaTurnos);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Seleccione un Turno Pendiente/Atendido"));
        scrollLista.setPreferredSize(new Dimension(350, 0));
        panel.add(scrollLista, BorderLayout.WEST);

        // --- Panel Derecho: Observaciones ---
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        areaObservaciones = new JTextArea();
        areaObservaciones.setLineWrap(true);
        areaObservaciones.setWrapStyleWord(true);
        areaObservaciones.setBorder(BorderFactory.createTitledBorder("Observaciones de la Evolución"));
        areaObservaciones.setEnabled(false); // Deshabilitado por defecto
        panelDerecho.add(new JScrollPane(areaObservaciones), BorderLayout.CENTER);

        btnGuardarEvolucion = new JButton("Guardar Evolución");
        btnGuardarEvolucion.setEnabled(false); // Deshabilitado por defecto
        panelDerecho.add(btnGuardarEvolucion, BorderLayout.SOUTH);

        panel.add(panelDerecho, BorderLayout.CENTER);

        // --- Lógica ---
        listaTurnos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean isTurnoSelected = listaTurnos.getSelectedValue() != null;
                    areaObservaciones.setEnabled(isTurnoSelected);
                    btnGuardarEvolucion.setEnabled(isTurnoSelected);
                }
            }
        });

        btnGuardarEvolucion.addActionListener(e -> guardarEvolucion());

        return panel;
    }

    private JPanel crearPanelConsultaHistorias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con el JComboBox
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Seleccione un paciente:"));
        cmbPacientes = new JComboBox<>();
        cmbPacientes.setRenderer(new PacienteListCellRenderer());
        panelSuperior.add(cmbPacientes);
        panel.add(panelSuperior, BorderLayout.NORTH);

        // Área de texto central para mostrar las historias
        areaHistoriasConsulta = new JTextArea();
        areaHistoriasConsulta.setEditable(false);
        areaHistoriasConsulta.setLineWrap(true);
        areaHistoriasConsulta.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(areaHistoriasConsulta);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historias Previas"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // ActionListener para el JComboBox
        cmbPacientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarHistorias();
            }
        });

        return panel;
    }

    private void cargarTurnos() {
        listModel.clear();
        List<Turno> turnos = controlador.obtenerTodosLosTurnos();
        for (Turno t : turnos) {
            // Se asume que se quiere mostrar turnos que no estén cancelados para evolución
            if (!"Cancelado".equalsIgnoreCase(t.getEstado())) {
                listModel.addElement(t);
            }
        }
    }

    private void guardarEvolucion() {
        Turno turnoSeleccionado = listaTurnos.getSelectedValue();
        String observaciones = areaObservaciones.getText().trim();

        if (turnoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un turno.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (observaciones.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Las observaciones no pueden estar vacías.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            controlador.guardarHistoriaClinica(turnoSeleccionado, observaciones);
            
            // Opcional: Marcar el turno como 'Atendido' si estaba 'Pendiente'
            if ("Pendiente".equalsIgnoreCase(turnoSeleccionado.getEstado())) {
                turnoSeleccionado.setEstado("Atendido");
                controlador.modificarTurno(turnoSeleccionado);
            }

            JOptionPane.showMessageDialog(this, "Evolución guardada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar y recargar
            areaObservaciones.setText("");
            listaTurnos.clearSelection();
            cargarTurnos(); // Recargar para reflejar el posible cambio de estado
            
            // Si el paciente seleccionado en la otra pestaña es el mismo, actualizar consulta
            Paciente pacCombo = (Paciente) cmbPacientes.getSelectedItem();
            if (pacCombo != null && pacCombo.getId() == turnoSeleccionado.getPaciente().getId()) {
                consultarHistorias();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPacientesConsulta() {
        cmbPacientes.removeAllItems();
        List<Paciente> pacientes = controlador.obtenerTodosLosPacientes();
        for (Paciente p : pacientes) {
            cmbPacientes.addItem(p);
        }
    }

    private void consultarHistorias() {
        Paciente pacienteSeleccionado = (Paciente) cmbPacientes.getSelectedItem();
        if (pacienteSeleccionado != null) {
            try {
                List<String> historias = controlador.obtenerHistoriasPorPaciente(pacienteSeleccionado.getId());
                areaHistoriasConsulta.setText("");
                if (historias.isEmpty()) {
                    areaHistoriasConsulta.setText("No hay historias clínicas registradas para este paciente.");
                } else {
                    for (String historia : historias) {
                        areaHistoriasConsulta.append(historia + "\n\n");
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
