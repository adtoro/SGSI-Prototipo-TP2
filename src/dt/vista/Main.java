package dt.vista;

import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la aplicación.
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        // Asegura que la creación y manipulación de la GUI se realice en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MenuPrincipal menu = new MenuPrincipal();
                menu.setVisible(true);
            }
        });
    }
}
