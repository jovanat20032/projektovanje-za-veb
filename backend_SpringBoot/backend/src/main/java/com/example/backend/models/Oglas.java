package com.example.backend.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Oglas {
    private int id;
    private String korisnickoIme;
    private String sport;
    private String grad;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datumVreme;
    
    private int nedostajeIgraca;
    private String status;

    public Oglas() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getGrad() { return grad; }
    public void setGrad(String grad) { this.grad = grad; }

    public LocalDateTime getDatumVreme() { return datumVreme; }
    public void setDatumVreme(LocalDateTime datumVreme) { this.datumVreme = datumVreme; }

    public int getNedostajeIgraca() { return nedostajeIgraca; }
    public void setNedostajeIgraca(int nedostajeIgraca) { this.nedostajeIgraca = nedostajeIgraca; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
