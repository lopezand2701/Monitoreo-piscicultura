package com.mycompany.piscicultura_proyect.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Lanza un backup diario y lo guarda en la nube local (carpeta /nube/backups)
 */
public class BackupScheduler {

    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

    /**
     * Inicia el programador de backups automáticos
     *
     * @param nombreBD Nombre de la base de datos (solo usado en el nombre del archivo)
     */
    public void iniciar(String nombreBD) {
        Runnable tarea = () -> StorageUtil.backupBaseDatos(nombreBD);

        // Primer disparo en 10 segundos, luego cada 24 horas (en segundos)
        ses.scheduleAtFixedRate(tarea, 10, 24 * 60 * 60, TimeUnit.SECONDS);

        System.out.println("🕒 Programador de backups iniciado. Se realizará un respaldo cada 24h.");
    }

    /**
     * Detiene el programador de backups
     */
    public void detener() {
        ses.shutdownNow();
        System.out.println("⏹️ Programador de backups detenido.");
    }
}
