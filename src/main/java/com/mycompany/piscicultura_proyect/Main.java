
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
            System.out.println("✅ \n" + "Conexión establecida correctamente.");
        }

        // 🎨 Configurar estilo visual (look & feel)
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { 
            System.out.println("⚠️ No se pudo aplicar el estilo visual: " + e.getMessage());
        }                                                               

        // 🚀 Iniciar la ventana de Login
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
   