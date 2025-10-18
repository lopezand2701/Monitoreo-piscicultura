package com.mycompany.piscicultura_proyect.modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private int rolId;         // ID del rol en la base de datos (1=Admin, 2=Piscicultor, etc.)
    private String rolNombre;  // Nombre legible del rol ("Administrador", "Piscicultor")

    // -------- CONSTRUCTORES --------
    public Usuario() {
    }

    // Constructor completo
    public Usuario(int id, String nombre, String email, String password, int rolId, String rolNombre) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
        this.rolNombre = rolNombre;
    }

    // Constructor sin id (para inserciones nuevas)
    public Usuario(String nombre, String email, String password, int rolId) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
    }

    // Constructor sin rolNombre (cuando no se necesita mostrarlo)
    public Usuario(int id, String nombre, String email, String password, int rolId) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rolId = rolId;
    }

    // -------- GETTERS Y SETTERS --------
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRolId() {
        return rolId;
    }

    public void setRolId(int rolId) {
        this.rolId = rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    // -------- MÉTODOS DE UTILIDAD --------
    public boolean esAdmin() {
        return rolId == 1 || "admin".equalsIgnoreCase(rolNombre);
    }

    public boolean esPiscicultor() {
        return rolId == 2 || "piscicultor".equalsIgnoreCase(rolNombre);
    }

    // -------- toString --------
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rolId=" + rolId +
                ", rolNombre='" + rolNombre + '\'' +
                '}';
    }
}
