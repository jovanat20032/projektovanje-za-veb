package com.example.backend.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Korisnik {
    @NotBlank(message = "Korisničko ime je obavezno")
    private String korisnickoIme;
    
    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 6, message = "Lozinka mora imati barem 6 karaktera")
    private String lozinka;
    
    @NotBlank(message = "Ime je obavezno")
    private String ime;
    
    @NotBlank(message = "Prezime je obavezno")
    private String prezime;
    
    @NotBlank(message = "Telefon je obavezan")
    private String telefon;
    
    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validan")
    private String email;
    
    private String profilnaSlika;
    
    @NotBlank(message = "Uloga je obavezna")
    private String uloga;
    
    private String status;
    public String getKorisnickoIme() {
        return korisnickoIme;
    }
    public Korisnik(String korisnickoIme, String lozinka, String ime, String prezime, String telefon, String email,
            String profilnaSlika, String uloga, String status) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.email = email;
        this.profilnaSlika = profilnaSlika;
        this.uloga = uloga;
        this.status = status;
    }
    public Korisnik(){}
    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }
    public String getLozinka() {
        return lozinka;
    }
    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }
    public String getIme() {
        return ime;
    }
    public void setIme(String ime) {
        this.ime = ime;
    }
    public String getPrezime() {
        return prezime;
    }
    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }
    public String getTelefon() {
        return telefon;
    }
    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getProfilnaSlika() {
        return profilnaSlika;
    }
    public void setProfilnaSlika(String profilnaSlika) {
        this.profilnaSlika = profilnaSlika;
    }
    public String getUloga() {
        return uloga;
    }
    public void setUloga(String uloga) {
        this.uloga = uloga;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
