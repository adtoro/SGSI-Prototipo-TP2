package dt.vista;

import dt.controlador.ControladorSGSI;
import dt.modelo.Profesional;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Pantalla para la gestión ABM (CRUD) completa de profesionales médicos.
 */
public class PantallaGestionProfesionales extends JFrame {

    private final ControladorSGSI controlador;

    private final JTextField txtMatricula;
    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextField txtEspecialidad;
    private final JTextArea areaProfesionales;
    private String matriculaOriginalParaModificacion;

    /**
     * Constructor que inicializa la interfaz y la lógica de la pantalla.
     */
    public PantallaGestionProfesionales() {
        this.controlador = new ControladorSGSI();

        // Configuración de la ventana
        setTitle("Gestión de Profesionales (ABM)");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Formulario y Botones ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos del Profesional"));

        // Formulario de entrada
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormulario.add(new JLabel("Matrícula:"));
        txtMatricula = new JTextField();
        panelFormulario.add(txtMatricula);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelFormulario.add(txtApellido);

        panelFormulario.add(new JLabel("Especialidad:"));
        txtEspecialidad = new JTextField();
        panelFormulario.add(txtEspecialidad);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnGuardar = new JButton("Guardar Nuevo");
        JButton btnBuscar = new JButton("Buscar por Matrícula");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel Central: Área de Texto para Listado ---
        areaProfesionales = new JTextArea();
        areaProfesionales.setEditable(false);
        areaProfesionales.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaProfesionales);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Profesionales Registrados en DB"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Lógica de los Botones ---

        btnGuardar.addActionListener(e -> {
            try {
                String matricula = txtMatricula.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String especialidad = txtEspecialidad.getText().trim();

                if (matricula.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || especialidad.isEmpty()) {
                    throw new Exception("Todos los campos son obligatorios.");
                }
                if (controlador.obtenerProfesionalPorMatricula(matricula).isPresent()) {
                    throw new Exception("Ya existe un profesional con la matrícula " + matricula);
                }

                int id = (int) (System.currentTimeMillis() % 100000);
                Profesional nuevoProfesional = new Profesional(id, nombre, apellido, matricula, especialidad);
                controlador.crearProfesional(nuevoProfesional);

                limpiarCampos();
                actualizarListaProfesionales();
                JOptionPane.showMessageDialog(this, "Profesional guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBuscar.addActionListener(e -> {
            String matriculaBusqueda = JOptionPane.showInputDialog(this, "Ingrese la matrícula del profesional a buscar:", "Buscar Profesional", JOptionPane.QUESTION_MESSAGE);
            if (matriculaBusqueda != null && !matriculaBusqueda.trim().isEmpty()) {
                controlador.obtenerProfesionalPorMatricula(matriculaBusqueda.trim())
                        .ifPresentOrElse(
                                this::cargarProfesionalEnFormulario,
                                () -> JOptionPane.showMessageDialog(this, "No se encontró ningún profesional con la matrícula " + matriculaBusqueda, "Error", JOptionPane.ERROR_MESSAGE)
                        );
            }
        });

        btnModificar.addActionListener(e -> {
            try {
                String matricula = txtMatricula.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String especialidad = txtEspecialidad.getText().trim();

                if (matricula.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || especialidad.isEmpty() || matriculaOriginalParaModificacion == null) {
                    throw new Exception("Debe buscar un profesional antes de poder modificarlo.");
                }

                Profesional profesionalModificado = new Profesional(0, nombre, apellido, matricula, especialidad);
                controlador.modificarProfesional(matriculaOriginalParaModificacion, profesionalModificado);

                actualizarListaProfesionales();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Profesional modificado con éxito.", "Modificación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Modificación", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            String matricula = txtMatricula.getText().trim();
            if (matricula.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Busque un profesional o ingrese una matrícula para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Profesional> profesionalOpt = controlador.obtenerProfesionalPorMatricula(matricula);
            if (profesionalOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un profesional con la matrícula " + matricula, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Profesional profesionalAEliminar = profesionalOpt.get();
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar al profesional:\n" + profesionalAEliminar.mostrarDetalles() + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    controlador.eliminarProfesional(matricula);
                    actualizarListaProfesionales();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "Profesional eliminado con éxito.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actualizarListaProfesionales();
    }

    private void cargarProfesionalEnFormulario(Profesional profesional) {
        txtMatricula.setText(profesional.getMatricula());
        txtNombre.setText(profesional.getNombre());
        txtApellido.setText(profesional.getApellido());
        txtEspecialidad.setText(profesional.getEspecialidad());
        this.matriculaOriginalParaModificacion = profesional.getMatricula();
    }

    private void limpiarCampos() {
        txtMatricula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEspecialidad.setText("");
        this.matriculaOriginalParaModificacion = null;
    }

    private void actualizarListaProfesionales() {
        areaProfesionales.setText("");
        List<Profesional> profesionales = controlador.obtenerTodosLosProfesionales();
        for (Profesional profesional : profesionales) {
            areaProfesionales.append(profesional.mostrarDetalles() + "\n");
        }
    }
}
