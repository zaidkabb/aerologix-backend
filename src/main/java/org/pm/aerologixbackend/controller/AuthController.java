package org.pm.aerologixbackend.controller1;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pm.aerologixbackend.dto.AuthResponseDTO;
import org.pm.aerologixbackend.dto.LoginRequestDTO;
import org.pm.aerologixbackend.dto.RegisterRequestDTO;
import org.pm.aerologixbackend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}