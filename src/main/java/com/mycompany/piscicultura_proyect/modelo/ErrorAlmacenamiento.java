package com.mycompany.piscicultura_proyect.modelo;

import java.time.LocalDateTime;

public class ErrorAlmacenamiento {
    private int id;
    private String tipoError; // DRIVE_UPLOAD, BACKUP, PDF, EXCEL, etc.
    private String mensaje;
    private LocalDateTime fecha;

    // getters/setters
    // ...
}
