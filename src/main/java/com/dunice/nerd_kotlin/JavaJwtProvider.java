package com.dunice.nerd_kotlin;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JavaJwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    private void init() {
        JwtBuilder builder = Jwts.builder()
                .setSubject("Nerd")
                .signWith(SignatureAlgorithm.HS512, jwtSecret);
        System.out.println(builder.compact());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            expEx.printStackTrace();
        } catch (UnsupportedJwtException unsEx) {
            unsEx.printStackTrace();
        } catch (MalformedJwtException mjEx) {
            mjEx.printStackTrace();
        } catch (SignatureException sEx) {
            sEx.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

