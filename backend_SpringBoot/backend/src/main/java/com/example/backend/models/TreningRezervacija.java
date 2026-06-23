package com.example.backend.models;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TreningRezervacija {
    private int id;
    private String trenerKorisnickoIme;
    private String sportistaKorisnickoIme;
    
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datumVreme;
    
    private String status;

    // Dodatna polja za frontend prikaz
    private String trenerIme;
    private String sport;

    public TreningRezervacija() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrenerKorisnickoIme() { return trenerKorisnickoIme; }
    public void setTrenerKorisnickoIme(String trenerKorisnickoIme) { this.trenerKorisnickoIme = trenerKorisnickoIme; }

    public String getSportistaKorisnickoIme() { return sportistaKorisnickoIme; }
    public void setSportistaKorisnickoIme(String sportistaKorisnickoIme) { this.sportistaKorisnickoIme = sportistaKorisnickoIme; }

    public LocalDateTime getDatumVreme() { return datumVreme; }
    public void setDatumVreme(LocalDateTime datumVreme) { this.datumVreme = datumVreme; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrenerIme() { return trenerIme; }
    public void setTrenerIme(String trenerIme) { this.trenerIme = trenerIme; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }
}
