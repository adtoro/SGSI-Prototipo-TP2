package dt.modelo;

/**
 * Clase que representa un Profesional.
 * Hereda de la clase Persona.
 */
public class Profesional extends Persona {
    private String matricula;
    private String especialidad;

    /**
     * Constructor de la clase Profesional.
     * @param id El id del profesional.
     * @param nombre El nombre del profesional.
     * @param apellido El apellido del profesional.
     * @param matricula La matrícula del profesional.
     * @param especialidad La especialidad del profesional.
     */
    public Profesional(int id, String nombre, String apellido, String matricula, String especialidad) {
        super(id, nombre, apellido);
        this.matricula = matricula;
        this.especialidad = especialidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    /**
     * Muestra los detalles del profesional.
     * @return Un String con los detalles del profesional.
     */
    @Override
    public String mostrarDetalles() {
        return "Dr/Dra. " + getNombre() + " " + getApellido() + " - " + especialidad + " - Matrícula: " + matricula;
    }
}
