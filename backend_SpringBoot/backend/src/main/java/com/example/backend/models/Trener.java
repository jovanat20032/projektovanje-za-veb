package com.example.backend.models;

public class Trener {
    private String korisnickoIme;
    private String ime;
    private String prezime;
    private String sport;
    private String specijalizacija;
    private double prosecnaOcena;
    private int cenaPoSatu;

    public Trener() {}

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }

    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getSpecijalizacija() { return specijalizacija; }
    public void setSpecijalizacija(String specijalizacija) { this.specijalizacija = specijalizacija; }

    public double getProsecnaOcena() { return prosecnaOcena; }
    public void setProsecnaOcena(double prosecnaOcena) { this.prosecnaOcena = prosecnaOcena; }

    public int getCenaPoSatu() { return cenaPoSatu; }
    public void setCenaPoSatu(int cenaPoSatu) { this.cenaPoSatu = cenaPoSatu; }
}
