package com.example.backend.models;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

public class Porudzbina {
    private int id;
    private String korisnickoIme;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datumVreme;
    
    private String status;
    private double ukupnaCena;

    private List<StavkaPorudzbine> stavke;

    public Porudzbina() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public LocalDateTime getDatumVreme() { return datumVreme; }
    public void setDatumVreme(LocalDateTime datumVreme) { this.datumVreme = datumVreme; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getUkupnaCena() { return ukupnaCena; }
    public void setUkupnaCena(double ukupnaCena) { this.ukupnaCena = ukupnaCena; }

    public List<StavkaPorudzbine> getStavke() { return stavke; }
    public void setStavke(List<StavkaPorudzbine> stavke) { this.stavke = stavke; }
}
