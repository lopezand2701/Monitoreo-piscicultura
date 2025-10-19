package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Especie;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecieDAO {

    private final Connection conexion;

    public EspecieDAO() {
        this.conexion = ConexionPostgres.getConexion();
    }

    // ------------------- INSERTAR -------------------
    public boolean insertarEspecie(Especie especie) {
        String sql = "INSERT INTO especies (nombre_cientifico, nombre_comun, descripcion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, especie.getNombreCientifico());
            ps.setString(2, especie.getNombreComun());
            ps.setString(3, especie.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar especie: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean actualizarEspecie(Especie especie) {
        String sql = "UPDATE especies SET nombre_cientifico=?, nombre_comun=?, descripcion=? WHERE especie_id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, especie.getNombreCientifico());
            ps.setString(2, especie.getNombreComun());
            ps.setString(3, especie.getDescripcion());
            ps.setInt(4, especie.getEspecieId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar especie: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminarEspecie(int id) {
        String sql = "DELETE FROM especies WHERE especie_id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar especie: " + e.getMessage());
            return false;
        }
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    // ------------------- EXTRAER TODOS -------------------
    public List<Especie> extraerTodos() {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT * FROM especies ORDER BY nombre_comun";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Especie e = new Especie();
                e.setEspecieId(rs.getInt("especie_id"));
                e.setNombreCientifico(rs.getString("nombre_cientifico"));
                e.setNombreComun(rs.getString("nombre_comun"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer todas las especies: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR ID -------------------
    public Especie extraerPorId(int id) {
        String sql = "SELECT * FROM especies WHERE especie_id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Especie e = new Especie();
                e.setEspecieId(rs.getInt("especie_id"));
                e.setNombreCientifico(rs.getString("nombre_cientifico"));
                e.setNombreComun(rs.getString("nombre_comun"));
                e.setDescripcion(rs.getString("descripcion"));
                return e;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer especie por ID: " + e.getMessage());
        }
        return null;
    }

    // ------------------- EXTRAER POR NOMBRE -------------------
    public List<Especie> extraerPorNombre(String nombre) {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT * FROM especies WHERE nombre_comun ILIKE ? OR nombre_cientifico ILIKE ? ORDER BY nombre_comun";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ps.setString(2, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Especie e = new Especie();
                e.setEspecieId(rs.getInt("especie_id"));
                e.setNombreCientifico(rs.getString("nombre_cientifico"));
                e.setNombreComun(rs.getString("nombre_comun"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer especies por nombre: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR COLUMNA -------------------
    public List<Especie> extraerPor(String columna, String valor) {
        List<Especie> lista = new ArrayList<>();

        // Validar columnas permitidas para prevenir SQL injection
        List<String> columnasPermitidas = List.of("nombre_cientifico", "nombre_comun", "descripcion");
        if (!columnasPermitidas.contains(columna)) {
            System.out.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT * FROM especies WHERE " + columna + " ILIKE ? ORDER BY nombre_comun";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + valor + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Especie e = new Especie();
                e.setEspecieId(rs.getInt("especie_id"));
                e.setNombreCientifico(rs.getString("nombre_cientifico"));
                e.setNombreComun(rs.getString("nombre_comun"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer especies por columna: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- MÉTODOS ESPECÍFICOS -------------------
    public List<Especie> obtenerTodasEspecies() {
        return extraerTodos(); // Alias para mantener compatibilidad
    }

    public Especie obtenerEspeciePorId(int id) {
        return extraerPorId(id); // Alias para mantener compatibilidad
    }

    // ------------------- ASIGNAR ESPECIE A ESTANQUE -------------------
    public boolean asignarEspecieAEstanque(int estanqueId, int especieId, int cantidad) {
        String sql = "INSERT INTO estanque_especies (estanque_id, especie_id, cantidad) VALUES (?, ?, ?) " +
                "ON CONFLICT (estanque_id, especie_id) DO UPDATE SET cantidad = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ps.setInt(2, especieId);
            ps.setInt(3, cantidad);
            ps.setInt(4, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al asignar especie a estanque: " + e.getMessage());
            return false;
        }
    }

    // ------------------- OBTENER ESPECIES DE UN ESTANQUE -------------------
    public List<Especie> obtenerEspeciesPorEstanque(int estanqueId) {
        List<Especie> lista = new ArrayList<>();
        String sql = "SELECT e.*, ee.cantidad " +
                "FROM especies e " +
                "JOIN estanque_especies ee ON e.especie_id = ee.especie_id " +
                "WHERE ee.estanque_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Especie e = new Especie();
                e.setEspecieId(rs.getInt("especie_id"));
                e.setNombreCientifico(rs.getString("nombre_cientifico"));
                e.setNombreComun(rs.getString("nombre_comun"));
                e.setDescripcion(rs.getString("descripcion"));
                lista.add(e);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener especies del estanque: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- OBTENER ESPECIES CON CANTIDAD -------------------
    public String obtenerEspeciesConCantidad(int estanqueId) {
        StringBuilder especies = new StringBuilder();
        String sql = "SELECT e.nombre_comun, ee.cantidad " +
                "FROM especies e " +
                "JOIN estanque_especies ee ON e.especie_id = ee.especie_id " +
                "WHERE ee.estanque_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (especies.length() > 0) {
                    especies.append(", ");
                }
                especies.append(rs.getString("nombre_comun"))
                        .append(" (")
                        .append(rs.getInt("cantidad"))
                        .append(")");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener especies con cantidad: " + e.getMessage());
        }
        return especies.length() > 0 ? especies.toString() : "Sin especies";
    }
}