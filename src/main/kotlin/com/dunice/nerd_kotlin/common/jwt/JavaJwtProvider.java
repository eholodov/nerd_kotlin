package com.dunice.nerd_kotlin.common.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JavaJwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

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
