package dt.modelo;

import java.util.Date;

/**
 * Clase que representa un Turno.
 */
public class Turno {
    private int idTurno;
    private Paciente paciente;
    private Profesional profesional;
    private Date fechaYHora;
    private String estado;

    /**
     * Constructor de la clase Turno.
     * @param idTurno El ID del turno.
     * @param paciente El paciente asignado al turno.
     * @param profesional El profesional que atenderá el turno.
     * @param fechaYHora La fecha y hora del turno.
     * @param estado El estado del turno (ej. Pendiente, Atendido, Cancelado).
     */
    public Turno(int idTurno, Paciente paciente, Profesional profesional, Date fechaYHora, String estado) {
        this.idTurno = idTurno;
        this.paciente = paciente;
        this.profesional = profesional;
        this.fechaYHora = fechaYHora;
        this.estado = estado;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
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

    public Date getFechaYHora() {
        return fechaYHora;
    }

    public void setFechaYHora(Date fechaYHora) {
        this.fechaYHora = fechaYHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Turno #" + idTurno + " - " + paciente.getNombre() + " " + paciente.getApellido() + " con " + profesional.getNombre() + " " + profesional.getApellido() + " (" + estado + ")";
    }
}
