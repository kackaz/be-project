package com.example.beproject.auth.controller;

import com.example.beproject.auth.dto.LoginRequest;
import com.example.beproject.auth.dto.RegisterRequest;
import com.example.beproject.auth.model.User;
import com.example.beproject.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

public class AuthControllerTest {

    private MockMvc mvc;

    @Mock
    private AuthService svc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthController controller = new AuthController(svc, "testsecrettestsecrettestsecret");
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void register_returns_ok() throws Exception {
        RegisterRequest r = new RegisterRequest();
        r.email = "a@b.com";
        r.password = "p";
        r.firstname = "F";
        r.lastname = "L";
        r.phone = "012";
        r.birthday = "2000-01-01";
        User u = new User();
        u.id = 5L; u.email = r.email;
        when(svc.register(any(RegisterRequest.class))).thenReturn(u);

        mvc.perform(post("/register").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"a@b.com\",\"password\":\"p\",\"firstname\":\"F\",\"lastname\":\"L\",\"phone\":\"012\",\"birthday\":\"2000-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.email", is("a@b.com")));
    }

    @Test
    void login_returns_token() throws Exception {
        LoginRequest lr = new LoginRequest();
        lr.email = "u@u.com"; lr.password = "s";
        when(svc.login(any(LoginRequest.class))).thenReturn("tok123");

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\":\"u@u.com\",\"password\":\"s\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("tok123")));
    }

    @Test
    void me_requires_auth_header() throws Exception {
        mvc.perform(get("/me")).andExpect(status().isUnauthorized());
    }
}
