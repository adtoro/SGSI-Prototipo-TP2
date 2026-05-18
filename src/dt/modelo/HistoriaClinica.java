package dt.modelo;
import java.util.Date;

public class HistoriaClinica {
    private Paciente paciente;
    private Profesional profesional;
    private Date fechaRegistro;
    private String observaciones;

    public HistoriaClinica(Paciente paciente, Profesional profesional, String observaciones) {
        this.paciente = paciente;
        this.profesional = profesional;
        this.fechaRegistro = new Date(); // Fecha actual automática
        this.observaciones = observaciones;
    }

    public String getEvolucion() {
        return "Fecha: " + fechaRegistro.toString() + 
               " | Prof: " + profesional.getDatosProfesional() + 
               " | Obs: " + observaciones;
    }
}