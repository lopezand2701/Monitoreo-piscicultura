package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Estanque;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstanqueDAO {

    private Connection conexion;

    public EstanqueDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // ------------------- INSERTAR -------------------
    public boolean insertarEstanque(Estanque e) {
        String sql = "INSERT INTO estanques (estacion_id, nombre, volumen_m3, descripcion, creado_en) " +
                "VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, e.getEstacionId());
            ps.setString(2, e.getNombre());
            ps.setDouble(3, e.getVolumenM3());
            ps.setString(4, e.getDescripcion());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("❌ Error al insertar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean actualizarEstanque(Estanque e, int usuarioId, String rol) {
        if (!"admin".equals(rol)) {
            System.err.println("⚠️ Acceso denegado: solo los administradores pueden actualizar estanques.");
            return false;
        }

        if (e.getEstacionId() <= 0) {
            System.err.println("⚠️ El ID de estación no es válido: " + e.getEstacionId());
            return false;
        }

        String sql = "UPDATE estanques " +
                "SET estacion_id = ?, nombre = ?, volumen_m3 = ?, descripcion = ? " +
                "WHERE estanque_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, e.getEstacionId());
            ps.setString(2, e.getNombre());
            ps.setDouble(3, e.getVolumenM3());
            ps.setString(4, e.getDescripcion());
            ps.setInt(5, e.getEstanqueId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("✅ Estanque actualizado correctamente.");
                return true;
            } else {
                System.err.println("⚠️ No se encontró el estanque con ID: " + e.getEstanqueId());
                return false;
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al actualizar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminarEstanque(int id, int usuarioId, String rol) {
        String sql;

        if ("admin".equals(rol)) {
            sql = "DELETE FROM estanques WHERE estanque_id = ?";
        } else {
            sql = "DELETE FROM estanques " +
                    "WHERE estanque_id = ? " +
                    "AND estacion_id IN ( " +
                    "    SELECT estacion_id FROM estaciones WHERE usuario_id = ? " +
                    ")";
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!"admin".equals(rol)) ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("❌ Error al eliminar estanque: " + ex.getMessage());
            return false;
        }
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    // ------------------- EXTRAER TODOS -------------------
    public List<Estanque> extraerTodos() {
        List<Estanque> lista = new ArrayList<>();
        String sql = "SELECT * FROM estanques ORDER BY estanque_id";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                e.setCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer todos los estanques: " + ex.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR ID -------------------
    public Estanque extraerPorId(int id) {
        String sql = "SELECT * FROM estanques WHERE estanque_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                e.setCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
                return e;
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estanque por ID: " + ex.getMessage());
        }
        return null;
    }

    // ------------------- EXTRAER POR NOMBRE -------------------
    public List<Estanque> extraerPorNombre(String nombre) {
        List<Estanque> lista = new ArrayList<>();
        String sql = "SELECT * FROM estanques WHERE nombre ILIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                e.setCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estanques por nombre: " + ex.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR COLUMNA -------------------
    public List<Estanque> extraerPor(String columna, String valor) {
        List<Estanque> lista = new ArrayList<>();

        // Validar columnas permitidas
        List<String> columnasPermitidas = List.of("nombre", "descripcion", "estacion_id");
        if (!columnasPermitidas.contains(columna)) {
            System.err.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT * FROM estanques WHERE " + columna + " ILIKE ? ORDER BY estanque_id";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (columna.equals("estacion_id")) {
                ps.setInt(1, Integer.parseInt(valor));
            } else {
                ps.setString(1, "%" + valor + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                e.setCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
                lista.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error al extraer estanques por columna: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.err.println("❌ Valor numérico inválido para columna " + columna + ": " + valor);
        }
        return lista;
    }

    // ------------------- MÉTODOS ESPECÍFICOS -------------------
    public List<Estanque> listarPorUsuario(int usuarioId, String rol) {
        List<Estanque> lista = new ArrayList<>();
        String sql;

        if ("admin".equals(rol)) {
            sql = "SELECT e.*, es.usuario_id " +
                    "FROM estanques e " +
                    "JOIN estaciones es ON e.estacion_id = es.estacion_id " +
                    "ORDER BY e.estanque_id";
        } else {
            sql = "SELECT e.*, es.usuario_id " +
                    "FROM estanques e " +
                    "JOIN estaciones es ON e.estacion_id = es.estacion_id " +
                    "WHERE es.usuario_id = ? " +
                    "ORDER BY e.estanque_id";
        }

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (!"admin".equals(rol)) ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Estanque e = new Estanque();
                e.setEstanqueId(rs.getInt("estanque_id"));
                e.setEstacionId(rs.getInt("estacion_id"));
                e.setNombre(rs.getString("nombre"));
                e.setVolumenM3(rs.getDouble("volumen_m3"));
                e.setDescripcion(rs.getString("descripcion"));
                e.setCreadoEn(rs.getTimestamp("creado_en").toLocalDateTime());
                lista.add(e);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error al obtener estanques: " + ex.getMessage());
        }

        return lista;
    }
}