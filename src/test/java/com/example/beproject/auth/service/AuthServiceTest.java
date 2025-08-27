package com.example.beproject.auth.service;

import com.example.beproject.auth.dto.LoginRequest;
import com.example.beproject.auth.dto.RegisterRequest;
import com.example.beproject.auth.model.User;
import com.example.beproject.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UserRepository repo;

    private AuthService svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        svc = new AuthService(repo, "testsecretkeytestsecretkeytestsecret");
    }

    @Test
    void register_should_save_user_and_return_with_id() throws Exception {
        // arrange
        RegisterRequest r = new RegisterRequest();
        r.email = "new@example.com";
        r.password = "pass";
        r.firstname = "First";
        r.lastname = "Last";
        r.phone = "0123";
        r.birthday = "2000-01-01";

        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.id = 42L; // simulate generated id
            return null;
        }).when(repo).save(any(User.class));

        // act
        User out = svc.register(r);

        // assert
        assertNotNull(out);
        assertEquals(42L, out.id);
        assertEquals("new@example.com", out.email);
        // password should be hashed
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        assertTrue(enc.matches("pass", out.password));
    }

    @Test
    void login_should_return_token_when_credentials_ok() throws Exception {
        // arrange
        LoginRequest lr = new LoginRequest();
        lr.email = "user@example.com";
        lr.password = "secret";

        User stored = new User();
        stored.id = 7L;
        stored.email = lr.email;
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        stored.password = enc.encode(lr.password);

        when(repo.findByEmail(lr.email)).thenReturn(stored);

        // act
        String token = svc.login(lr);

        // assert
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void login_should_return_null_when_bad_credentials() throws Exception {
        LoginRequest lr = new LoginRequest();
        lr.email = "noone@example.com";
        lr.password = "bad";

        when(repo.findByEmail(lr.email)).thenReturn(null);
        String token = svc.login(lr);
        assertNull(token);
    }
}
