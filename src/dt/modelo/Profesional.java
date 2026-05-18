package dt.modelo;

public class Profesional {
    private String matricula;
    private String nombre;
    private String especialidad;

    public Profesional(String matricula, String nombre, String especialidad) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public String getMatricula() { return matricula; }
    public String getDatosProfesional() { return nombre + " (" + especialidad + ")"; }
}