package com.example.backend.models;

public class Korisnik {
    private String korisnickoIme;
    private String lozinka;
    private String ime;
    private String prezime;
    private String telefon;
    private String email;
    private String profilnaSlika;
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
