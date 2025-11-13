package com.mycompany.piscicultura_proyect.util;

import com.mycompany.piscicultura_proyect.dao.ErrorAlmacenamientoDAO;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Maneja el almacenamiento local simulando una nube.
 * Guarda archivos en la carpeta /nube dentro del proyecto.
 */
public class StorageUtil {

    private static final String CLOUD_FOLDER = "nube"; // carpeta raíz

    /** Sube un archivo (lo copia dentro de la carpeta /nube/) */
    public static String subirArchivo(File archivo) {
        ErrorAlmacenamientoDAO logdao = new ErrorAlmacenamientoDAO();
        try {
            // Crear carpeta si no existe
            File carpeta = new File(CLOUD_FOLDER);
            if (!carpeta.exists()) carpeta.mkdirs();

            // Copiar el archivo con nombre único
            String nombreDestino = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_")) + archivo.getName();
            Path destino = Paths.get(carpeta.getAbsolutePath(), nombreDestino);
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Archivo subido a nube local: " + destino);
            return destino.toString();

        } catch (Exception e) {
            logdao.registrar("UPLOAD_LOCAL", e.getMessage());
            System.err.println("Error subiendo archivo: " + e.getMessage());
            return null;
        }
    }

    /** Realiza un respaldo automático de la base de datos (simulado) */
    public static void backupBaseDatos(String nombreBD) {
        ErrorAlmacenamientoDAO logdao = new ErrorAlmacenamientoDAO();
        try {
            File carpeta = new File(CLOUD_FOLDER + "/backups");
            if (!carpeta.exists()) carpeta.mkdirs();

            String nombreArchivo = nombreBD + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";

            File destino = new File(carpeta, nombreArchivo);

            // Aquí simulas el backup con texto
            try (PrintWriter pw = new PrintWriter(destino)) {
                pw.println("-- Simulación de respaldo de base de datos " + nombreBD);
                pw.println("-- Generado en: " + LocalDateTime.now());
            }

            System.out.println("Backup guardado en nube local: " + destino.getAbsolutePath());

        } catch (Exception e) {
            logdao.registrar("BACKUP_LOCAL", e.getMessage());
            System.err.println("Error generando backup: " + e.getMessage());
        }
    }
}
