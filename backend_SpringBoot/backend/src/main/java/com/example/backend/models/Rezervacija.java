package com.example.backend.models;

import java.time.LocalDateTime;

public class Rezervacija {
    private int id;
    private String korisnickoIme;
    private int terenId;
    private LocalDateTime vremeOd;
    private LocalDateTime vremeDo;
    private String status;

    public Rezervacija() {}


    public Rezervacija(int id, String korisnickoIme, int terenId, LocalDateTime vremeOd, LocalDateTime vremeDo,
            String status) {
        this.id = id;
        this.korisnickoIme = korisnickoIme;
        this.terenId = terenId;
        this.vremeOd = vremeOd;
        this.vremeDo = vremeDo;
        this.status = status;
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
    
}
