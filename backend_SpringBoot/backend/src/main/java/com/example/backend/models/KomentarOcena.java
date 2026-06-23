package com.example.backend.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class KomentarOcena {
    private int id;
    private int objekatId;
    private String korisnickoIme;
    private String tekst;
    private String reakcija;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datumVreme;

    public KomentarOcena() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getObjekatId() { return objekatId; }
    public void setObjekatId(int objekatId) { this.objekatId = objekatId; }

    public String getKorisnickoIme() { return korisnickoIme; }
    public void setKorisnickoIme(String korisnickoIme) { this.korisnickoIme = korisnickoIme; }

    public String getTekst() { return tekst; }
    public void setTekst(String tekst) { this.tekst = tekst; }

    public String getReakcija() { return reakcija; }
    public void setReakcija(String reakcija) { this.reakcija = reakcija; }

    public LocalDateTime getDatumVreme() { return datumVreme; }
    public void setDatumVreme(LocalDateTime datumVreme) { this.datumVreme = datumVreme; }
}
