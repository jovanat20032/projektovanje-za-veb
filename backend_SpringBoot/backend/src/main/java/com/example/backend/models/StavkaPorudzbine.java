package com.example.backend.models;

public class StavkaPorudzbine {
    private int id;
    private int porudzbinaId;
    private int opremaId;
    private int kolicina;
    private double cenaPoKomadu;
    
    // Dodatno polje za frontend prikaz
    private String opremaNaziv;

    public StavkaPorudzbine() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPorudzbinaId() { return porudzbinaId; }
    public void setPorudzbinaId(int porudzbinaId) { this.porudzbinaId = porudzbinaId; }

    public int getOpremaId() { return opremaId; }
    public void setOpremaId(int opremaId) { this.opremaId = opremaId; }

    public int getKolicina() { return kolicina; }
    public void setKolicina(int kolicina) { this.kolicina = kolicina; }

    public double getCenaPoKomadu() { return cenaPoKomadu; }
    public void setCenaPoKomadu(double cenaPoKomadu) { this.cenaPoKomadu = cenaPoKomadu; }

    public String getOpremaNaziv() { return opremaNaziv; }
    public void setOpremaNaziv(String opremaNaziv) { this.opremaNaziv = opremaNaziv; }
}
