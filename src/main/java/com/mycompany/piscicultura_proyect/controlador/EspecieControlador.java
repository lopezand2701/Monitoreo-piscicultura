package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.EspecieDAO;
import com.mycompany.piscicultura_proyect.modelo.Especie;

import java.util.List;

public class EspecieControlador {

    private final EspecieDAO especieDAO;

    public EspecieControlador() {
        this.especieDAO = new EspecieDAO();
    }

    // ------------------- INSERTAR -------------------
    public boolean insertarEspecie(Especie especie) {
        if (especie.getNombreCientifico().isEmpty() || especie.getNombreComun().isEmpty()) {
            System.out.println("⚠️ Datos incompletos, no se puede registrar la especie.");
            return false;
        }
        return especieDAO.insertarEspecie(especie);
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean modificarEspecie(Especie especie) {
        return especieDAO.actualizarEspecie(especie);
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminarEspecie(int id) {
        return especieDAO.eliminarEspecie(id);
    }

    // ------------------- LISTAR -------------------
    public List<Especie> obtenerEspecies() {
        return especieDAO.obtenerTodasEspecies();
    }

    // ------------------- OBTENER POR ID -------------------
    public Especie obtenerEspeciePorId(int id) {
        return especieDAO.obtenerEspeciePorId(id);
    }

    // ------------------- ASIGNAR ESPECIE A ESTANQUE -------------------
    public boolean asignarEspecieAEstanque(int estanqueId, int especieId, int cantidad) {
        if (cantidad <= 0) {
            System.out.println("⚠️ La cantidad debe ser mayor a cero.");
            return false;
        }
        return especieDAO.asignarEspecieAEstanque(estanqueId, especieId, cantidad);
    }

    // ------------------- OBTENER ESPECIES DE ESTANQUE -------------------
    public List<Especie> obtenerEspeciesPorEstanque(int estanqueId) {
        return especieDAO.obtenerEspeciesPorEstanque(estanqueId);
    }

    // ------------------- OBTENER ESPECIES CON CANTIDAD -------------------
    public String obtenerEspeciesConCantidad(int estanqueId) {
        return especieDAO.obtenerEspeciesConCantidad(estanqueId);
    }
}