package com.mycompany.piscicultura_proyect.controlador;

import com.mycompany.piscicultura_proyect.dao.UsuarioDAO;
import com.mycompany.piscicultura_proyect.modelo.Usuario;

import java.util.List;

public class UsuarioControlador {

    private final UsuarioDAO usuarioDAO;

    public UsuarioControlador() {
        this.usuarioDAO = new UsuarioDAO();
    }

    // ------------------- LOGIN -------------------
    public Usuario verificarLogin(String email, String password) {
        System.out.println("🔐 Controlador - Intentando login para: " + email);
        Usuario usuario = usuarioDAO.verificarLogin(email, password);
        if (usuario != null) {
            System.out.println("✅ Login exitoso: " + usuario.getNombre() + " (Rol: " + usuario.getRolNombre() + ")");
        } else {
            System.out.println("❌ Credenciales inválidas para: " + email);
        }
        return usuario;
    }

    // ------------------- INSERTAR -------------------
    public boolean insertarUsuario(Usuario usuario) {
        if (usuario.getNombre().isEmpty() || usuario.getEmail().isEmpty() || usuario.getPassword().isEmpty()) {
            System.out.println("⚠️ Datos incompletos, no se puede registrar el usuario.");
            return false;
        }

        if (!validarFortalezaPassword(usuario.getPassword())) {
            System.out.println("⚠️ La contraseña debe tener al menos 6 caracteres.");
            return false;
        }

        return usuarioDAO.insertarUsuario(usuario);
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean modificarUsuario(Usuario usuario) {
        if (!validarFortalezaPassword(usuario.getPassword())) {
            System.out.println("⚠️ La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        return usuarioDAO.actualizarUsuario(usuario);
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminarUsuario(int id) {
        return usuarioDAO.eliminarUsuario(id);
    }

    // ------------------- LISTAR -------------------
    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.obtenerUsuarios();
    }

    // ------------------- VALIDACIONES -------------------
    public boolean esAdmin(Usuario usuario) {
        return usuario != null && "admin".equalsIgnoreCase(usuario.getRolNombre());
    }

    public boolean esPiscicultor(Usuario usuario) {
        return usuario != null && "piscicultor".equalsIgnoreCase(usuario.getRolNombre());
    }

    private boolean validarFortalezaPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static String encriptarPassword(String password) {
        return UsuarioDAO.encriptarPasswordPublic(password);
    }

    // ------------------- NUEVOS MÉTODOS PARA INTERFAZ CRUD -------------------
    /**
     * Devuelve la lista completa de usuarios (para combos u otras vistas).
     */
    public List<Usuario> listarTodos() {
        return usuarioDAO.obtenerUsuarios();
    }

    /**
     * Devuelve el nombre del usuario dado su ID.
     */
    public String obtenerNombreUsuario(int id) {
        Usuario u = usuarioDAO.obtenerUsuarioPorId(id);
        return (u != null) ? u.getNombre() : "Desconocido";
    }
}


