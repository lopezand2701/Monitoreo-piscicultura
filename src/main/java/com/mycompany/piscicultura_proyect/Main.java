package com.mycompany.piscicultura_proyect;

import com.mycompany.piscicultura_proyect.vista.LoginFrame;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        // 🌐 Verificar conexión a la base de datos
        Connection conexion = ConexionPostgres.getConexion();
        if (conexion == null) {
            System.out.println("❌ No se pudo establecer la conexión con la base de datos. Verifica las credenciales.");
            return; // Detener el programa si no hay conexión
        } else {
            System.out.println  ("✅ Conexión establecida correctamente con la base de datos.");
        }

        // 🚀 Iniciar la ventana de Login
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

// ------------------- Credenciales de prueba -------------------
// Admin: admin@piscicultura.com / admin123
// Piscicultor: piscicultor@piscicultura.com / user123
// --------------------------------------------------------------
