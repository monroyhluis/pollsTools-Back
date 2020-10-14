package com.desarrollo.luis.security;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtService {

	private static final int EXPIRATION_TIME = 1000 * 60 * 60;
	private static final String AUTHORITIES = "authorities";
	private final String SECRET_KEY;
	
	public JwtService() {
		SECRET_KEY = Base64.getEncoder().encodeToString("key".getBytes());
	}
	
	public String createToken(UserDetails userDetails) {
		String username = userDetails.getUsername();
		Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
		return Jwts.builder()
				.setSubject(username)
				.claim(AUTHORITIES, authorities)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET_KEY)
				.compact();
	}
	
	public Boolean hasTokenExpired(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.parseClaimsJws(token)
				.getBody()
				.getExpiration()
				.before(new Date());
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (userDetails.getUsername().equals(username) && !hasTokenExpired(token));

    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}