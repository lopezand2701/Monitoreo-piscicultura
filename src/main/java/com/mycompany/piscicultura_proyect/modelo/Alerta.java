package com.mycompany.piscicultura_proyect.modelo;

import java.time.LocalDateTime;

public class Alerta {

    private int alertaId;
    private Integer reporteId; // Puede ser null
    private int estanqueId;
    private int sensorId;
    private String tipo;
    private double valor;
    private String rangoEsperado;
    private LocalDateTime generadoEn;

    // Constructor vacío
    public Alerta() {
    }

    // Constructor opcional
    public Alerta(Integer reporteId, int estanqueId, int sensorId, String tipo, double valor, String rangoEsperado) {
        this.reporteId = reporteId;
        this.estanqueId = estanqueId;
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.rangoEsperado = rangoEsperado;
        this.generadoEn = LocalDateTime.now();
    }

    // Getters y Setters
    public int getAlertaId() {
        return alertaId;
    }

    public void setAlertaId(int alertaId) {
        this.alertaId = alertaId;
    }

    public Integer getReporteId() {
        return reporteId;
    }

    public void setReporteId(Integer reporteId) {
        this.reporteId = reporteId;
    }

    public int getEstanqueId() {
        return estanqueId;
    }

    public void setEstanqueId(int estanqueId) {
        this.estanqueId = estanqueId;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getRangoEsperado() {
        return rangoEsperado;
    }

    public void setRangoEsperado(String rangoEsperado) {
        this.rangoEsperado = rangoEsperado;
    }

    public LocalDateTime getGeneradoEn() {
        return generadoEn;
    }

    public void setGeneradoEn(LocalDateTime generadoEn) {
        this.generadoEn = generadoEn;
    }

    @Override
    public String toString() {
        return "Alerta{" +
                "alertaId=" + alertaId +
                ", estanqueId=" + estanqueId +
                ", sensorId=" + sensorId +
                ", tipo='" + tipo + '\'' +
                ", valor=" + valor +
                ", rangoEsperado='" + rangoEsperado + '\'' +
                ", generadoEn=" + generadoEn +
                '}';
    }
}
