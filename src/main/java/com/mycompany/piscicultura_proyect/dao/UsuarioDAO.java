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

    // ==================== MÉTODOS DE CONSULTA ====================

    // ------------------- EXTRAER TODOS -------------------
    public List<Usuario> extraerTodos() {
        List<Usuario> lista = new ArrayList<>();
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
                u.setPassword(rs.getString("password"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRolNombre(rs.getString("rol_nombre"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer todos los usuarios: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR ID -------------------
    public Usuario extraerPorId(int id) {
        String sql = "SELECT u.usuario_id, u.nombre, u.email, u.password, u.rol_id, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.rol_id " +
                "WHERE u.usuario_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("usuario_id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRolNombre(rs.getString("rol_nombre"));
                return u;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer usuario por ID: " + e.getMessage());
        }
        return null;
    }

    // ------------------- EXTRAER POR NOMBRE -------------------
    public List<Usuario> extraerPorNombre(String nombre) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.usuario_id, u.nombre, u.email, u.password, u.rol_id, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.rol_id " +
                "WHERE u.nombre ILIKE ? " +
                "ORDER BY u.nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("usuario_id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRolNombre(rs.getString("rol_nombre"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer usuarios por nombre: " + e.getMessage());
        }
        return lista;
    }

    // ------------------- EXTRAER POR COLUMNA -------------------
    public List<Usuario> extraerPor(String columna, String valor) {
        List<Usuario> lista = new ArrayList<>();

        // Validar columnas permitidas
        List<String> columnasPermitidas = List.of("nombre", "email", "rol_id");
        if (!columnasPermitidas.contains(columna)) {
            System.out.println("❌ Columna no permitida para búsqueda: " + columna);
            return lista;
        }

        String sql = "SELECT u.usuario_id, u.nombre, u.email, u.password, u.rol_id, r.nombre AS rol_nombre " +
                "FROM usuarios u " +
                "JOIN roles r ON u.rol_id = r.rol_id " +
                "WHERE u." + columna + " ILIKE ? " +
                "ORDER BY u.nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (columna.equals("rol_id")) {
                ps.setInt(1, Integer.parseInt(valor));
            } else {
                ps.setString(1, "%" + valor + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("usuario_id"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setRolId(rs.getInt("rol_id"));
                u.setRolNombre(rs.getString("rol_nombre"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al extraer usuarios por columna: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Valor numérico inválido para columna " + columna + ": " + valor);
        }
        return lista;
    }

    // ------------------- MÉTODOS ESPECÍFICOS -------------------
    public List<Usuario> obtenerUsuarios() {
        return extraerTodos(); // Alias para mantener compatibilidad
    }

    // ------------------- LOGIN -------------------
    public Usuario verificarLogin(String email, String password) {
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

    // 🔐 Método público para encriptar
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

    public Usuario obtenerUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE usuario_id = ?";
        try (Connection con = ConexionPostgres.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("usuario_id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setRolId(rs.getInt("rol_id"));
                    // si tienes el campo rol_nombre:
                    u.setRolNombre(rs.getString("rol_nombre"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }


}