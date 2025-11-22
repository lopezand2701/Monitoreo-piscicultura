package com.mycompany.piscicultura_proyect.modelo;

public class Especie {
    private int especieId;
    private String nombreCientifico;
    private String nombreComun;
    private String descripcion;
    private Double tempMin;
    private Double tempMax;
    private Double phMinimo;
    private Double phMaximo;
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

    // Constructor completo con rangos
    public Especie(int especieId, String nombreCientifico, String nombreComun, String descripcion,
                   Double tempMin, Double tempMax, Double phMinimo, Double phMaximo) {
        this.especieId = especieId;
        this.nombreCientifico = nombreCientifico;
        this.nombreComun = nombreComun;
        this.descripcion = descripcion;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.phMinimo = phMinimo;
        this.phMaximo = phMaximo;
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

    public Double getTempMin() { return tempMin; }
    public void setTempMin(Double tempMin) { this.tempMin = tempMin; }

    public Double getTempMax() { return tempMax; }
    public void setTempMax(Double tempMax) { this.tempMax = tempMax; }

    public Double getPhMinimo() { return phMinimo; }
    public void setPhMinimo(Double phMinimo) { this.phMinimo = phMinimo; }

    public Double getPhMaximo() { return phMaximo; }
    public void setPhMaximo(Double phMaximo) { this.phMaximo = phMaximo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return nombreComun + " (" + nombreCientifico + ")";
    }

    // Método para obtener rangos como texto
    public String getRangosTexto() {
        StringBuilder sb = new StringBuilder();
        if (tempMin != null && tempMax != null) {
            sb.append("Temp: ").append(tempMin).append("°C - ").append(tempMax).append("°C");
        }
        if (phMinimo != null && phMaximo != null) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append("pH: ").append(phMinimo).append(" - ").append(phMaximo);
        }
        return sb.length() > 0 ? sb.toString() : "Rangos no definidos";
    }
}