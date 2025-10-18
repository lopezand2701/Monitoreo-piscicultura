package com.mycompany.piscicultura_proyect.modelo;

public class Especie {
    private int especieId;
    private String nombreCientifico;
    private String nombreComun;
    private String descripcion;
    private boolean activo;

    // Constructores
    public Especie() {}

    public Especie(int especieId, String nombreCientifico, String nombreComun, String descripcion) {
        this.especieId = especieId;
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public Especie(String nombreCientifico, String nombreComun, String descripcion) {
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
        this.descripcion = descripcion;
        this.activo = true;
    }

    // Getters y Setters
    public int getEspecieId() { return especieId; }
    public void setEspecieId(int especieId) { this.especieId = especieId; }

    public String getNombreCientifico() { return nombreCientifico; }
    public void setNombreCientifico(String nombreCientifico) { this.nombreCientifico = nombreCientifico; }

    public String getNombreComun() { return nombreComun; }
    public void setNombreComun(String nombreComun) { this.nombreComun = nombreComun; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreComun + " (" + nombreCientifico + ")";
    }
}
