package dt.vista;

import dt.controlador.ControladorSGSI;
import dt.modelo.Paciente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Pantalla para consultar las historias clínicas de un paciente.
 */
public class PantallaConsultaHistorias extends JFrame {

    private final ControladorSGSI controlador;
    private JComboBox<Paciente> cmbPacientes;
    private JTextArea areaHistorias;

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

    public PantallaConsultaHistorias() {
        this.controlador = new ControladorSGSI();

        setTitle("Consulta de Historias Clínicas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con el JComboBox
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.add(new JLabel("Seleccione un paciente:"));
        cmbPacientes = new JComboBox<>();
        cmbPacientes.setRenderer(new PacienteListCellRenderer());
        panelSuperior.add(cmbPacientes);
        add(panelSuperior, BorderLayout.NORTH);

        // Área de texto central para mostrar las historias
        areaHistorias = new JTextArea();
        areaHistorias.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaHistorias);
        add(scrollPane, BorderLayout.CENTER);

        // Cargar pacientes en el JComboBox
        cargarPacientes();

        // ActionListener para el JComboBox
        cmbPacientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarHistorias();
            }
        });
    }

    private void cargarPacientes() {
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
                areaHistorias.setText("");
                for (String historia : historias) {
                    areaHistorias.append(historia + "\n");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al consultar las historias: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
