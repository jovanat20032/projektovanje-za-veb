package com.example.backend.models;

public class ZahtevOglasa {
    private int id;
    private int oglasId;
    private String korisnickoIme;
    private String status;

    public ZahtevOglasa() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOglasId() { return oglasId; }
    public void setOglasId(int oglasId) { this.oglasId = oglasId; }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
