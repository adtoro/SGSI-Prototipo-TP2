package dt.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal del menú del sistema SGSI.
 */
public class MenuPrincipal extends JFrame {

    /**
     * Constructor que inicializa la ventana y sus componentes.
     */
    public MenuPrincipal() {
        // Configuración de la ventana
        setTitle("SGSI - Centro de Salud");
        setSize(400, 450); // Ajustamos el tamaño
        setLocationRelativeTo(null); // Centrar en pantalla
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 15, 15));

        // Título
        JLabel lblTitulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitulo);

        // Botones
        JButton btnGestionPacientes = new JButton("Gestión de Pacientes");
        JButton btnGestionProfesionales = new JButton("Gestión de Profesionales");
        JButton btnAgendaTurnos = new JButton("Agenda de Turnos");
        JButton btnGestionHistoria = new JButton("Gestionar Historia Clínica");
        JButton btnSalir = new JButton("Salir del Sistema");

        add(btnGestionPacientes);
        add(btnGestionProfesionales);
        add(btnAgendaTurnos);
        add(btnGestionHistoria);
        add(btnSalir);

        // Action Listeners
        btnGestionPacientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre la nueva pantalla de gestión
                PantallaGestionPacientes pantallaPacientes = new PantallaGestionPacientes();
                pantallaPacientes.setVisible(true);
            }
        });

        btnGestionProfesionales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // REEMPLAZO: Se instancia y muestra la nueva pantalla de gestión
                PantallaGestionProfesionales pantallaProfesionales = new PantallaGestionProfesionales();
                pantallaProfesionales.setVisible(true);
            }
        });

        btnAgendaTurnos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abre la nueva pantalla de gestión de turnos
                PantallaGestionTurnos pantallaTurnos = new PantallaGestionTurnos();
                pantallaTurnos.setVisible(true);
            }
        });

        btnGestionHistoria.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PantallaHistoriaClinica().setVisible(true);
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirmacion = JOptionPane.showConfirmDialog(MenuPrincipal.this,
                        "¿Está seguro de que desea salir del sistema?",
                        "Confirmar Salida",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirmacion == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}
