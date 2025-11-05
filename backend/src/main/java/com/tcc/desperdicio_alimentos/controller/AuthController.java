package com.tcc.desperdicio_alimentos.controller;

import com.tcc.desperdicio_alimentos.LoginRequest;
import com.tcc.desperdicio_alimentos.RegisterRequest;
import com.tcc.desperdicio_alimentos.model.Usuario;
import com.tcc.desperdicio_alimentos.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService s) { this.authService = s; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
