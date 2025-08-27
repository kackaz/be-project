package com.example.beproject.auth.service;

import com.example.beproject.auth.dto.LoginRequest;
import com.example.beproject.auth.dto.RegisterRequest;
import com.example.beproject.auth.model.User;
import com.example.beproject.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Date;

@Service
public class AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final byte[] jwtSecret;

    public AuthService(UserRepository repo, @Value("${app.jwt.secret:defaultsecretkeydefaultsecret}") String secret) {
        this.repo = repo;
        this.jwtSecret = secret.getBytes();
    }

    @PostConstruct
    public void init() throws Exception {
        repo.init();
    }

    public User register(RegisterRequest r) throws Exception {
        User u = new User();
        u.email = r.email;
        u.password = encoder.encode(r.password);
        u.firstname = r.firstname;
        u.lastname = r.lastname;
        u.phone = r.phone;
        u.birthday = r.birthday;
        repo.save(u);
        return u;
    }

    public String login(LoginRequest r) throws Exception {
        User u = repo.findByEmail(r.email);
        if (u == null) return null;
        if (!encoder.matches(r.password, u.password)) return null;
        return Jwts.builder()
                .setSubject(String.valueOf(u.id))
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(jwtSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public User me(Long id) throws Exception {
        return repo.findById(id);
    }
}
