package com.example.backend.models;

import java.time.LocalDate;

public class Promocija {
    private int id;
    private String naziv;
    private int objekatId;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate vaziOd;

    @com.fasterxml.jackson.annotation.JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate vaziDo;

    private String popust;
    private String sport;

    // Dodatno polje za front
    private String nazivObjekta;

    public Promocija() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNaziv() { return naziv; }
    public void setNaziv(String naziv) { this.naziv = naziv; }

    public int getObjekatId() { return objekatId; }
    public void setObjekatId(int objekatId) { this.objekatId = objekatId; }

    public LocalDate getVaziOd() { return vaziOd; }
    public void setVaziOd(LocalDate vaziOd) { this.vaziOd = vaziOd; }

    public LocalDate getVaziDo() { return vaziDo; }
    public void setVaziDo(LocalDate vaziDo) { this.vaziDo = vaziDo; }

    public String getPopust() { return popust; }
    public void setPopust(String popust) { this.popust = popust; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getNazivObjekta() { return nazivObjekta; }
    public void setNazivObjekta(String nazivObjekta) { this.nazivObjekta = nazivObjekta; }
}
