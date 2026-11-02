package com.athenea.ms_usuarios.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expiracionSegundos;

    public JwtService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiracion-segundos}") long expiracionSegundos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionSegundos = expiracionSegundos;
    }

    public String generar(String correo, Long usuarioId, List<String> roles) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(correo)
                .claim("uid", usuarioId)
                .claim("roles", roles)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plusSeconds(expiracionSegundos)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims leer(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}