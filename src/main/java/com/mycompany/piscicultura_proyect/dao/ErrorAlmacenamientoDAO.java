package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.ConexionPostgres;
import java.sql.*;

public class ErrorAlmacenamientoDAO {
    public void registrar(String tipo, String mensaje) {
        String sql = "INSERT INTO errores_almacenamiento (tipo_error, mensaje) VALUES (?,?)";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, mensaje);
            ps.executeUpdate();
        } catch (Exception e) {
            // último recurso: imprimir en consola
            System.err.println("Fallo registrando error: " + e.getMessage());
        }
    }
}
