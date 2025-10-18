package com.mycompany.piscicultura_proyect.dao;

import com.mycompany.piscicultura_proyect.modelo.Usuario;
import com.mycompany.piscicultura_proyect.ConexionPostgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioDAO {

    private final Connection conexion;

    public UsuarioDAO() {
        this.conexion = ConexionPostgres.getConexion();
    }

    // 🔐 Método para encriptar contraseña con SHA-256
    private String encriptarPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("❌ Error al encriptar password: " + e.getMessage());
            return null;
        }
    }

    // ------------------- INSERTAR -------------------
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, email, password, rol_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());

            // 🔐 Encriptar la contraseña antes de guardar
            String passwordEncriptada = encriptarPassword(usuario.getPassword());
            if (passwordEncriptada == null) {
                return false;
            }

            ps.setString(3, passwordEncriptada);
            ps.setInt(4, usuario.getRolId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ACTUALIZAR -------------------
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, email=?, password=?, rol_id=? WHERE usuario_id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());

            // 🔐 Encriptar la nueva contraseña
            String passwordEncriptada = encriptarPassword(usuario.getPassword());
            if (passwordEncriptada == null) {
                return false;
            }

            ps.setString(3, passwordEncriptada);
            ps.setInt(4, usuario.getRolId());
            ps.setInt(5, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ------------------- ELIMINAR -------------------
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE usuario_id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // ------------------- LISTAR -------------------
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        // SIN TEXT BLOCKS - Compatible con Java 11
        String sql = "SELECT u.usuario_id, u.nombre, u.email, u.password, u.rol_id, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.rol_id " +
                "ORDER BY u.usuario_id";

        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("usuario_id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password")); // 🔐 Contraseña encriptada
                u.setRolId(rs.getInt("rol_id"));
                u.setRolNombre(rs.getString("rol_nombre"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- LOGIN -------------------
    public Usuario verificarLogin(String email, String password) {
        // SIN TEXT BLOCKS - Compatible con Java 11
        String sql = "SELECT u.usuario_id, u.nombre, u.email, u.password, u.rol_id, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.rol_id " +
                "WHERE u.email=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 🔐 Comparar contraseñas encriptadas
                String passwordEncriptadaIngresada = encriptarPassword(password);
                String passwordEncriptadaBD = rs.getString("password");

                if (passwordEncriptadaIngresada != null &&
                        passwordEncriptadaIngresada.equals(passwordEncriptadaBD)) {

                    Usuario u = new Usuario();
                    u.setId(rs.getInt("usuario_id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setPassword(rs.getString("password"));
                    u.setRolId(rs.getInt("rol_id"));
                    u.setRolNombre(rs.getString("rol_nombre"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en login: " + e.getMessage());
        }
        return null;
    }

    // 🔐 Método público para encriptar (puede ser útil para otras clases)
    public static String encriptarPasswordPublic(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("❌ Error al encriptar password: " + e.getMessage());
            return null;
        }
    }
}
