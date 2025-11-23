package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.ReportesDAO;
import com.mycompany.piscicultura_proyect.modelo.Reportes;

public class ReporteControlador {

    private final ReportesDAO reportesDAO;

    public ReporteControlador() {
        this.reportesDAO = new ReportesDAO();
    }

    public int crearReporte(int estanqueId, Integer sensorId, String titulo, String descripcion) throws Exception {
        Reportes r = new Reportes();
        r.setEstanqueId(estanqueId);
        r.setSensorId(sensorId);
        r.setTitulo(titulo);
        r.setDescripcion(descripcion);
        return reportesDAO.guardar(r);
    }
}

