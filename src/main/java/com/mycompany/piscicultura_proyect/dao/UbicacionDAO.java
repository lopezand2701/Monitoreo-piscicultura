package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Departamento;
import com.mycompany.piscicultura_proyect.modelo.Municipio;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UbicacionDAO {

    private final Connection conexion;

    public UbicacionDAO() {
        this.conexion = ConexionPostgres.getConexion();
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    // ------------------- DEPARTAMENTOS - EXTRAER TODOS -------------------
    public List<Departamento> extraerTodosDepartamentos() {
        List<Departamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM departamentos ORDER BY nombre";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setDepartamentoId(rs.getInt("departamento_id"));
                d.setNombre(rs.getString("nombre"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer todos los departamentos: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- DEPARTAMENTOS - EXTRAER POR ID -------------------
    public Departamento extraerDepartamentoPorId(int id) {
        String sql = "SELECT * FROM departamentos WHERE departamento_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Departamento d = new Departamento();
                d.setDepartamentoId(rs.getInt("departamento_id"));
                d.setNombre(rs.getString("nombre"));
                return d;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer departamento por ID: " + e.getMessage());
        }
        return null;
    }

    // ------------------- DEPARTAMENTOS - EXTRAER POR NOMBRE -------------------
    public List<Departamento> extraerDepartamentosPorNombre(String nombre) {
        List<Departamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM departamentos WHERE nombre ILIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setDepartamentoId(rs.getInt("departamento_id"));
                d.setNombre(rs.getString("nombre"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer departamentos por nombre: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- DEPARTAMENTOS - EXTRAER POR COLUMNA -------------------
    public List<Departamento> extraerDepartamentosPor(String columna, String valor) {
        List<Departamento> lista = new ArrayList<>();

        // Validar columnas permitidas
        List<String> columnasPermitidas = List.of("nombre");
        if (!columnasPermitidas.contains(columna)) {
            System.out.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT * FROM departamentos WHERE " + columna + " ILIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + valor + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setDepartamentoId(rs.getInt("departamento_id"));
                d.setNombre(rs.getString("nombre"));
                lista.add(d);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer departamentos por columna: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- MUNICIPIOS - EXTRAER TODOS -------------------
    public List<Municipio> extraerTodosMunicipios() {
        List<Municipio> lista = new ArrayList<>();
        String sql = "SELECT * FROM municipios ORDER BY nombre";
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Municipio m = new Municipio();
                m.setMunicipioId(rs.getInt("municipio_id"));
                m.setDepartamentoId(rs.getInt("departamento_id"));
                m.setNombre(rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer todos los municipios: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- MUNICIPIOS - EXTRAER POR ID -------------------
    public Municipio extraerMunicipioPorId(int id) {
        String sql = "SELECT * FROM municipios WHERE municipio_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Municipio m = new Municipio();
                m.setMunicipioId(rs.getInt("municipio_id"));
                m.setDepartamentoId(rs.getInt("departamento_id"));
                m.setNombre(rs.getString("nombre"));
                return m;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer municipio por ID: " + e.getMessage());
        }
        return null;
    }

    // ------------------- MUNICIPIOS - EXTRAER POR NOMBRE -------------------
    public List<Municipio> extraerMunicipiosPorNombre(String nombre) {
        List<Municipio> lista = new ArrayList<>();
        String sql = "SELECT * FROM municipios WHERE nombre ILIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Municipio m = new Municipio();
                m.setMunicipioId(rs.getInt("municipio_id"));
                m.setDepartamentoId(rs.getInt("departamento_id"));
                m.setNombre(rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer municipios por nombre: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- MUNICIPIOS - EXTRAER POR COLUMNA -------------------
    public List<Municipio> extraerMunicipiosPor(String columna, String valor) {
        List<Municipio> lista = new ArrayList<>();

        // Validar columnas permitidas
        List<String> columnasPermitidas = List.of("nombre", "departamento_id");
        if (!columnasPermitidas.contains(columna)) {
            System.out.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT * FROM municipios WHERE " + columna + " ILIKE ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (columna.equals("departamento_id")) {
                ps.setInt(1, Integer.parseInt(valor));
            } else {
                ps.setString(1, "%" + valor + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Municipio m = new Municipio();
                m.setMunicipioId(rs.getInt("municipio_id"));
                m.setDepartamentoId(rs.getInt("departamento_id"));
                m.setNombre(rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer municipios por columna: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Valor numérico inválido para columna " + columna + ": " + valor);
        }
        return lista;
    }

    // ------------------- MÉTODOS ESPECÍFICOS -------------------
    public List<Departamento> obtenerTodosDepartamentos() {
        return extraerTodosDepartamentos(); // Alias para mantener compatibilidad
    }

    public List<Municipio> obtenerMunicipiosPorDepartamento(int departamentoId) {
        return extraerMunicipiosPor("departamento_id", String.valueOf(departamentoId));
    }

    public Departamento obtenerDepartamentoPorId(int id) {
        return extraerDepartamentoPorId(id); // Alias para mantener compatibilidad
    }

    public Municipio obtenerMunicipioPorId(int id) {
        return extraerMunicipioPorId(id); // Alias para mantener compatibilidad
    }

    // ------------------- OBTENER NOMBRES -------------------
    public String obtenerNombreDepartamento(int departamentoId) {
        String sql = "SELECT nombre FROM departamentos WHERE departamento_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, departamentoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener nombre departamento: " + e.getMessage());
        }
        return "Desconocido";
    }

    public String obtenerNombreMunicipio(int municipioId) {
        String sql = "SELECT nombre FROM municipios WHERE municipio_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, municipioId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener nombre municipio: " + e.getMessage());
        }
        return "Desconocido";
    }
}