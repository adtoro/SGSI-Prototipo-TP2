package dt.vista;

import dt.controlador.ControladorSGSI;
import dt.modelo.Paciente;
import dt.modelo.Profesional;
import dt.modelo.Turno;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla para la gestión ABM completa de Turnos.
 * Permite registrar, consultar, modificar y eliminar turnos médicos,
 * interactuando con la base de datos a través de ControladorSGSI.
 */
public class PantallaGestionTurnos extends JFrame {

    private final ControladorSGSI controlador;

    private JComboBox<Paciente> cmbPacientes;
    private JComboBox<Profesional> cmbProfesionales;
    private JTextField txtFechaHora;
    private JComboBox<String> cmbEstado;
    private JTextArea areaTurnos;
    private Turno turnoSeleccionado; // Para guardar el turno cargado para modificar/eliminar

    // Renderizadores para JComboBox
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

    private class ProfesionalListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Profesional) {
                Profesional p = (Profesional) value;
                setText(p.getNombre() + " " + p.getApellido());
            }
            return this;
        }
    }

    /**
     * Constructor de la clase PantallaGestionTurnos.
     * Inicializa la interfaz gráfica, los componentes del formulario,
     * la conexión con el controlador y carga los datos iniciales.
     */
    public PantallaGestionTurnos() {
        this.controlador = new ControladorSGSI();

        setTitle("Gestión de Turnos (ABM)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Formulario y Botones ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos del Turno"));

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormulario.add(new JLabel("Paciente:"));
        cmbPacientes = new JComboBox<>();
        cmbPacientes.setRenderer(new PacienteListCellRenderer());
        panelFormulario.add(cmbPacientes);

        panelFormulario.add(new JLabel("Profesional:"));
        cmbProfesionales = new JComboBox<>();
        cmbProfesionales.setRenderer(new ProfesionalListCellRenderer());
        panelFormulario.add(cmbProfesionales);

        panelFormulario.add(new JLabel("Fecha y Hora (yyyy-MM-dd HH:mm):"));
        txtFechaHora = new JTextField();
        panelFormulario.add(txtFechaHora);

        panelFormulario.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(new String[]{"Pendiente", "Atendido", "Cancelado"});
        panelFormulario.add(cmbEstado);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton btnGuardar = new JButton("Guardar Turno");
        JButton btnBuscar = new JButton("Buscar por ID");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel Central: Área de Texto ---
        areaTurnos = new JTextArea();
        areaTurnos.setEditable(false);
        areaTurnos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaTurnos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Turnos Registrados"));
        add(scrollPane, BorderLayout.CENTER);

        cargarCombos();

        // --- Action Listeners ---
        btnGuardar.addActionListener(e -> guardarTurno());
        btnBuscar.addActionListener(e -> buscarTurno());
        btnModificar.addActionListener(e -> modificarTurno());
        btnEliminar.addActionListener(e -> eliminarTurno());

        actualizarListaTurnos();
    }

    /**
     * Carga los JComboBox de pacientes y profesionales con los datos
     * obtenidos desde la base de datos a través del controlador.
     */
    private void cargarCombos() {
        cmbPacientes.removeAllItems();
        controlador.obtenerTodosLosPacientes().forEach(cmbPacientes::addItem);

        cmbProfesionales.removeAllItems();
        controlador.obtenerTodosLosProfesionales().forEach(cmbProfesionales::addItem);
    }

    /**
     * Valida los datos del formulario, instancia un nuevo Turno y
     * solicita al controlador que lo guarde en la base de datos.
     * Muestra mensajes de éxito o error según corresponda.
     */
    private void guardarTurno() {
        try {
            Paciente paciente = (Paciente) cmbPacientes.getSelectedItem();
            Profesional profesional = (Profesional) cmbProfesionales.getSelectedItem();
            String fechaTexto = txtFechaHora.getText().trim();
            String estado = (String) cmbEstado.getSelectedItem();

            if (paciente == null || profesional == null || fechaTexto.isEmpty() || estado == null) {
                throw new Exception("Todos los campos son obligatorios.");
            }

            Date fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaTexto);
            int idTurno = (int) (System.currentTimeMillis() % 100000);
            Turno nuevoTurno = new Turno(idTurno, paciente, profesional, fecha, estado);

            controlador.crearTurno(nuevoTurno);
            actualizarListaTurnos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Turno guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use yyyy-MM-dd HH:mm", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Solicita al usuario un ID de turno, lo busca en la base de datos
     * mediante el controlador y carga sus datos en el formulario si es encontrado.
     */
    private void buscarTurno() {
        String idStr = JOptionPane.showInputDialog(this, "Ingrese el ID del turno a buscar:", "Buscar Turno", JOptionPane.QUESTION_MESSAGE);
        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int idTurno = Integer.parseInt(idStr.trim());
                Optional<Turno> turnoOpt = controlador.obtenerTurnoPorId(idTurno);
                turnoOpt.ifPresentOrElse(
                        this::cargarTurnoEnFormulario,
                        () -> JOptionPane.showMessageDialog(this, "No se encontró turno con ID " + idTurno, "No Encontrado", JOptionPane.ERROR_MESSAGE)
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Valida los datos modificados en el formulario y actualiza el turno
     * seleccionado previamente en la base de datos a través del controlador.
     */
    private void modificarTurno() {
        if (turnoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar un turno para poder modificarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Paciente paciente = (Paciente) cmbPacientes.getSelectedItem();
            Profesional profesional = (Profesional) cmbProfesionales.getSelectedItem();
            String fechaTexto = txtFechaHora.getText().trim();
            String estado = (String) cmbEstado.getSelectedItem();

            if (paciente == null || profesional == null || fechaTexto.isEmpty() || estado == null) {
                throw new Exception("Todos los campos son obligatorios.");
            }

            Date fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(fechaTexto);
            
            // Actualizamos el objeto Turno existente
            turnoSeleccionado.setPaciente(paciente);
            turnoSeleccionado.setProfesional(profesional);
            turnoSeleccionado.setFechaYHora(fecha);
            turnoSeleccionado.setEstado(estado);

            controlador.modificarTurno(turnoSeleccionado);
            actualizarListaTurnos();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Turno modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina el turno seleccionado de la base de datos previa confirmación
     * del usuario, interactuando con el controlador.
     */
    private void eliminarTurno() {
        if (turnoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Primero debe buscar un turno para poder eliminarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el turno #" + turnoSeleccionado.getIdTurno() + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                controlador.eliminarTurno(turnoSeleccionado.getIdTurno());
                actualizarListaTurnos();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Turno eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Carga los datos de un objeto Turno en los componentes visuales del formulario.
     * 
     * @param turno El objeto Turno cuyos datos se mostrarán en la interfaz.
     */
    private void cargarTurnoEnFormulario(Turno turno) {
        this.turnoSeleccionado = turno;
        txtFechaHora.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(turno.getFechaYHora()));
        cmbEstado.setSelectedItem(turno.getEstado());

        // Seleccionar el paciente y profesional en los JComboBox
        for (int i = 0; i < cmbPacientes.getItemCount(); i++) {
            if (cmbPacientes.getItemAt(i).getId() == turno.getPaciente().getId()) {
                cmbPacientes.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cmbProfesionales.getItemCount(); i++) {
            if (cmbProfesionales.getItemAt(i).getId() == turno.getProfesional().getId()) {
                cmbProfesionales.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Restablece todos los campos del formulario a su estado inicial y limpia 
     * la referencia al turno seleccionado.
     */
    private void limpiarCampos() {
        txtFechaHora.setText("");
        if (cmbPacientes.getItemCount() > 0) cmbPacientes.setSelectedIndex(0);
        if (cmbProfesionales.getItemCount() > 0) cmbProfesionales.setSelectedIndex(0);
        if (cmbEstado.getItemCount() > 0) cmbEstado.setSelectedIndex(0);
        this.turnoSeleccionado = null;
    }

    /**
     * Obtiene la lista actualizada de turnos desde la base de datos a través
     * del controlador y la muestra formateada en el JTextArea central.
     */
    private void actualizarListaTurnos() {
        areaTurnos.setText("");
        List<Turno> turnos = controlador.obtenerTodosLosTurnos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Turno turno : turnos) {
            String fechaFormateada = sdf.format(turno.getFechaYHora());
            
            String linea = String.format(
                "Turno #%d | %s | Paciente: %s %s | Prof: %s %s - %s | Estado: %s",
                turno.getIdTurno(),
                fechaFormateada,
                turno.getPaciente().getNombre(),
                turno.getPaciente().getApellido(),
                turno.getProfesional().getNombre(),
                turno.getProfesional().getApellido(),
                turno.getProfesional().getEspecialidad(),
                turno.getEstado()
            );
            areaTurnos.append(linea + "\n");
        }
    }
}
