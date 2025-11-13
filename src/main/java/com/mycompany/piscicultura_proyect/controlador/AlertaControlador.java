package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.AlertaDAO;
import com.mycompany.piscicultura_proyect.modelo.Alerta;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AlertaControlador {

    private final AlertaDAO alertaDAO = new AlertaDAO();

    // Rango esperado por tipo de sensor (puedes moverlo a tabla si prefieres)
    private static final Map<String, double[]> RANGOS = new HashMap<>();
    static {
        RANGOS.put("Temperatura", new double[]{20.0, 30.0}); // °C
        // RANGOS.put("pH", new double[]{6.5, 8.5});
        // RANGOS.put("Oxigeno", new double[]{5.0, 12.0});
    }

    /** Evalúa la última medición de cada sensor y genera alerta si está fuera de rango */
    public void evaluarMedicionesRecientes() throws SQLException {
        String sql = """
            SELECT m.sensor_id, s.estanque_id, s.tipo, m.valor
            FROM (
              SELECT DISTINCT ON (sensor_id) sensor_id, valor
              FROM mediciones
              ORDER BY sensor_id, fecha_hora DESC
            ) m
            JOIN sensores s ON s.sensor_id = m.sensor_id
            """;
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int sensorId = rs.getInt("sensor_id");
                int estanqueId = rs.getInt("estanque_id");
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");

                double[] rango = RANGOS.get(tipo);
                if (rango != null && (valor < rango[0] || valor > rango[1])) {
                    Alerta a = new Alerta();
                    a.setReporteId(null);
                    a.setEstanqueId(estanqueId);
                    a.setSensorId(sensorId);
                    a.setTipo(tipo + " fuera de rango");
                    a.setValor(valor);
                    a.setRangoEsperado(rango[0] + " - " + rango[1]);
                    alertaDAO.insertar(a);
                }
            }
        }
    }
}
