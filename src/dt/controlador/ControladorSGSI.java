package dt.controlador;

import dt.modelo.Paciente;
import dt.modelo.Profesional;
import dt.modelo.Turno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador que maneja la lógica de negocio y la persistencia de datos
 * para el sistema SGSI, utilizando JDBC.
 */
public class ControladorSGSI {

    // --- Configuración de la Conexión JDBC ---
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/FREE";
    private static final String DB_USER = "sgsi";
    private static final String DB_PASSWORD = "admin1234";

    /**
     * Establece y devuelve una conexión a la base de datos.
     * 
     * @return Una conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error al intentar establecer la conexión.
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de Oracle JDBC. Verifica tu classpath.");
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Convierte los errores técnicos de SQL en mensajes amigables para el usuario.
     * 
     * @param e La excepción SQL capturada.
     * @return Un mensaje de error claro y comprensible.
     */
    public String formatearError(SQLException e) {
        String mensajeError = e.getMessage();
        if (mensajeError != null) {
            if (mensajeError.contains("ORA-00001")) {
                return "El dato ingresado ya existe en el sistema. Por favor, verifique el DNI o Matrícula.";
            } else if (mensajeError.contains("ORA-02291")) {
                return "No se pudo completar la operación. Verifique que el Paciente o Profesional exista y sea válido.";
            }
        }
        return "Ocurrió un error inesperado al conectar con la base de datos. Por favor, contacte a soporte.";
    }

    // --- MÉTODOS CRUD PARA PACIENTES ---

    /**
     * Inserta un nuevo paciente en la base de datos.
     * 
     * @param paciente El objeto Paciente que contiene los datos a persistir.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public void crearPaciente(Paciente paciente) throws SQLException {
        String sql = "INSERT INTO PACIENTES (ID_PACIENTE, DNI, NOMBRE, APELLIDO, TELEFONO, EMAIL) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paciente.getId());
            pstmt.setString(2, paciente.getDni());
            pstmt.setString(3, paciente.getNombre());
            pstmt.setString(4, paciente.getApellido());
            pstmt.setString(5, paciente.getTelefono());
            pstmt.setString(6, paciente.getEmail());
            pstmt.executeUpdate();
        }
    }

    /**
     * Recupera todos los pacientes registrados en la base de datos.
     * 
     * @return Una lista de objetos Paciente. Retorna una lista vacía si no hay registros o si ocurre un error.
     */
    public List<Paciente> obtenerTodosLosPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT ID_PACIENTE, DNI, NOMBRE, APELLIDO, TELEFONO, EMAIL FROM PACIENTES";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pacientes.add(new Paciente(
                        rs.getInt("ID_PACIENTE"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        rs.getString("DNI"),
                        rs.getString("TELEFONO"),
                        rs.getString("EMAIL")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pacientes;
    }

