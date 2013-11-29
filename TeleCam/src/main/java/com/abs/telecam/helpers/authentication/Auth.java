package com.abs.telecam.helpers.authentication;

public class Auth {

    public static boolean isPasswordValid(String password){
        String password1 = "1ec10bf67b724bfc1f1d155bc98ef96a857eced3";
        return password1.equals(SHA(password));
    }

    public static String SHA(String sha) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] array = md.digest(sha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException ignored) {
        }
        return null;
    }
}
