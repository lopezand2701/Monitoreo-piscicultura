package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Sensor;
import com.mycompany.piscicultura_proyect.ConexionPostgres;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SensorDAO {

    public List<Sensor> listar() throws SQLException {
        String sql = "SELECT sensor_id, estanque_id, tipo, modelo, unidad, creado_en FROM sensores ORDER BY sensor_id";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Sensor> out = new ArrayList<>();
            while (rs.next()) {
                Sensor s = map(rs);
                out.add(s);
            }
            return out;
        }
    }

    public Sensor obtener(int id) throws SQLException {
        String sql = "SELECT sensor_id, estanque_id, tipo, modelo, unidad, creado_en FROM sensores WHERE sensor_id=?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public int crear(Sensor s) throws SQLException {
        String sql = "INSERT INTO sensores (estanque_id, tipo, modelo, unidad) VALUES (?,?,?,?) RETURNING sensor_id";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getEstanqueId());
            ps.setString(2, s.getTipo());
            ps.setString(3, s.getModelo());
            ps.setString(4, s.getUnidad());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public boolean actualizar(Sensor s) throws SQLException {
        String sql = "UPDATE sensores SET estanque_id=?, tipo=?, modelo=?, unidad=? WHERE sensor_id=?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getEstanqueId());
            ps.setString(2, s.getTipo());
            ps.setString(3, s.getModelo());
            ps.setString(4, s.getUnidad());
            ps.setInt(5, s.getSensorId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM sensores WHERE sensor_id=?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Sensor map(ResultSet rs) throws SQLException {
        Sensor s = new Sensor();
        s.setSensorId(rs.getInt("sensor_id"));
        s.setEstanqueId(rs.getInt("estanque_id"));
        s.setTipo(rs.getString("tipo"));
        s.setModelo(rs.getString("modelo"));
        s.setUnidad(rs.getString("unidad"));
        Timestamp t = rs.getTimestamp("creado_en");
        if (t != null) s.setCreadoEn(t.toLocalDateTime());
        return s;
    }
}
