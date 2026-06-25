package com.example.backend.models;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "Korisničko ime je obavezno.")
    private String korisnickoIme;

    @NotBlank(message = "Lozinka je obavezna.")
    private String lozinka;

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }
}
