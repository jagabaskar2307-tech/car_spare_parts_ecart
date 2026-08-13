package com.jagadeesh.jagadeeshcart.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    public static boolean matches(String plainPassword, String hash) {
        return BCrypt.checkpw(plainPassword, hash);
    }

    /**
     * Utility entry point to print a bcrypt hash for a plaintext password,
     * so seed.sql can be populated with a real hash instead of a placeholder.
     * Usage: mvn compile exec:java -Dexec.mainClass=com.jagadeesh.jagadeeshcart.util.PasswordUtil -Dexec.args="admin123"
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: PasswordUtil <plainPassword>");
            return;
        }
        System.out.println(hash(args[0]));
    }
}
