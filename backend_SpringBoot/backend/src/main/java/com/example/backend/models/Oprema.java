package com.example.backend.models;

public class Oprema {
    private int id;
    private String sport;
    private String naziv;
    private double cena;
    private int zaliha;
    private String slika;

    public Oprema() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public double getCena() { return cena; }
    public void setCena(double cena) { this.cena = cena; }

    public int getZaliha() { return zaliha; }
    public void setZaliha(int zaliha) { this.zaliha = zaliha; }

    public String getSlika() { return slika; }
    public void setSlika(String slika) { this.slika = slika; }
}
