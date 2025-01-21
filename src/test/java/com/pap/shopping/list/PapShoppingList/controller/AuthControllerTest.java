package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerTest {
    @Mock
    private DbService dbService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeAll
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanUp() {
        reset(dbService);
        reset(passwordEncoder);
    }

    @Test
    @DisplayName("Test successful user registration")
    void testRegisterUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(dbService.getUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseEntity<String> response = authController.registerUser(user);

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200");
        assertEquals("Register successful", response.getBody(), "Response body should confirm registration");
        verify(dbService, times(1)).saveUser(any(User.class));
    }

    @Test
    @DisplayName("Test registration failure due to existing user")
    void testRegisterUser_UserAlreadyExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(dbService.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<String> response = authController.registerUser(user);

        assertEquals(400, response.getStatusCodeValue(), "Response status should be 400 for bad request");
        assertNull(response.getBody(), "Response body should be null for a failed registration");
    }

    @Test
    @DisplayName("Test successful user login")
    void testLoginUser_Success() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(dbService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        ResponseEntity<String> response = authController.loginUser("test@example.com", "password");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200 for successful login");
        assertEquals("Login successful", response.getBody(), "Response body should confirm login success");
    }

    @Test
    @DisplayName("Test login failure due to invalid credentials")
    void testLoginUser_InvalidCredentials() {
        when(dbService.getUserByEmail("invalid@example.com")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.loginUser("invalid@example.com", "password");

        assertEquals(401, response.getStatusCodeValue(), "Response status should be 401 for invalid credentials");
        assertEquals("Invalid credentials", response.getBody(), "Response body should indicate invalid credentials");
    }

    @Test
    @DisplayName("Test password reset request for an existing user")
    void testRequestPasswordReset_ExistingUser() {
        when(dbService.getUserByEmail("test@example.com")).thenReturn(Optional.of(new User()));

        ResponseEntity<String> response = authController.requestPasswordReset("test@example.com");

        assertEquals(200, response.getStatusCodeValue(), "Response status should be 200 for successful password reset request");
        assertEquals("If the email exists, a password reset link will be sent.", response.getBody());
        verify(dbService, times(1)).initiatePasswordReset("test@example.com");
    }

    @Test
    @DisplayName("Test password reset request for a non-existing user")
    void testRequestPasswordReset_NonExistingUser() {
        when(dbService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authController.requestPasswordReset("nonexistent@example.com");

        assertEquals(200, response.getStatusCodeValue(), "Response status should still be 200 for non-existing user");
        assertEquals("If the email exists, a password reset link will be sent.", response.getBody());
        verify(dbService, never()).initiatePasswordReset(anyString());
    }
}
