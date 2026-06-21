package com.example.backend.models;

public class Objekat {
    private int id;
    private String naziv;
    private String grad;
    private String adresa;
    private String maticniBroj;
    private String pib;
    private double cenaPoSatu;
    private String radnoVreme;
    private int dozvoljeniMinusi;
    private int lajkovi;
    private int dislajkovi;
    private String status;
    private String sport;
    public int getId() {
        return id;
    }
    public String getSport() {
        return sport;
    }
    public void setSport(String sport) {
        this.sport = sport;
    }
    public Objekat(int id, String naziv, String grad, String adresa, String maticniBroj, String pib, double cenaPoSatu,
            String radnoVreme, int dozvoljeniMinusi, int lajkovi, int dislajkovi, String status, String sport) {
        this.id = id;
        this.naziv = naziv;
        this.grad = grad;
        this.adresa = adresa;
        this.maticniBroj = maticniBroj;
        this.pib = pib;
        this.cenaPoSatu = cenaPoSatu;
        this.radnoVreme = radnoVreme;
        this.dozvoljeniMinusi = dozvoljeniMinusi;
        this.lajkovi = lajkovi;
        this.dislajkovi = dislajkovi;
        this.status = status;
        this.sport = sport;
    }
    public Objekat(){}
    public void setId(int id) {
        this.id = id;
    }
    public String getNaziv() {
        return naziv;
    }
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    public String getGrad() {
        return grad;
    }
    public void setGrad(String grad) {
        this.grad = grad;
    }
    public String getAdresa() {
        return adresa;
    }
    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }
    public String getMaticniBroj() {
        return maticniBroj;
    }
    public void setMaticniBroj(String maticniBroj) {
        this.maticniBroj = maticniBroj;
    }
    public String getPib() {
        return pib;
    }
    public void setPib(String pib) {
        this.pib = pib;
    }
    public double getCenaPoSatu() {
        return cenaPoSatu;
    }
    public void setCenaPoSatu(double cenaPoSatu) {
        this.cenaPoSatu = cenaPoSatu;
    }
    public String getRadnoVreme() {
        return radnoVreme;
    }
    public void setRadnoVreme(String radnoVreme) {
        this.radnoVreme = radnoVreme;
    }
    public int getDozvoljeniMinusi() {
        return dozvoljeniMinusi;
    }
    public void setDozvoljeniMinusi(int dozvoljeniMinusi) {
        this.dozvoljeniMinusi = dozvoljeniMinusi;
    }
    public int getLajkovi() {
        return lajkovi;
    }
    public void setLajkovi(int lajkovi) {
        this.lajkovi = lajkovi;
    }
    public int getDislajkovi() {
        return dislajkovi;
    }
    public void setDislajkovi(int dislajkovi) {
        this.dislajkovi = dislajkovi;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
}
