package dt.modelo;

/**
 * Clase abstracta que representa una Persona.
 */
public abstract class Persona {
    private int id;
    private String nombre;
    private String apellido;

    /**
     * Constructor de la clase Persona.
     * @param id El id de la persona.
     * @param nombre El nombre de la persona.
     * @param apellido El apellido de la persona.
     */
    public Persona(int id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Método abstracto para mostrar los detalles de la persona.
     * @return Un String con los detalles de la persona.
     */
    public abstract String mostrarDetalles();
}
