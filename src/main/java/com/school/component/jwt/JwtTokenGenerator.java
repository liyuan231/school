package com.school.component.jwt;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.school.exception.JwtExpiredAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.util.Assert;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JwtTokenGenerator {
    private JwtPayloadBuilder jwtPayloadBuilder;
    private JwtProperties jwtProperties;
    private KeyPair keyPair;

    public JwtTokenGenerator(JwtPayloadBuilder jwtPayloadBuilder, JwtProperties jwtProperties) {
        this.jwtPayloadBuilder = jwtPayloadBuilder;
        this.jwtProperties = jwtProperties;
        KeyPairFactory keyPairFactory = new KeyPairFactory();
        this.keyPair = keyPairFactory.create(jwtProperties.getKeyLocation(), jwtProperties.getKeyAlias(), jwtProperties.getKeyPass());
    }

    public JwtTokenPair jwtTokenPair(String audience,
                                     Set<String> authorities,
                                     Map<String, String> additional) throws JsonProcessingException {
        String accessToken = jwtToken(audience, jwtProperties.getTokenExpirationDays(), authorities, additional);
        String refreshToken = jwtToken(audience, jwtProperties.getRefreshTokenExpirationDays(), authorities, additional);
        JwtTokenPair jwtTokenPair = new JwtTokenPair(accessToken, refreshToken);
        return jwtTokenPair;
    }

    public JwtTokenPair jwtTokenPair(String audience,
                                     Collection<? extends GrantedAuthority> authorities,
                                     Map<String, String> additional) throws JsonProcessingException {
        Set<String> authorityStrings = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            authorityStrings.add(authority.getAuthority());
        }
        return jwtTokenPair(audience, authorityStrings, additional);
    }

    private String jwtToken(String audience, int tokenExpirationDays, Set<String> authorities, Map<String, String> additional) throws JsonProcessingException {
        String payload = jwtPayloadBuilder
                .issuer(jwtProperties.getIssuer())
                .subscriber(jwtProperties.getSubscriber())
                .audience(audience)
                .additional(additional)
                .authorities(authorities)
                .expirationDays(tokenExpirationDays)
                .builder();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RsaSigner signer = new RsaSigner(privateKey);
        return JwtHelper.encode(payload, signer).getEncoded();
    }

    public JSONObject decodeAndVerify(String jwtToken) throws JsonProcessingException {
        Assert.hasText(jwtToken, "jwtToken should not be null!");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) this.keyPair.getPublic();
        SignatureVerifier signatureVerifier = new RsaVerifier(rsaPublicKey);
        Jwt jwt = JwtHelper.decodeAndVerify(jwtToken, signatureVerifier);
        String claims = jwt.getClaims();
        JSONObject jsonObject = JSONObject.parseObject(claims);
        String expiration = (String) jsonObject.get("expiration");
        if (LocalDateTime.now().isAfter(LocalDateTime.parse(expiration, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))) {
            throw new JwtExpiredAuthenticationException("jwt has been expired!");
        }
        return jsonObject;
    }

}
