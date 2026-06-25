package com.example.backend.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PromenaLozinkeDTO {
    @NotBlank(message = "Token je obavezan.")
    private String token;

    @NotBlank(message = "Nova lozinka je obavezna.")
    @Size(min = 6, message = "Lozinka mora imati barem 6 karaktera.")
    private String novaLozinka;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNovaLozinka() {
        return novaLozinka;
    }

    public void setNovaLozinka(String novaLozinka) {
        this.novaLozinka = novaLozinka;
    }
}
