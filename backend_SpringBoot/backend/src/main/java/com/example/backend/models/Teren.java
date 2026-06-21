package com.example.backend.models;

public class Teren {
    private int id;
    private int objekatId;
    private String naziv;
    private String tip;
    private int kapacitet;
    private String opisOpreme;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getObjekatId() {
        return objekatId;
    }
    public void setObjekatId(int objekatId) {
        this.objekatId = objekatId;
    }
    public String getNaziv() {
        return naziv;
    }
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }
    public int getKapacitet() {
        return kapacitet;
    }
    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }
    public String getOpisOpreme() {
        return opisOpreme;
    }
    public void setOpisOpreme(String opisOpreme) {
        this.opisOpreme = opisOpreme;
    }
    public Teren(int id, int objekatId, String naziv, String tip, int kapacitet, String opisOpreme) {
        this.id = id;
        this.objekatId = objekatId;
        this.naziv = naziv;
        this.tip = tip;
        this.kapacitet = kapacitet;
        this.opisOpreme = opisOpreme;
    }
    public Teren() {}
    
}
