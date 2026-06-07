package dt.modelo;

import java.util.Date;

/**
 * Clase que representa una Historia Clínica.
 */
public class HistoriaClinica {
    private Paciente paciente;
    private Profesional profesional;
    private Date fechaRegistro;
    private String observaciones;

    /**
     * Constructor de la clase HistoriaClinica.
     * @param paciente El paciente de la historia clínica.
     * @param profesional El profesional que atiende.
     * @param fechaRegistro La fecha de registro.
     * @param observaciones Las observaciones médicas.
     */
    public HistoriaClinica(Paciente paciente, Profesional profesional, Date fechaRegistro, String observaciones) {
        this.paciente = paciente;
        this.profesional = profesional;
        this.fechaRegistro = fechaRegistro;
        this.observaciones = observaciones;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Profesional getProfesional() {
        return profesional;
    }

    public void setProfesional(Profesional profesional) {
        this.profesional = profesional;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
