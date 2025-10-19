package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.modelo.Estacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstacionDAO {

    // ------------------- INSERTAR -------------------
    public boolean insertar(Estacion estacion, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede crear estaciones.");
            return false;
        }

        String sql = "INSERT INTO estaciones (usuario_id, nombre, ubicacion, departamento_id, municipio_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estacion.getUsuarioId());
            stmt.setString(2, estacion.getNombre());
            stmt.setString(3, estacion.getUbicacion());
            stmt.setInt(4, estacion.getDepartamentoId());
            stmt.setInt(5, estacion.getMunicipioId());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar estación: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean actualizar(Estacion estacion, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede editar estaciones.");
            return false;
        }

        String sql = "UPDATE estaciones " +
                "SET nombre = ?, ubicacion = ?, usuario_id = ?, departamento_id = ?, municipio_id = ? " +
                "WHERE estacion_id = ?";

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estacion.getNombre());
            stmt.setString(2, estacion.getUbicacion());
            stmt.setInt(3, estacion.getUsuarioId());
            stmt.setInt(4, estacion.getDepartamentoId());
            stmt.setInt(5, estacion.getMunicipioId());
            stmt.setInt(6, estacion.getEstacionId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar estación: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminar(int estacionId, int rol) {
        if (rol != 1) {
            System.out.println("⛔ No autorizado: solo el administrador puede eliminar estaciones.");
            return false;
        }

        String sql = "DELETE FROM estaciones WHERE estacion_id=?";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estacionId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar estación: " + e.getMessage());
            return false;
        }
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    // ------------------- EXTRAER TODOS -------------------
    public List<Estacion> extraerTodos() {
        List<Estacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM estaciones ORDER BY estacion_id";
        try (Connection conn = ConexionPostgres.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                e.setDepartamentoId(rs.getInt("departamento_id"));
                e.setMunicipioId(rs.getInt("municipio_id"));
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer todas las estaciones: " + ex.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR ID -------------------
    public Estacion extraerPorId(int id) {
        String sql = "SELECT * FROM estaciones WHERE estacion_id = ?";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                e.setDepartamentoId(rs.getInt("departamento_id"));
                e.setMunicipioId(rs.getInt("municipio_id"));
                return e;
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estación por ID: " + ex.getMessage());
        }

        return null;
    }

    // ------------------- EXTRAER POR NOMBRE -------------------
    public List<Estacion> extraerPorNombre(String nombre) {
        List<Estacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM estaciones WHERE nombre ILIKE ? ORDER BY nombre";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                e.setDepartamentoId(rs.getInt("departamento_id"));
                e.setMunicipioId(rs.getInt("municipio_id"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estaciones por nombre: " + ex.getMessage());
        }

        return lista;
    }

    // ------------------- EXTRAER POR COLUMNA -------------------
    public List<Estacion> extraerPor(String columna, String valor) {
        List<Estacion> lista = new ArrayList<>();

        // Validar columnas permitidas
        List<String> columnasPermitidas = List.of("nombre", "ubicacion", "usuario_id", "departamento_id", "municipio_id");
        if (!columnasPermitidas.contains(columna)) {
            System.err.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT * FROM estaciones WHERE " + columna + " ILIKE ? ORDER BY estacion_id";
        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Manejar diferentes tipos de datos
            if (columna.equals("usuario_id") || columna.equals("departamento_id") || columna.equals("municipio_id")) {
                ps.setInt(1, Integer.parseInt(valor));
            } else {
                ps.setString(1, "%" + valor + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                e.setDepartamentoId(rs.getInt("departamento_id"));
                e.setMunicipioId(rs.getInt("municipio_id"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estaciones por columna: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.err.println("❌ Valor numérico inválido para columna " + columna + ": " + valor);
        }

        return lista;
    }

    // ------------------- MÉTODOS ESPECÍFICOS -------------------
    public List<Estacion> listarPorUsuario(int usuarioId, int rol) {
        List<Estacion> lista = new ArrayList<>();
        String sql;

        if (rol == 1) {
            sql = "SELECT * FROM estaciones ORDER BY estacion_id";
        } else {
            sql = "SELECT * FROM estaciones WHERE usuario_id = ? ORDER BY estacion_id";
        }

        try (Connection conn = ConexionPostgres.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (rol != 1) {
                ps.setInt(1, usuarioId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Estacion e = new Estacion();
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setUsuarioId(rs.getInt("usuario_id"));
                e.setNombre(rs.getString("nombre"));
                e.setUbicacion(rs.getString("ubicacion"));
                e.setCreadoEn(rs.getString("creado_en"));
                e.setDepartamentoId(rs.getInt("departamento_id"));
                e.setMunicipioId(rs.getInt("municipio_id"));
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al listar estaciones: " + ex.getMessage());
        }

        return lista;
    }

    public Estacion obtenerPorId(int estacionId) {
        return extraerPorId(estacionId); // Alias para mantener compatibilidad
    }
}