package com.mycompany.piscicultura_proyect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionPostgres {

    private static final String URL = "jdbc:postgresql://localhost:5432/psicultura"; // Cambia "piscicultura" por el nombre de tu BD
    private static final String USER = "postgres"; // Tu usuario de PostgreSQL
    private static final String PASSWORD = "postgres"; // Tu contraseña

    public static Connection getConexion() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa a PostgreSQL");
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar con PostgreSQL: " + e.getMessage());
        }
        return conn;
    }
}

