package com.aeon;

import org.bouncycastle.util.io.pem.PemReader;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.Security;
import java.util.Objects;

public class Main {
    private static final String file = "private_key.pem";

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) {
        try (var inputStream = Main.class.getClassLoader().getResourceAsStream(file)) {
            var bytes = Objects.requireNonNull(inputStream, "Null input stream").readAllBytes();
            var privateKey = getPrivateKey(new String(bytes));
            System.out.println(privateKey.getAlgorithm());
            System.out.println(new String(privateKey.getEncoded()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        var reader = new PemReader(new StringReader(privateKeyString));
        var pemObject = reader.readPemObject();
        System.out.println(pemObject);
        if (pemObject instanceof PrivateKey) {
            return (PrivateKey) pemObject;
        }
        throw new RuntimeException("No private key found");
    }
}