package dt.vista;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521/FREE";
        String usuario = "sgsi";
        String password = "pass123";

        String insertQuery = "INSERT INTO historias_clinicas (id_paciente, id_profesional, fecha_registro, observaciones) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

        String selectQuery = "SELECT p.nombre || ' ' || p.apellido AS paciente, prof.especialidad, hc.fecha_registro, hc.observaciones " +
                "FROM historias_clinicas hc " +
                "JOIN pacientes p ON hc.id_paciente = p.id_paciente " +
                "JOIN profesionales prof ON hc.id_profesional = prof.id_profesional " +
                "WHERE p.id_paciente = ?";

        try (Connection conn = DriverManager.getConnection(url, usuario, password)) {
            
            // 1. Insertar registro usando PreparedStatement
            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertQuery)) {
                pstmtInsert.setInt(1, 1);
                pstmtInsert.setInt(2, 1);
                pstmtInsert.setString(3, "Paciente reporta mejoría del cuadro febril. Se suspende medicación.");
                
                int filasAfectadas = pstmtInsert.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("EXITO: Evolución registrada en Base de Datos Oracle.");
                }
            }

            // 2. Consultar historial
            try (PreparedStatement pstmtSelect = conn.prepareStatement(selectQuery)) {
                pstmtSelect.setInt(1, 1);
                
                try (ResultSet rs = pstmtSelect.executeQuery()) {
                    System.out.println("\n--- Historial del Paciente ---");
                    while (rs.next()) {
                        // Usamos exactamente los nombres que pusimos en el SELECT de arriba
                        String paciente = rs.getString("paciente");
                        String especialidad = rs.getString("especialidad");
                        String fecha = rs.getTimestamp("fecha_registro").toString();
                        String observacion = rs.getString("observaciones");

                        System.out.println("Fecha: " + fecha);
                        System.out.println("Paciente: " + paciente);
                        System.out.println("Especialidad: " + especialidad);
                        System.out.println("Evolución: " + observacion);
                        System.out.println("--------------------------------------");
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en la conexión a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}