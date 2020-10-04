package com.school.component.jwt;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class KeyPairFactory {
    private KeyStore keyStore;

    KeyPair create(String keyPath,
                   String keyAlias,
                   String keyPass) {
        ClassPathResource resource = new ClassPathResource(keyPath);
        char[] password = keyPass.toCharArray();
        try {
            synchronized (this) {
                if (keyStore == null) {
                    synchronized (this) {
                        keyStore = KeyStore.getInstance("jks");
                        keyStore.load(resource.getInputStream(), password);
                    }
                }
            }
            RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) keyStore.getKey(keyAlias, password);
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(privateCrtKey.getModulus(), privateCrtKey.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
            return new KeyPair(publicKey, privateCrtKey);
        } catch (InvalidKeySpecException | CertificateException | IOException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            throw new IllegalStateException("can not load keys from store" + resource, e);
        }
    }

}