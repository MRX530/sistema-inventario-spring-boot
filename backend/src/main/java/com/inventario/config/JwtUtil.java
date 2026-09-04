package com.inventario.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // En un proyecto real, esta clave va en application.properties (o variable de entorno),
    // no fija en el codigo. Se deja aqui simplificado para portafolio.
    private final SecretKey key = Keys.hmacShaKeyFor(
            "clave-secreta-super-larga-para-firmar-tokens-jwt-1234567890".getBytes());

    private static final long EXPIRACION_MS = 1000 * 60 * 60 * 8; // 8 horas

    // Genera un token que "contiene" el email del usuario y cuando expira.
    // El token va firmado con la clave: si alguien lo modifica, la firma no cuadra
    // y Spring lo rechaza (asi se garantiza que no fue alterado).
    public String generarToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRACION_MS))
                .signWith(key)
                .compact();
    }

    public String extraerEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean esValido(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
