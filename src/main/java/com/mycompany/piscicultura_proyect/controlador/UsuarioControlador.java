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
    /**
     * Verifica las credenciales del usuario y devuelve el objeto Usuario con su rol.
     */
    public Usuario verificarLogin(String email, String password) {
        System.out.println("🔐 Controlador - Intentando login para: " + email);

        Usuario usuario = usuarioDAO.verificarLogin(email, password);
        if (usuario != null) {
            System.out.println("✅ Controlador - Login exitoso. Rol: " + usuario.getRolNombre());
            System.out.println("✅ Controlador - ID: " + usuario.getId() + ", Nombre: " + usuario.getNombre());
        } else {
            System.out.println("❌ Controlador - Credenciales inválidas para: " + email);
        }
        return usuario;
    }

    // ------------------- INSERTAR -------------------
    /**
     * Inserta un nuevo usuario (puede ser admin o piscicultor).
     */
    public boolean insertarUsuario(Usuario usuario) {
        if (usuario.getNombre().isEmpty() || usuario.getEmail().isEmpty() || usuario.getPassword().isEmpty()) {
            System.out.println("⚠️ Datos incompletos, no se puede registrar el usuario.");
            return false;
        }

        // Validar fortaleza de contraseña
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
        List<Usuario> usuarios = usuarioDAO.obtenerUsuarios();
        System.out.println("📊 Controlador - Usuarios obtenidos: " + usuarios.size());
        for (Usuario u : usuarios) {
            System.out.println("   - " + u.getNombre() + " (" + u.getEmail() + ") - Rol: " + u.getRolNombre());
        }
        return usuarios;
    }

    // ------------------- VALIDACIONES POR ROL -------------------
    /**
     * Determina si un usuario tiene permisos de administrador.
     */
    public boolean esAdmin(Usuario usuario) {
        boolean esAdmin = usuario != null && "admin".equalsIgnoreCase(usuario.getRolNombre());
        System.out.println("👨‍💼 Controlador - ¿Es admin " + (usuario != null ? usuario.getNombre() : "null") + "? " + esAdmin);
        return esAdmin;
    }

    /**
     * Determina si un usuario es piscicultor.
     */
    public boolean esPiscicultor(Usuario usuario) {
        boolean esPiscicultor = usuario != null && "piscicultor".equalsIgnoreCase(usuario.getRolNombre());
        System.out.println("👨‍🌾 Controlador - ¿Es piscicultor " + (usuario != null ? usuario.getNombre() : "null") + "? " + esPiscicultor);
        return esPiscicultor;
    }

    // 🔐 Validar fortaleza de contraseña
    private boolean validarFortalezaPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // 🔐 Método público para encriptar contraseñas
    public static String encriptarPassword(String password) {
        return UsuarioDAO.encriptarPasswordPublic(password);
    }
}
