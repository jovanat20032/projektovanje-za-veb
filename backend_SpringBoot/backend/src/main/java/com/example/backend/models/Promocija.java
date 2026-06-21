package com.example.backend.models;

import java.sql.Date;

public class Promocija {
    private int id;
    private String naziv;
    private String objekatNaziv;
    private Date vaziOd;
    private Date vaziDo;
    private String popust;

    public Promocija(int id, String naziv, String objekatNaziv, Date vaziOd, Date vaziDo, String popust) {
        this.id = id;
        this.naziv = naziv;
        this.objekatNaziv = objekatNaziv;
        this.vaziOd = vaziOd;
        this.vaziDo = vaziDo;
        this.popust = popust;
    }

    public Promocija() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getObjekatNaziv() {
        return objekatNaziv;
    }

    public void setObjekatNaziv(String objekatNaziv) {
        this.objekatNaziv = objekatNaziv;
    }

    public Date getVaziOd() {
        return vaziOd;
    }

    public void setVaziOd(Date vaziOd) {
        this.vaziOd = vaziOd;
    }

    public Date getVaziDo() {
        return vaziDo;
    }

    public void setVaziDo(Date vaziDo) {
        this.vaziDo = vaziDo;
    }

    public String getPopust() {
        return popust;
    }

    public void setPopust(String popust) {
        this.popust = popust;
    }
    
}
