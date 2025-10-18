package com.mycompany.piscicultura_proyect.modelo;

public class Estacion {
    private int estacionId;
    private int usuarioId;
    private String nombre;
    private String ubicacion;
    private String creadoEn;
    private int departamentoId;
    private int municipioId;

    // Constructores
    public Estacion() {}

    public Estacion(int estacionId, int usuarioId, String nombre, String ubicacion, String creadoEn) {
        this.estacionId = estacionId;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.creadoEn = creadoEn;
    }

    // Constructor completo con departamento y municipio
    public Estacion(int estacionId, int usuarioId, String nombre, String ubicacion, String creadoEn, int departamentoId, int municipioId) {
        this.estacionId = estacionId;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.creadoEn = creadoEn;
        this.departamentoId = departamentoId;
        this.municipioId = municipioId;
    }

    // Getters y Setters
    public int getEstacionId() { return estacionId; }
    public void setEstacionId(int estacionId) { this.estacionId = estacionId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getCreadoEn() { return creadoEn; }
    public void setCreadoEn(String creadoEn) { this.creadoEn = creadoEn; }

    public int getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }

    public int getMunicipioId() { return municipioId; }
    public void setMunicipioId(int municipioId) { this.municipioId = municipioId; }
}