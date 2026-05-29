package com.jpb.ServiceImpl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
public class EncryptionService {
	
	public static String generateRandomString(int length) {
	    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	    Random random = new Random();
	    StringBuilder stringBuilder = new StringBuilder(length);

	    for (int i = 0; i < length; i++) {
	        int randomIndex = random.nextInt(characters.length());
	        char randomChar = characters.charAt(randomIndex);
	        stringBuilder.append(randomChar);
	    }
	    return stringBuilder.toString();
	}
	
	public static String encryptAES(String plainData, String aesKey) throws Exception {
	    SecretKeySpec secretKey = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
	    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    return Base64.getEncoder().encodeToString(cipher.doFinal(plainData.getBytes()));
	}
	
	public static String encryptRSA(String plainData, String base64PublicKey) throws Exception {
	    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey));
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	    PublicKey publicKey = getPublicKey(publicKeySpec);
	    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	    return Base64.getEncoder().encodeToString(cipher.doFinal(plainData.getBytes()));
	}
	
	private static PublicKey getPublicKey(X509EncodedKeySpec keySpec) throws Exception {
	    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	    return keyFactory.generatePublic(keySpec);
	}
}
