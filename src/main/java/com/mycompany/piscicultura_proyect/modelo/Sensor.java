package com.mycompany.piscicultura_proyect.modelo;

import java.time.LocalDateTime;

public class Sensor {

    private int sensorId;
    private int estanqueId;
    private String tipo;      // Ej: Temperatura, pH, Oxígeno, etc.
    private String modelo;    // Ej: DS18B20
    private String unidad;    // Ej: °C, ppm, etc.
    private LocalDateTime creadoEn;

    // Constructor vacío
    public Sensor() {
    }

    // Constructor opcional
    public Sensor(int estanqueId, String tipo, String modelo, String unidad) {
        this.estanqueId = estanqueId;
        this.tipo = tipo;
        this.modelo = modelo;
        this.unidad = unidad;
        this.creadoEn = LocalDateTime.now();
    }

    // ======================
    // Getters y Setters
    // ======================

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getEstanqueId() {
        return estanqueId;
    }

    public void setEstanqueId(int estanqueId) {
        this.estanqueId = estanqueId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId=" + sensorId +
                ", estanqueId=" + estanqueId +
                ", tipo='" + tipo + '\'' +
                ", modelo='" + modelo + '\'' +
                ", unidad='" + unidad + '\'' +
                ", creadoEn=" + creadoEn +
                '}';
    }
}
