package com.tss.springSecurity.security;

import com.tss.springSecurity.exception.UserApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${app-jwt-secret}")
    private String jwtSecret;

    @Value("${app-jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.startsWith("ROLE_"))
                .toList();

        String token = Jwts.builder().claims()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .and()
                .signWith(key())
                .claim("role", roles)
                .compact();

        return token;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        // returns Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }


    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch(MalformedJwtException exception){
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Invalid JWT Token");
        }catch(ExpiredJwtException exception){
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Expired JWT Token");
        }catch(UnsupportedJwtException exception){
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Unsupported JWT Token");
        }catch(IllegalArgumentException exception){
            throw new UserApiException(HttpStatus.BAD_REQUEST,"JWT claims string is empty");
        }catch(Exception exception){
            throw new UserApiException(HttpStatus.BAD_REQUEST,"Invalid Credentials");
        }
    }

    public String getUsername(String token){
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.getSubject();
        return username;
    }
}