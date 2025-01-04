package com.pap.shopping.list.PapShoppingList.controller;

import com.pap.shopping.list.PapShoppingList.domain.User;
import com.pap.shopping.list.PapShoppingList.service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final DbService dbService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(DbService dbService, PasswordEncoder passwordEncoder) {
        this.dbService = dbService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User newUser) {
        if (dbService.getUserByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(null); // Email already exists
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        return ResponseEntity.ok(dbService.saveUser(newUser));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String email, @RequestParam String password) {
        return dbService.getUserByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> ResponseEntity.ok("Login successful"))
                .orElse(ResponseEntity.status(401).body("Invalid credentials"));
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<String> requestPasswordReset(@RequestParam String email) {
        try {
            dbService.initiatePasswordReset(email);
            return ResponseEntity.ok("Password reset email sent");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            dbService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
