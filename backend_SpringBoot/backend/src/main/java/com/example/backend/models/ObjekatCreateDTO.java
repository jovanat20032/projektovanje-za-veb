package com.example.backend.models;

import java.util.List;

public class ObjekatCreateDTO {
    private String naziv;
    private String grad;
    private String adresa;
    private String maticniBroj;
    private String pib;
    private double cenaPoSatu;
    private String radnoVreme;
    private int dozvoljeniMinusi;
    private List<Teren> tereni;
    private List<String> vrsteSportova;

    public ObjekatCreateDTO() {}

    public List<String> getVrsteSportova() {
        return vrsteSportova;
    }

    public void setVrsteSportova(List<String> vrsteSportova) {
        this.vrsteSportova = vrsteSportova;
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

    public List<Teren> getTereni() {
        return tereni;
    }

    public void setTereni(List<Teren> tereni) {
        this.tereni = tereni;
    }
}
