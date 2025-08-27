package com.example.beproject.auth.controller;

import com.example.beproject.auth.dto.LoginRequest;
import com.example.beproject.auth.dto.RegisterRequest;
import com.example.beproject.auth.model.User;
import com.example.beproject.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthService svc;
    private final byte[] jwtSecret;

    public AuthController(AuthService svc, @Value("${app.jwt.secret:defaultsecretkeydefaultsecret}") String secret) {
        this.svc = svc;
        this.jwtSecret = secret.getBytes();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) throws Exception {
        User u = svc.register(req);
        return ResponseEntity.ok(Map.of("id", u.id, "email", u.email));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) throws Exception {
        String token = svc.login(req);
        if (token == null) return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return ResponseEntity.status(401).body(Map.of("error", "missing token"));
        String token = auth.substring(7);
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtSecret)).build().parseClaimsJws(token);
        Long id = Long.parseLong(claims.getBody().getSubject());
        User u = svc.me(id);
        if (u == null) return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        return ResponseEntity.ok(Map.of("id", u.id, "email", u.email, "firstname", u.firstname, "lastname", u.lastname, "phone", u.phone, "birthday", u.birthday));
    }
}
