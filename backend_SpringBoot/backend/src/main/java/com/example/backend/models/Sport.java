package com.example.backend.models;

public class Sport {
    private int id;
    private String naziv;
    private boolean timski;

    public Sport() {}

    public Sport(int id, String naziv, boolean timski) {
        this.id = id;
        this.naziv = naziv;
        this.timski = timski;
    }

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

    public boolean isTimski() {
        return timski;
    }

    public void setTimski(boolean timski) {
        this.timski = timski;
    }
}