package com.mabcoApp.mabco.Classes;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ClsEncrypte {

    public String encrypt(String plainText, String passPhrase, String saltValue, String hashAlgorithm, int passwordIterations, String initVector, int keySize) throws Exception {
        byte[] initVectorBytes = initVector.getBytes(StandardCharsets.US_ASCII);
        byte[] saltValueBytes = saltValue.getBytes(StandardCharsets.US_ASCII);
        byte[] plainTextBytes = plainText.getBytes(StandardCharsets.US_ASCII);

        SecretKeySpec key = generateKey(passPhrase, saltValueBytes, hashAlgorithm, passwordIterations, keySize);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initVectorBytes));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            cipherOutputStream.write(plainTextBytes);
        }

        byte[] encryptedBytes = outputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(encryptedBytes);
        }
        else
        return plainText;
    }

    public String encrypt(String plainText) throws Exception {
        return encrypt(plainText, "Syria", "Mabco", "MD5", 5, "sdferdfsvbfgtrtd", 128);
    }

    public String decrypt(String cipherText, String passPhrase, String saltValue, String hashAlgorithm, int passwordIterations, String initVector, int keySize) throws Exception {
        byte[] initVectorBytes = initVector.getBytes(StandardCharsets.US_ASCII);
        byte[] saltValueBytes = saltValue.getBytes(StandardCharsets.US_ASCII);
        byte[] encryptedBytes = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encryptedBytes = Base64.getDecoder().decode(cipherText);
        }

        SecretKeySpec key = generateKey(passPhrase, saltValueBytes, hashAlgorithm, passwordIterations, keySize);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initVectorBytes));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return new String(outputStream.toByteArray(), StandardCharsets.US_ASCII);
    }

    public String decrypt(String cipherText) throws Exception {
        return decrypt(cipherText, "Syria", "Mabco", "MD5", 5, "sdferdfsvbfgtrtd", 128);
    }

    private SecretKeySpec generateKey(String passPhrase, byte[] saltValue, String hashAlgorithm, int passwordIterations, int keySize) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
        digest.reset();
        digest.update(saltValue);

        byte[] keyBytes = digest.digest(passPhrase.getBytes(StandardCharsets.US_ASCII));
        for (int i = 1; i < passwordIterations; i++) {
            keyBytes = digest.digest(keyBytes);
        }

        return new SecretKeySpec(Arrays.copyOf(keyBytes, keySize / 8), "AES");
    }

    public String replace(String s) {
        s = s.replace("\\", "ع");
        s = s.replace("/", "غ");
        s = s.replace(":", "ت");
        s = s.replace("*", "ل");
        s = s.replace("?", "ب");
        s = s.replace("\"", "ر");
        s = s.replace("<", "ي");
        s = s.replace(">", "ق");
        return s;
    }

    public String unReplace(String s) {
        s = s.replace("ع", "\\");
        s = s.replace("غ", "/");
        s = s.replace("ت", ":");
        s = s.replace("ل", "*");
        s = s.replace("ب", "?");
        s = s.replace("ر", "\\");
        s = s.replace("ي", "<");
        s = s.replace("ق", ">");
        return s;
    }
}
