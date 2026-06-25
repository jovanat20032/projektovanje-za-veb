package com.example.backend.models;

import jakarta.validation.constraints.NotBlank;

public class ZahtevResetDTO {
    @NotBlank(message = "Korisničko ime ili email su obavezni.")
    private String unos;

    public String getUnos() {
        return unos;
    }

    public void setUnos(String unos) {
        this.unos = unos;
    }
}
