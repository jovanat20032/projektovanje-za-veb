package com.example.backend.models;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class RezervacijaDTO {
    public int id;
    public String nazivObjekta;
    public String grad;
    public String nazivTerena;
    public String sport;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime vremeOd;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime vremeDo;
    public String status;

    public RezervacijaDTO(int id, String nazivObjekta, String grad, String nazivTerena, String sport, LocalDateTime vremeOd, LocalDateTime vremeDo, String status) {
        this.id = id;
        this.nazivObjekta = nazivObjekta;
        this.grad = grad;
        this.nazivTerena = nazivTerena;
        this.sport = sport;
        this.vremeOd = vremeOd;
        this.vremeDo = vremeDo;
        this.status = status;
    }
}
