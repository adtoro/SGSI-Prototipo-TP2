package dt.modelo;

/**
 * Clase que representa un Paciente.
 * Hereda de la clase Persona.
 */
public class Paciente extends Persona {
    private String dni;
    private String telefono;
    private String email;

    /**
     * Constructor de la clase Paciente (básico).
     * @param id El id del paciente (ID_PACIENTE).
     * @param nombre El nombre del paciente.
     * @param apellido El apellido del paciente.
     * @param dni El DNI del paciente.
     */
    public Paciente(int id, String nombre, String apellido, String dni) {
        super(id, nombre, apellido);
        this.dni = dni;
    }

    /**
     * Constructor de la clase Paciente (completo).
     * @param id El id del paciente (ID_PACIENTE).
     * @param nombre El nombre del paciente.
     * @param apellido El apellido del paciente.
     * @param dni El DNI del paciente.
     * @param telefono El teléfono del paciente.
     * @param email El email del paciente.
     */
    public Paciente(int id, String nombre, String apellido, String dni, String telefono, String email) {
        super(id, nombre, apellido);
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Muestra los detalles del paciente.
     * @return Un String con los detalles del paciente.
     */
    @Override
    public String mostrarDetalles() {
        return "Paciente: " + getNombre() + " " + getApellido() + " - DNI: " + dni;
    }
}
