package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import com.careapp.utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("family-001");
        mockUser.setUname("bob.family@example.com");
        mockUser.setPassword("abc12345");
        mockUser.setRole("FAMILY");
    }

    @Test
    void testRegisterSuccess() throws Exception {
        Mockito.when(userService.registerService(Mockito.any(User.class)))
                .thenReturn(mockUser);

        String requestBody = """
            {
              "id": "family-001",
              "uname": "bob.family@example.com",
              "password": "abc12345",
              "role": "FAMILY"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Registration successful!"))
                .andExpect(jsonPath("$.data.id").value("family-001"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        Mockito.when(userService.loginService("bob.family@example.com", "abc12345"))
                .thenReturn(mockUser);

        String requestBody = """
            {
              "uname": "bob.family@example.com",
              "password": "abc12345"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Login successful!"))
                .andExpect(jsonPath("$.data.uname").value("bob.family@example.com"));
    }

    @Test
    void testLoginFailure() throws Exception {
        Mockito.when(userService.loginService("wrongUser", "wrongPass"))
                .thenReturn(null);

        String requestBody = """
            {
              "uname": "wrongUser",
              "password": "wrongPass"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())  // 控制器始终返回200，但内部 code=401
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.msg").value("Invalid username or password!"));
    }
}

