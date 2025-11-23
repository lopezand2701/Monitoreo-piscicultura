package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.ConexionPostgres;
import com.mycompany.piscicultura_proyect.modelo.Reportes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportesDAO {

    public int guardar(Reportes reporte) throws Exception {
        String sql = "INSERT INTO reportes (sensor_id, estanque_id, titulo, descripcion, creado_en) VALUES (?, ?, ?, ?, NOW()) RETURNING reporte_id";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (reporte.getSensorId() != null) ps.setInt(1, reporte.getSensorId()); else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, reporte.getEstanqueId());
            ps.setString(3, reporte.getTitulo());
            ps.setString(4, reporte.getDescripcion());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    reporte.setReporteId(id);
                    return id;
                }
            }
        }
        throw new Exception("No se pudo insertar el reporte");
    }

    public Reportes obtenerPorId(int id) throws Exception {
        String sql = "SELECT reporte_id, sensor_id, estanque_id, titulo, descripcion, creado_en FROM reportes WHERE reporte_id = ?";
        try (Connection con = ConexionPostgres.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Reportes r = new Reportes();
                    r.setReporteId(rs.getInt("reporte_id"));
                    int sensor = rs.getInt("sensor_id");
                    r.setSensorId(rs.wasNull() ? null : sensor);
                    r.setEstanqueId(rs.getInt("estanque_id"));
                    r.setTitulo(rs.getString("titulo"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setCreadoEn(rs.getTimestamp("creado_en"));
                    return r;
                }
            }
        }
        return null;
    }

    public List<Reportes> listarPorEstanque(int estanqueId) throws Exception {
        String sql = "SELECT reporte_id, sensor_id, estanque_id, titulo, descripcion, creado_en FROM reportes WHERE estanque_id = ? ORDER BY creado_en DESC";
        List<Reportes> lista = new ArrayList<>();
        try (Connection con = ConexionPostgres.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reportes r = new Reportes();
                    r.setReporteId(rs.getInt("reporte_id"));
                    int sensor = rs.getInt("sensor_id");
                    r.setSensorId(rs.wasNull() ? null : sensor);
                    r.setEstanqueId(rs.getInt("estanque_id"));
                    r.setTitulo(rs.getString("titulo"));
                    r.setDescripcion(rs.getString("descripcion"));
                    r.setCreadoEn(rs.getTimestamp("creado_en"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }
}

