import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Security {

    private static final String key = "aesEncryptionKey";
    private static final String initVector = "encryptionIntVec";

    /** Given a String, performs AES encryption on it and returns the resulting ciphertext String */
    protected static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /** Given a String, performs AES decryption on it and returns the resulting plaintext String */
    protected static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /** Given a string value and an algorithm, calculates the corresponding hash in hexadecimal
     * Possible algorithms include: MD5, SHA-1, SHA-256 */
    protected static String computeHash(String str, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] encodedhash = digest.digest(str.getBytes(StandardCharsets.UTF_8));
        StringBuffer hexString = new StringBuffer();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    /** Given a string value, return the string concatenated with its hash value */
    protected static String combineStrAndHash(String str) throws NoSuchAlgorithmException {
        return str + computeHash(str, "SHA-256");
    }
    /** Given a string concatenated with its hash value, extracts the hash value */
    protected static String extractHash(String strAndHash) {
        return strAndHash.substring(strAndHash.length() - 64); // SHA-256 is 64 chars long
    }
    /** Given a string concatenated with its hash value, extracts the string value */
    protected static String extractStr(String strAndHash) {
        return strAndHash.substring(0, strAndHash.length() - 64); // SHA-256 is 64 chars long
    }
    /** Verifies if the hash value corresponds to the string, returns true if verified, false otherwise */
    protected static Boolean verifyHash(String strAndHash) throws NoSuchAlgorithmException {
        // calculate the hash from the transmitted string
        String calculatedHash = computeHash(extractStr(strAndHash), "SHA-256");
        // compare the transmitted hash value to the calculated hash value
        return calculatedHash.equals(extractHash(strAndHash));
    }
}
