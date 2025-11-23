package com.mycompany.piscicultura_proyect.modelo;

import java.sql.Timestamp;

public class Reportes {
    private Integer reporteId;
    private Integer sensorId;
    private Integer estanqueId;
    private String titulo;
    private String descripcion;
    private Timestamp creadoEn;

    public Reportes() {}

    public Reportes(Integer reporteId, Integer sensorId, Integer estanqueId, String titulo, String descripcion, Timestamp creadoEn) {
        this.reporteId = reporteId;
        this.sensorId = sensorId;
        this.estanqueId = estanqueId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.creadoEn = creadoEn;
    }

    public Reportes(Integer estanqueId, String titulo, String descripcion) {
        this.estanqueId = estanqueId;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Integer getReporteId() { return reporteId; }
    public void setReporteId(Integer reporteId) { this.reporteId = reporteId; }
    public Integer getSensorId() { return sensorId; }
    public void setSensorId(Integer sensorId) { this.sensorId = sensorId; }
    public Integer getEstanqueId() { return estanqueId; }
    public void setEstanqueId(Integer estanqueId) { this.estanqueId = estanqueId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Timestamp getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Timestamp creadoEn) { this.creadoEn = creadoEn; }
}

