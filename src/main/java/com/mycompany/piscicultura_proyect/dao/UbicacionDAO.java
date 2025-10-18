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

    // ------------------- OBTENER TODOS LOS DEPARTAMENTOS -------------------
    public List<Departamento> obtenerTodosDepartamentos() {
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
            System.out.println("❌ Error al obtener departamentos: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- OBTENER MUNICIPIOS POR DEPARTAMENTO -------------------
    public List<Municipio> obtenerMunicipiosPorDepartamento(int departamentoId) {
        List<Municipio> lista = new ArrayList<>();
        String sql = "SELECT * FROM municipios WHERE departamento_id = ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, departamentoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Municipio m = new Municipio();
                m.setMunicipioId(rs.getInt("municipio_id"));
                m.setDepartamentoId(rs.getInt("departamento_id"));
                m.setNombre(rs.getString("nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener municipios: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- OBTENER DEPARTAMENTO POR ID -------------------
    public Departamento obtenerDepartamentoPorId(int id) {
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
            System.out.println("❌ Error al obtener departamento: " + e.getMessage());
        }
        return null;
    }

    // ------------------- OBTENER MUNICIPIO POR ID -------------------
    public Municipio obtenerMunicipioPorId(int id) {
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
            System.out.println("❌ Error al obtener municipio: " + e.getMessage());
        }
        return null;
    }

    // ------------------- OBTENER NOMBRE DEPARTAMENTO -------------------
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

    // ------------------- OBTENER NOMBRE MUNICIPIO -------------------
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