    /**
     * Busca un paciente específico en la base de datos utilizando su DNI.
     * 
     * @param dni El DNI del paciente a buscar.
     * @return Un Optional que contiene el objeto Paciente si se encuentra, o un Optional vacío en caso contrario.
     */
    public Optional<Paciente> obtenerPacientePorDni(String dni) {
        String sql = "SELECT ID_PACIENTE, DNI, NOMBRE, APELLIDO, TELEFONO, EMAIL FROM PACIENTES WHERE DNI = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Paciente(
                            rs.getInt("ID_PACIENTE"),
                            rs.getString("NOMBRE"),
                            rs.getString("APELLIDO"),
                            rs.getString("DNI"),
                            rs.getString("TELEFONO"),
                            rs.getString("EMAIL")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un paciente existente en la base de datos.
     * 
     * @param dniOriginal El DNI original del paciente, utilizado para identificar el registro a actualizar.
     * @param pacienteConNuevosDatos Un objeto Paciente que contiene los datos actualizados.
     * @throws SQLException Si ocurre un error durante la actualización en la base de datos.
     */
    public void modificarPaciente(String dniOriginal, Paciente pacienteConNuevosDatos) throws SQLException {
        String sql = "UPDATE PACIENTES SET NOMBRE = ?, APELLIDO = ?, DNI = ?, TELEFONO = ?, EMAIL = ? WHERE DNI = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pacienteConNuevosDatos.getNombre());
            pstmt.setString(2, pacienteConNuevosDatos.getApellido());
            pstmt.setString(3, pacienteConNuevosDatos.getDni());
            pstmt.setString(4, pacienteConNuevosDatos.getTelefono());
            pstmt.setString(5, pacienteConNuevosDatos.getEmail());
            pstmt.setString(6, dniOriginal);
            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina el registro de un paciente de la base de datos utilizando su DNI.
     * 
     * @param dni El DNI del paciente que se desea eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación en la base de datos.
     */
    public void eliminarPaciente(String dni) throws SQLException {
        String sql = "DELETE FROM PACIENTES WHERE DNI = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dni);
            pstmt.executeUpdate();
        }
    }

    // --- MÉTODOS CRUD PARA PROFESIONALES ---

    /**
     * Inserta un nuevo profesional en la base de datos.
     * 
     * @param profesional El objeto Profesional que contiene los datos a persistir.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public void crearProfesional(Profesional profesional) throws SQLException {
        String sql = "INSERT INTO PROFESIONALES (ID_PROFESIONAL, MATRICULA, NOMBRE, APELLIDO, ESPECIALIDAD) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profesional.getId());
            pstmt.setString(2, profesional.getMatricula());
            pstmt.setString(3, profesional.getNombre());
            pstmt.setString(4, profesional.getApellido());
            pstmt.setString(5, profesional.getEspecialidad());
            pstmt.executeUpdate();
        }
    }

    /**
     * Recupera todos los profesionales registrados en la base de datos.
     * 
     * @return Una lista de objetos Profesional. Retorna una lista vacía si no hay registros o si ocurre un error.
     */
    public List<Profesional> obtenerTodosLosProfesionales() {
        List<Profesional> profesionales = new ArrayList<>();
        String sql = "SELECT ID_PROFESIONAL, MATRICULA, NOMBRE, APELLIDO, ESPECIALIDAD FROM PROFESIONALES";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                profesionales.add(new Profesional(
                        rs.getInt("ID_PROFESIONAL"),
                        rs.getString("NOMBRE"),
                        rs.getString("APELLIDO"),
                        rs.getString("MATRICULA"),
                        rs.getString("ESPECIALIDAD")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profesionales;
    }

    /**
     * Busca un profesional específico en la base de datos utilizando su matrícula.
     * 
     * @param matricula La matrícula del profesional a buscar.
     * @return Un Optional que contiene el objeto Profesional si se encuentra, o un Optional vacío en caso contrario.
     */
    public Optional<Profesional> obtenerProfesionalPorMatricula(String matricula) {
        String sql = "SELECT ID_PROFESIONAL, MATRICULA, NOMBRE, APELLIDO, ESPECIALIDAD FROM PROFESIONALES WHERE MATRICULA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Profesional(
                            rs.getInt("ID_PROFESIONAL"),
                            rs.getString("NOMBRE"),
                            rs.getString("APELLIDO"),
                            rs.getString("MATRICULA"),
                            rs.getString("ESPECIALIDAD")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un profesional existente en la base de datos.
     * 
     * @param matricula La matrícula original del profesional, utilizada para identificar el registro a actualizar.
     * @param profesional Un objeto Profesional que contiene los datos actualizados.
     * @throws SQLException Si ocurre un error durante la actualización en la base de datos.
     */
    public void modificarProfesional(String matricula, Profesional profesional) throws SQLException {
        String sql = "UPDATE PROFESIONALES SET NOMBRE = ?, APELLIDO = ?, ESPECIALIDAD = ?, MATRICULA = ? WHERE MATRICULA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, profesional.getNombre());
            pstmt.setString(2, profesional.getApellido());
            pstmt.setString(3, profesional.getEspecialidad());
            pstmt.setString(4, profesional.getMatricula());
            pstmt.setString(5, matricula);
            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina el registro de un profesional de la base de datos utilizando su matrícula.
     * 
     * @param matricula La matrícula del profesional que se desea eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación en la base de datos.
     */
    public void eliminarProfesional(String matricula) throws SQLException {
        String sql = "DELETE FROM PROFESIONALES WHERE MATRICULA = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, matricula);
            pstmt.executeUpdate();
        }
    }
    
    // --- MÉTODOS CRUD PARA TURNOS ---

    /**
     * Inserta un nuevo turno en la base de datos.
     * 
     * @param turno El objeto Turno que contiene la información del turno a registrar.
     * @throws SQLException Si ocurre un error durante la inserción en la base de datos.
     */
    public void crearTurno(Turno turno) throws SQLException {
        String sql = "INSERT INTO TURNOS (ID_TURNO, ID_PACIENTE, ID_PROFESIONAL, FECHA_HORA, ESTADO) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, turno.getIdTurno());
            pstmt.setInt(2, turno.getPaciente().getId());
            pstmt.setInt(3, turno.getProfesional().getId());
            pstmt.setTimestamp(4, new Timestamp(turno.getFechaYHora().getTime()));
            pstmt.setString(5, turno.getEstado());
            pstmt.executeUpdate();
        }
    }

    /**
     * Recupera todos los turnos registrados en la base de datos, incluyendo la información 
     * completa de los pacientes y profesionales asociados mediante un JOIN.
     * 
     * @return Una lista de objetos Turno completamente poblados. Retorna una lista vacía si no hay registros o si ocurre un error.
     */
    public List<Turno> obtenerTodosLosTurnos() {
        List<Turno> turnos = new ArrayList<>();
        String sql = "SELECT t.ID_TURNO, t.FECHA_HORA, t.ESTADO, " +
                     "p.ID_PACIENTE, p.DNI, p.NOMBRE AS PAC_NOMBRE, p.APELLIDO AS PAC_APELLIDO, p.TELEFONO, p.EMAIL, " +
                     "pr.ID_PROFESIONAL, pr.MATRICULA, pr.NOMBRE AS PROF_NOMBRE, pr.APELLIDO AS PROF_APELLIDO, pr.ESPECIALIDAD " +
                     "FROM TURNOS t " +
                     "JOIN PACIENTES p ON t.ID_PACIENTE = p.ID_PACIENTE " +
                     "JOIN PROFESIONALES pr ON t.ID_PROFESIONAL = pr.ID_PROFESIONAL";
                     
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Paciente p = new Paciente(
                        rs.getInt("ID_PACIENTE"),
                        rs.getString("PAC_NOMBRE"),
                        rs.getString("PAC_APELLIDO"),
                        rs.getString("DNI"),
                        rs.getString("TELEFONO"),
                        rs.getString("EMAIL")
                );
                
                Profesional prof = new Profesional(
                        rs.getInt("ID_PROFESIONAL"),
                        rs.getString("PROF_NOMBRE"),
                        rs.getString("PROF_APELLIDO"),
                        rs.getString("MATRICULA"),
                        rs.getString("ESPECIALIDAD")
                );
                
                turnos.add(new Turno(
                        rs.getInt("ID_TURNO"),
                        p,
                        prof,
                        rs.getTimestamp("FECHA_HORA"),
                        rs.getString("ESTADO")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return turnos;
    }

    /**
     * Busca un turno específico en la base de datos utilizando su ID.
     * Recupera la información completa del paciente y profesional asociados mediante un JOIN.
     * 
     * @param idTurno El identificador único del turno a buscar.
     * @return Un Optional que contiene el objeto Turno si se encuentra, o un Optional vacío en caso contrario.
     */
    public Optional<Turno> obtenerTurnoPorId(int idTurno) {
        String sql = "SELECT t.ID_TURNO, t.FECHA_HORA, t.ESTADO, " +
                     "p.ID_PACIENTE, p.DNI, p.NOMBRE AS PAC_NOMBRE, p.APELLIDO AS PAC_APELLIDO, p.TELEFONO, p.EMAIL, " +
                     "pr.ID_PROFESIONAL, pr.MATRICULA, pr.NOMBRE AS PROF_NOMBRE, pr.APELLIDO AS PROF_APELLIDO, pr.ESPECIALIDAD " +
                     "FROM TURNOS t " +
                     "JOIN PACIENTES p ON t.ID_PACIENTE = p.ID_PACIENTE " +
                     "JOIN PROFESIONALES pr ON t.ID_PROFESIONAL = pr.ID_PROFESIONAL " +
                     "WHERE t.ID_TURNO = ?";
                     
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTurno);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Paciente p = new Paciente(
                            rs.getInt("ID_PACIENTE"),
                            rs.getString("PAC_NOMBRE"),
                            rs.getString("PAC_APELLIDO"),
                            rs.getString("DNI"),
                            rs.getString("TELEFONO"),
                            rs.getString("EMAIL")
                    );
                    
                    Profesional prof = new Profesional(
                            rs.getInt("ID_PROFESIONAL"),
                            rs.getString("PROF_NOMBRE"),
                            rs.getString("PROF_APELLIDO"),
                            rs.getString("MATRICULA"),
                            rs.getString("ESPECIALIDAD")
                    );

                    return Optional.of(new Turno(
                            rs.getInt("ID_TURNO"),
                            p,
                            prof,
                            rs.getTimestamp("FECHA_HORA"),
                            rs.getString("ESTADO")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un turno existente en la base de datos.
     * Permite modificar la fecha, el estado y las referencias al paciente o profesional.
     * 
     * @param turno El objeto Turno que contiene los datos actualizados y el ID del turno a modificar.
     * @throws SQLException Si ocurre un error durante la actualización en la base de datos.
     */
    public void modificarTurno(Turno turno) throws SQLException {
        String sql = "UPDATE TURNOS SET FECHA_HORA = ?, ESTADO = ?, ID_PACIENTE = ?, ID_PROFESIONAL = ? WHERE ID_TURNO = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(turno.getFechaYHora().getTime()));
            pstmt.setString(2, turno.getEstado());
            pstmt.setInt(3, turno.getPaciente().getId());
            pstmt.setInt(4, turno.getProfesional().getId());
            pstmt.setInt(5, turno.getIdTurno());
            pstmt.executeUpdate();
        }
    }

    /**
     * Elimina el registro de un turno de la base de datos utilizando su ID.
     * 
     * @param idTurno El identificador único del turno que se desea eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación en la base de datos.
     */
    public void eliminarTurno(int idTurno) throws SQLException {
        String sql = "DELETE FROM TURNOS WHERE ID_TURNO = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idTurno);
            pstmt.executeUpdate();
        }
    }

    // --- METODO PARA HISTORIA CLINICA ---

    /**
     * Guarda una nueva entrada de evolución en la historia clínica del paciente.
     * 
     * @param turno El turno asociado a la atención médica.
     * @param observaciones El texto detallado con las observaciones de la evolución clínica.
     * @throws SQLException Si ocurre un error al insertar el registro en la base de datos.
     */
    public void guardarHistoriaClinica(Turno turno, String observaciones) throws SQLException {
        // 1. Generamos el ID
        int idGenerado = (int) (System.currentTimeMillis() / 1000);

        // 2. Usamos '?' para TODOS los valores.
        // Ahora el primer '?' corresponde a idGenerado.
        String sql = "INSERT INTO HISTORIAS_CLINICAS (ID_HISTORIA, ID_PACIENTE, ID_PROFESIONAL, FECHA_REGISTRO, OBSERVACIONES) VALUES (?, ?, ?, SYSTIMESTAMP, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 3. Asignamos los valores en orden (1 al 4)
            pstmt.setInt(1, idGenerado);
            pstmt.setInt(2, turno.getPaciente().getId());
            pstmt.setInt(3, turno.getProfesional().getId());
            pstmt.setString(4, observaciones);

            pstmt.executeUpdate();
        }
    }
    
    /**
     * Consulta y recupera todas las entradas de la historia clínica asociadas a un paciente específico.
     * 
     * @param idPaciente El identificador único del paciente.
     * @return Una lista de cadenas de texto (String), donde cada cadena representa una entrada formateada de la historia clínica.
     * @throws SQLException Si ocurre un error al realizar la consulta en la base de datos.
     */
    public List<String> obtenerHistoriasPorPaciente(int idPaciente) throws SQLException {
        List<String> historias = new ArrayList<>();
        String sql = "SELECT h.FECHA_REGISTRO, h.OBSERVACIONES, p.NOMBRE, p.APELLIDO " +
                "FROM HISTORIAS_CLINICAS h " +
                "JOIN PACIENTES p ON h.ID_PACIENTE = p.ID_PACIENTE " +
                "WHERE h.ID_PACIENTE = ? ORDER BY h.FECHA_REGISTRO DESC";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPaciente);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                historias.add(rs.getString("FECHA_REGISTRO") + " | " +
                        rs.getString("NOMBRE") + " " + rs.getString("APELLIDO") +
                        ": " + rs.getString("OBSERVACIONES"));
            }
        }
        return historias;
    }
}