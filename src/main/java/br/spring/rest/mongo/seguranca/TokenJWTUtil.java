/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.spring.rest.mongo.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 *
 * @author Renan
 */
public class TokenJWTUtil {

    private static KeyGenerator keyGenerator = new KeyGenerator();

    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    public static String gerarToken(String username, List<String> roles) {
        Key key = keyGenerator.generateKey();

        String jwtToken = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuer("GedOnline")
                .setIssuedAt(new Date())
                .setExpiration(toDate(LocalDateTime.now().plusDays(30L)))
                .claim("roles", roles)
                .compact();
        return jwtToken;
    }

    public static Authentication getByToken(String token) {

        token = token.replace(TOKEN_PREFIX, "").trim();
        String id = recuperarID(token, keyGenerator.generateKey());

        return id != null ? new UsernamePasswordAuthenticationToken(id, null, null) : null;
    }

    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            return getByToken(token);
        }
        return null;
    }

    public static boolean tokenValido(String token, Key key) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String recuperarID(String token, Key key) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        return claimsJws.getBody().getSubject();
    }

    public static List<String> recuperarRoles(String token, Key key) {
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
        return claimsJws.getBody().get("roles", List.class);
    }

    private static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
