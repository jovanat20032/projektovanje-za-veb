package com.example.backend.models;

import java.time.LocalDateTime;

public class Rezervacija {
    private int id;
    private String korisnickoIme;
    private int terenId;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime vremeOd;
    
    @com.fasterxml.jackson.annotation.JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime vremeDo;
    
    private String status;
    private String sport;

    // Dodatna polja za frontend
    private String korisnikIme;
    private String nazivTerena;
    private String nazivObjekta;

    public Rezervacija() {}

    public String getKorisnikIme() { return korisnikIme; }
    public void setKorisnikIme(String korisnikIme) { this.korisnikIme = korisnikIme; }

    public String getNazivTerena() { return nazivTerena; }
    public void setNazivTerena(String nazivTerena) { this.nazivTerena = nazivTerena; }

    public String getNazivObjekta() { return nazivObjekta; }
    public void setNazivObjekta(String nazivObjekta) { this.nazivObjekta = nazivObjekta; }


    public Rezervacija(int id, String korisnickoIme, int terenId, LocalDateTime vremeOd, LocalDateTime vremeDo,
            String status, String sport) {
        this.id = id;
        this.korisnickoIme = korisnickoIme;
        this.terenId = terenId;
        this.vremeOd = vremeOd;
        this.vremeDo = vremeDo;
        this.status = status;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public int getTerenId() {
        return terenId;
    }

    public void setTerenId(int terenId) {
        this.terenId = terenId;
    }

    public LocalDateTime getVremeOd() {
        return vremeOd;
    }

    public void setVremeOd(LocalDateTime vremeOd) {
        this.vremeOd = vremeOd;
    }

    public LocalDateTime getVremeDo() {
        return vremeDo;
    }

    public void setVremeDo(LocalDateTime vremeDo) {
        this.vremeDo = vremeDo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
