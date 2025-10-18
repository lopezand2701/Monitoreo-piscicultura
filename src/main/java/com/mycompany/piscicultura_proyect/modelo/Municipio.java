package com.mycompany.piscicultura_proyect.modelo;

public class Municipio {
    private int municipioId;
    private int departamentoId;
    private String nombre;

    // Constructores
    public Municipio() {}

    public Municipio(int municipioId, int departamentoId, String nombre) {
        this.municipioId = municipioId;
        this.departamentoId = departamentoId;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getMunicipioId() { return municipioId; }
    public void setMunicipioId(int municipioId) { this.municipioId = municipioId; }

    public int getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}