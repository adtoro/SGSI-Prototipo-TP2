package dt.controlador;
import dt.modelo.Paciente;
import dt.modelo.Profesional;
import dt.modelo.HistoriaClinica;

public class ControladorHC {
    
    // Método que simula la lógica de negocio y persistencia
    public boolean registrarNuevaEvolucion(Paciente pac, Profesional prof, String obs) {
        if (pac != null && prof != null && !obs.isEmpty()) {
            HistoriaClinica nuevaHc = new HistoriaClinica(pac, prof, obs);
            
            // Aquí iría la conexión JDBC a MySQL: insert(nuevaHc)
            System.out.println(">>> ÉXITO: Evolución registrada en Base de Datos MySQL.");
            System.out.println("Paciente: " + pac.getNombreCompleto());
            System.out.println("Detalle: " + nuevaHc.getEvolucion());
            return true;
        } else {
            System.out.println(">>> ERROR: Faltan datos para registrar la evolución.");
            return false;
        }
    }
}