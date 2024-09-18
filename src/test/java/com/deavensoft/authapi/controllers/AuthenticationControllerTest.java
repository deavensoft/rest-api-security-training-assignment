package com.deavensoft.authapi.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class AuthenticationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testLoginLockAfterThreeFailedAttempts() throws Exception {
        String email = "testuser@example.com";
        String incorrectPassword = "wrong password";
        String correctPassword = "correct password";

        String loginJson = createRequestBody(email, incorrectPassword, null);
        String correctSignupJson = createRequestBody(email, correctPassword, "Test User");

        // Signup a new user
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctSignupJson))
                .andExpect(status().isOk());

        // Perform 3 failed login attempts
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(containsString("Bad credentials")));
        }

        // 4th attempt should result in blocking
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().is4xxClientError())  // Adjust status as per your actual exception handler
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assert responseContent.contains("The account is locked");
    }

    @Test
    void testLoginSuccessResetsFailedAttempts() throws Exception {
        String email = "testuser2@example.com";
        String correctPassword = "correct password";
        String incorrectPassword = "wrong password";

        String incorrectLoginJson = createRequestBody(email, incorrectPassword, null);
        String correctLoginJson = createRequestBody(email, correctPassword, null);
        String correctSignupJson = createRequestBody(email, correctPassword, "Test User");

        // Signup a new user
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctSignupJson))
                .andExpect(status().isOk());

        // Perform 2 failed login attempts
        for (int i = 0; i < 2; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(incorrectLoginJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(containsString("Bad credentials")));
        }

        // Perform a successful login attempt
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctLoginJson))
                .andExpect(status().isOk());

        // Another failed attempt should not result in blocking
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incorrectLoginJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Bad credentials")));
    }


    @Test
    void testLoginSuccessAfterBlockAndAfterSomeTimeResetsFailedAttempts() throws Exception {
        String email = "testuser3@example.com";
        String correctPassword = "correct password";
        String incorrectPassword = "wrong password";

        String incorrectLoginJson = createRequestBody(email, incorrectPassword, null);
        String correctLoginJson = createRequestBody(email, correctPassword, null);
        String correctSignupJson = createRequestBody(email, correctPassword, "Test User");

        // Signup a new user
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctSignupJson))
                .andExpect(status().isOk());

        // Perform 3 failed login attempts
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(incorrectLoginJson))
                    .andExpect(status().is4xxClientError())
                    .andExpect(content().string(containsString("Bad credentials")));
        }

        // Another successful attempt should fail since the user is blocked
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctLoginJson))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("The account is locked")));

        // wait for 10 seconds
        Thread.sleep(10000);

        // Perform a successful login attempt
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(correctLoginJson))
                .andExpect(status().isOk());

    }

    private String createRequestBody(String email, String correctPassword, String fullName) {
        String json = "{\"email\":\"" + email + "\", \"password\":\"" + correctPassword;

        if (fullName != null) {
            json += "\", \"fullName\":\"" + fullName;
        }

        json += "\"}";

        return json;
    }
}