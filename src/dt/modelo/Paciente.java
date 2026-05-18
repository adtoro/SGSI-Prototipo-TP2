package dt.modelo;

public class Paciente {
    private String dni;
    private String nombre;
    private String apellido;

    public Paciente(String dni, String nombre, String apellido) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String getDni() { return dni; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}