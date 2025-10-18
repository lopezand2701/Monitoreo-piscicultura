package com.mycompany.piscicultura_proyect.modelo;

public class Departamento {
    private int departamentoId;
    private String nombre;

    // Constructores
    public Departamento() {}

    public Departamento(int departamentoId, String nombre) {
        this.departamentoId = departamentoId;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}