package ch.bytecrowd.markupeditor.jwt.helpers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private HashUtil() {}

    public static String sha512(String message) {
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            byte[] digest = sha512.digest(message.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hash = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                hash.append(
                        String.format("%02x", digest[i])
                );
            }
            return hash.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
