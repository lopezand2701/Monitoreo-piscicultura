package com.mycompany.piscicultura_proyect.modelo;

import java.time.LocalDateTime;

public class Estanque {
    private int estanqueId;
    private int estacionId;
    private String nombre;
    private double volumenM3;
    private String descripcion;
    private LocalDateTime creadoEn;

    public Estanque() {}

    public Estanque(int estanqueId, int estacionId, String nombre, double volumenM3, String descripcion, LocalDateTime creadoEn) {
        this.estanqueId = estanqueId;
        this.estacionId = estacionId;
        this.nombre = nombre;
        this.volumenM3 = volumenM3;
        this.descripcion = descripcion;
        this.creadoEn = creadoEn;
    }

    public int getEstanqueId() { return estanqueId; }
    public void setEstanqueId(int estanqueId) { this.estanqueId = estanqueId; }

    public int getEstacionId() { return estacionId; }
    public void setEstacionId(int estacionId) { this.estacionId = estacionId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getVolumenM3() { return volumenM3; }
    public void setVolumenM3(double volumenM3) { this.volumenM3 = volumenM3; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
