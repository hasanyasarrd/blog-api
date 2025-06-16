package com.example.blog.controller;

import com.example.blog.dto.JwtResponse;
import com.example.blog.dto.LoginRequest;
import com.example.blog.dto.SignupRequest;
import com.example.blog.entity.User;
import com.example.blog.security.JwtUtils;
import com.example.blog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    AuthService authService;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("Login attempt for user: " + loginRequest.getUsername());
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), 
                        loginRequest.getPassword())
            );
            
            System.out.println("Authentication successful for: " + loginRequest.getUsername());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((User) authentication.getPrincipal());
            
            User userDetails = (User) authentication.getPrincipal();
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail()));
                    
        } catch (BadCredentialsException e) {
            System.out.println("Bad credentials for user: " + loginRequest.getUsername());
            return ResponseEntity.badRequest().body("Error: Invalid username or password!");
        } catch (AuthenticationException e) {
            System.out.println("Authentication exception: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: Authentication failed - " + e.getMessage());
        } catch (Exception e) {
            System.out.println("General exception during login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User user = authService.registerUser(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    signUpRequest.getPassword()
            );
            
            return ResponseEntity.ok("User registered successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
