package dt.vista;

import dt.controlador.ControladorSGSI;
import dt.modelo.Paciente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Pantalla para la gestión ABM (CRUD) completa de pacientes.
 * Ahora interactúa con la base de datos a través de un controlador MVC.
 */
public class PantallaGestionPacientes extends JFrame {

    private final ControladorSGSI controlador;

    private final JTextField txtDni;
    private final JTextField txtNombre;
    private final JTextField txtApellido;
    private final JTextArea areaPacientes;
    private String dniOriginalParaModificacion;

    /**
     * Constructor que inicializa la interfaz y la lógica de la pantalla.
     */
    public PantallaGestionPacientes() {
        this.controlador = new ControladorSGSI();

        // Configuración de la ventana
        setTitle("Gestión de Pacientes (ABM)");
        setSize(600, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Formulario y Botones ---
        JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Datos del Paciente"));

        // Formulario de entrada
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 5));
        panelFormulario.add(new JLabel("DNI:"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panelFormulario.add(txtApellido);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnGuardar = new JButton("Guardar Nuevo");
        JButton btnBuscar = new JButton("Buscar por DNI");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnBuscar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel Central: Área de Texto para Listado ---
        areaPacientes = new JTextArea();
        areaPacientes.setEditable(false);
        areaPacientes.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(areaPacientes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pacientes Registrados"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Lógica de los Botones ---

        // 1. Guardar (Alta)
        btnGuardar.addActionListener(e -> {
            try {
                String dni = txtDni.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();

                if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()) {
                    throw new Exception("Todos los campos son obligatorios.");
                }
                if (controlador.obtenerPacientePorDni(dni).isPresent()) {
                    throw new Exception("Ya existe un paciente con el DNI " + dni);
                }

                // Generar un ID simple para el paciente
                int id = (int) (System.currentTimeMillis() % 100000);
                Paciente nuevoPaciente = new Paciente(id, nombre, apellido, dni);
                
                controlador.crearPaciente(nuevoPaciente);

                limpiarCampos();
                actualizarListaPacientes();
                JOptionPane.showMessageDialog(this, "Paciente guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 2. Buscar (Consulta)
        btnBuscar.addActionListener(e -> {
            String dniBusqueda = JOptionPane.showInputDialog(this, "Ingrese el DNI del paciente a buscar:", "Buscar Paciente", JOptionPane.QUESTION_MESSAGE);
            if (dniBusqueda != null && !dniBusqueda.trim().isEmpty()) {
                controlador.obtenerPacientePorDni(dniBusqueda.trim())
                        .ifPresentOrElse(
                                paciente -> {
                                    txtDni.setText(paciente.getDni());
                                    txtNombre.setText(paciente.getNombre());
                                    txtApellido.setText(paciente.getApellido());
                                    dniOriginalParaModificacion = paciente.getDni(); // Guardar para futura modificación
                                },
                                () -> JOptionPane.showMessageDialog(this, "No se encontró ningún paciente con el DNI " + dniBusqueda, "Error", JOptionPane.ERROR_MESSAGE)
                        );
            }
        });

        // 3. Modificar
        btnModificar.addActionListener(e -> {
            try {
                String dni = txtDni.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();

                if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || dniOriginalParaModificacion == null) {
                    throw new Exception("Busque un paciente o complete todos los campos para modificar.");
                }

                Paciente pacienteAModificar = new Paciente(0, nombre, apellido, dni); // ID irrelevante aquí
                controlador.modificarPaciente(dniOriginalParaModificacion, pacienteAModificar);

                actualizarListaPacientes();
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Paciente modificado con éxito.", "Modificación Exitosa", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Modificación", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 4. Eliminar (Baja)
        btnEliminar.addActionListener(e -> {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Busque un paciente o ingrese un DNI para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar si el paciente existe antes de pedir confirmación
            controlador.obtenerPacientePorDni(dni).ifPresentOrElse(
                    paciente -> {
                        int confirmacion = JOptionPane.showConfirmDialog(this,
                                "¿Está seguro de que desea eliminar al paciente:\n" + paciente.mostrarDetalles() + "?",
                                "Confirmar Eliminación",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);

                        if (confirmacion == JOptionPane.YES_OPTION) {
                            try {
                                controlador.eliminarPaciente(dni);
                                actualizarListaPacientes();
                                limpiarCampos();
                                JOptionPane.showMessageDialog(this, "Paciente eliminado con éxito.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(this, controlador.formatearError(ex), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    },
                    () -> JOptionPane.showMessageDialog(this, "No se encontró un paciente con el DNI " + dni, "Error", JOptionPane.ERROR_MESSAGE)
            );
        });
        
        // Cargar la lista al iniciar
        actualizarListaPacientes();
    }

    /**
     * Limpia los campos de texto del formulario.
     */
    private void limpiarCampos() {
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        dniOriginalParaModificacion = null;
    }

    /**
     * Actualiza el JTextArea con los datos obtenidos del controlador.
     */
    private void actualizarListaPacientes() {
        areaPacientes.setText("");
        List<Paciente> pacientes = controlador.obtenerTodosLosPacientes();
        for (Paciente paciente : pacientes) {
            areaPacientes.append(paciente.mostrarDetalles() + "\n");
        }
    }
}
