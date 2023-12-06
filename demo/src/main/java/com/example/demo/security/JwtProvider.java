package com.example.demo.security;

import com.example.demo.exception.SpringRedditException;
import com.example.demo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

@Service

public class JwtProvider {
    private KeyStore keyStore;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;
    @PostConstruct
    public void init() throws SpringRedditException {
        try {
            keyStore=KeyStore.getInstance("JKS");
            InputStream resourceAsStream=getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream,"secret".toCharArray());

        }catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e)
        {
            throw  new SpringRedditException("Exception occured while loading keystore");
        }
    }
    public String generateToken(Authentication authentication) throws SpringRedditException {
        org.springframework.security.core.userdetails.User user= (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return Jwts.builder().setSubject(user.getUsername())
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();

    }
    public String generateTokenWithUserName(String username) throws SpringRedditException {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey() throws SpringRedditException {
        try {
            return (PrivateKey)keyStore.getKey("springblog","secret".toCharArray());

        }catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e)
        {
            throw new SpringRedditException("Esception occured while retriviving public keystore");
        }
    }
    //for authorization
    public boolean validateToken(String jwt) throws SpringRedditException {
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;

    }

    private PublicKey getPublicKey() throws SpringRedditException {
        try {
            return keyStore.getCertificate("springblog").getPublicKey();

        }catch (KeyStoreException e)
        {
            throw new SpringRedditException("Exception occured when retriving key");
        }
    }
    public String  getUsernameFromJwt(String token) throws SpringRedditException {
        Claims claims=parser().setSigningKey(getPublicKey())
                .parseClaimsJws(token).getBody();
        return claims.getSubject();

    }

    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }
}
