package com.mycompany.piscicultura_proyect.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para encriptación de contraseñas usando SHA-256
 */
public class PasswordUtil {

    /**
     * Encripta una contraseña usando SHA-256
     * @param password Contraseña en texto plano
     * @return Contraseña encriptada en hexadecimal (64 caracteres) o null si hay error
     */
    public static String encriptarSHA256(String password) {
        if (password == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("❌ Error: Algoritmo SHA-256 no disponible: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si una contraseña en texto plano coincide con una encriptada
     * @param passwordPlain Contraseña en texto plano
     * @param passwordEncrypted Contraseña encriptada
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verificarPassword(String passwordPlain, String passwordEncrypted) {
        if (passwordPlain == null || passwordEncrypted == null) {
            return false;
        }

        String encrypted = encriptarSHA256(passwordPlain);
        return encrypted != null && encrypted.equals(passwordEncrypted);
    }

    /**
     * Valida la fortaleza de una contraseña
     * @param password Contraseña a validar
     * @return true si es suficientemente fuerte (mínimo 6 caracteres)
     */
    public static boolean validarFortalezaPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Genera un mensaje informativo sobre los requisitos de contraseña
     * @return String con los requisitos
     */
    public static String getRequisitosPassword() {
        return "La contraseña debe tener al menos 6 caracteres. Se encriptará automáticamente con SHA-256.";
    }
}