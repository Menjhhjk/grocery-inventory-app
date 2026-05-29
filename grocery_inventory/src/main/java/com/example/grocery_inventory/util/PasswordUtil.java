package com.example.grocery_inventory.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    public static boolean verify(String plainPassword, String storedHash) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), storedHash).verified;
    }
}
