package com.excelr.util;

import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private static final String SECRET = "my-super-secret-key-which-should-be-long-enough";
    private static final SecretKey SECRET_KEY = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (username.equals(extractedUsername) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
