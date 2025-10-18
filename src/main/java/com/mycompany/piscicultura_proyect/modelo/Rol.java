package com.mycompany.piscicultura_proyect.modelo;

public class Rol {

    private int id;
    private String nombre;
    private String descripcion;

    // 🔹 Constructor vacío
    public Rol() {}

    // 🔹 Constructor con parámetros
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // 🔹 Constructor sin ID (para nuevos registros)
    public Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // 🔹 Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre; // útil para comboBox o listas
    }
}

