package com.example.appfirebaselittledemons.utils;

public class ValidationUtils {

    private static final int USERNAME_MAX_LENGTH = 20;
    private static final int ROOMCODE_MAX_LENGTH = 6;

    // Validar nombre de usuario
    public static boolean isValidUsername(String username) {
        return username != null && username.length() > 0 && username.length() <= USERNAME_MAX_LENGTH;
    }

    // Validar código de sala
    public static boolean isValidRoomCode(String roomCode) {
        return roomCode != null && roomCode.matches("\\d{" + ROOMCODE_MAX_LENGTH + "}");
    }
}
