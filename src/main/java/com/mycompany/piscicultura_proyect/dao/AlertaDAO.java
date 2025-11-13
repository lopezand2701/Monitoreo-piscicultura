package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Alerta;
import com.mycompany.piscicultura_proyect.ConexionPostgres;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertaDAO {

    public int insertar(Alerta a) throws SQLException {
        String sql = "INSERT INTO alertas (reporte_id, estanque_id, sensor_id, tipo, valor, rango_esperado) " +
                "VALUES (?,?,?,?,?,?) RETURNING alerta_id";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (a.getReporteId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, a.getReporteId());
            ps.setInt(2, a.getEstanqueId());
            ps.setInt(3, a.getSensorId());
            ps.setString(4, a.getTipo());
            ps.setDouble(5, a.getValor());
            ps.setString(6, a.getRangoEsperado());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public List<Alerta> listarPorEstanque(int estanqueId) throws SQLException {
        String sql = "SELECT * FROM alertas WHERE estanque_id=? ORDER BY generado_en DESC";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, estanqueId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Alerta> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        }
    }

    private Alerta map(ResultSet rs) throws SQLException {
        Alerta a = new Alerta();
        a.setAlertaId(rs.getInt("alerta_id"));
        int r = rs.getInt("reporte_id");
        a.setReporteId(rs.wasNull() ? null : r);
        a.setEstanqueId(rs.getInt("estanque_id"));
        a.setSensorId(rs.getInt("sensor_id"));
        a.setTipo(rs.getString("tipo"));
        a.setValor(rs.getDouble("valor"));
        a.setRangoEsperado(rs.getString("rango_esperado"));
        Timestamp t = rs.getTimestamp("generado_en");
        if (t != null) a.setGeneradoEn(t.toLocalDateTime());
        return a;
    }
}
