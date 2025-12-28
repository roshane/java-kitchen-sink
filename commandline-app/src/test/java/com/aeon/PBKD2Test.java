package com.aeon;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PBKD2Test {
    private final Logger logger = LoggerFactory.getLogger(PBKD2Test.class);
    private int keyLength = 256;
    private int iterations = 1_000;
    private String previouslyGenerated = "19qdynjt5outNHwa+Ruczu8pjiLhS1YHphKNQHaF35/0zwof6p4TR2Pk3gNiatV98NA/IlX7oI2bBZhpi3CYUA==";
    private final SecretKeyFactory factory = fromThrowable(() -> SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256"));
    private final String rawPassword = "password";
    private final String rawSalt = "19890615".repeat(2);
    private final byte[] saltBytes = rawSalt.getBytes(StandardCharsets.UTF_8);
    private final SecureRandom secureRandom = new SecureRandom(String.valueOf(System.nanoTime()).getBytes(StandardCharsets.UTF_8));
    private final String plainText = "Hello, Roshane! how are you?";
    private final String encryptedTextB64 = "SneesAoZmhii5opMXmHnXz+E5+d17H1T3FbFGKmy3dk=";

    static <R> R fromThrowable(Callable<R> provider) {
        try {
            return provider.call();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Test
    void generatePassword() throws Exception {
        var generatedKey = generatePBKDKey().getEncoded();
        var generatedKeyString = new String(Base64.getEncoder().encode(generatedKey));
        logger.info("generated-key: {}", generatedKeyString);
        assertEquals(previouslyGenerated, generatedKeyString);
    }

    @Test
    void doEncryption() throws Exception {
        var encryptor = getCipher(Cipher.ENCRYPT_MODE, saltBytes);
        byte[] encryptedBytes = encryptor.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        logger.info("Plain text: {}", plainText);
        logger.info("Encrypted text: {}", Base64.getEncoder().encodeToString(encryptedBytes));
        var decryptor = getCipher(Cipher.DECRYPT_MODE, saltBytes);
        byte[] decryptedBytes = decryptor.doFinal(encryptedBytes);
        logger.info("Decrypted text: {}", new String(decryptedBytes));
    }

    @Test
    void doDecrypt() throws Exception {
        var cipher = getCipher(Cipher.DECRYPT_MODE, saltBytes);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedTextB64.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = cipher.doFinal(encryptedBytes);
        String decodedString = new String(decrypted);
        logger.info("decryptedText: {}", decodedString);
        assertEquals(plainText, decodedString);
    }

    Cipher getCipher(int mode, byte[] seed) throws Exception {
        var cipherAlgo = "PBEWithHmacSHA256AndAES_256";
        var secretKey = Objects.requireNonNull(generatePBKDKey());
        var cipher = Cipher.getInstance(cipherAlgo);
        var pebParamSpec =
                new PBEParameterSpec(saltBytes, iterations, new IvParameterSpec(seed));
        cipher.init(mode, secretKey, pebParamSpec);
        return cipher;
    }

    SecretKey generatePBKDKey() {
        logger.info("salt: {}", new String(Base64.getEncoder().encode(saltBytes)));
        var passwordCharArray = rawPassword.toCharArray();
        var keySpec = new PBEKeySpec(passwordCharArray, saltBytes, iterations, keyLength);
        return fromThrowable(() -> factory.generateSecret(keySpec));
    }
}
