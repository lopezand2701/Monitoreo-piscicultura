package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.UbicacionDAO;
import com.mycompany.piscicultura_proyect.modelo.Departamento;
import com.mycompany.piscicultura_proyect.modelo.Municipio;

import java.util.List;

public class UbicacionControlador {

    private final UbicacionDAO ubicacionDAO;

    public UbicacionControlador() {
        this.ubicacionDAO = new UbicacionDAO();
    }

    // ------------------- OBTENER DEPARTAMENTOS -------------------
    public List<Departamento> obtenerTodosDepartamentos() {
        return ubicacionDAO.obtenerTodosDepartamentos();
    }

    // ------------------- OBTENER MUNICIPIOS POR DEPARTAMENTO -------------------
    public List<Municipio> obtenerMunicipiosPorDepartamento(int departamentoId) {
        return ubicacionDAO.obtenerMunicipiosPorDepartamento(departamentoId);
    }

    // ------------------- OBTENER DEPARTAMENTO POR ID -------------------
    public Departamento obtenerDepartamentoPorId(int id) {
        return ubicacionDAO.obtenerDepartamentoPorId(id);
    }

    // ------------------- OBTENER MUNICIPIO POR ID -------------------
    public Municipio obtenerMunicipioPorId(int id) {
        return ubicacionDAO.obtenerMunicipioPorId(id);
    }

    // ------------------- OBTENER NOMBRES -------------------
    public String obtenerNombreDepartamento(int departamentoId) {
        return ubicacionDAO.obtenerNombreDepartamento(departamentoId);
    }

    public String obtenerNombreMunicipio(int municipioId) {
        return ubicacionDAO.obtenerNombreMunicipio(municipioId);
    }
}