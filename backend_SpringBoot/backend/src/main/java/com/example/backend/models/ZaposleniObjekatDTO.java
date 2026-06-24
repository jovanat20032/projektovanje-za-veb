package com.example.backend.models;

import java.util.List;

public class ZaposleniObjekatDTO {
    private int id;
    private String naziv;
    private String grad;
    private List<String> vrsteSportova;
    private List<String> elementi;

    public ZaposleniObjekatDTO() {
    }

    public ZaposleniObjekatDTO(int id, String naziv, String grad, List<String> vrsteSportova, List<String> elementi) {
        this.id = id;
        this.naziv = naziv;
        this.grad = grad;
        this.vrsteSportova = vrsteSportova;
        this.elementi = elementi;
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

    public String getGrad() {
        return grad;
    }
    public void setGrad(String grad) {
        this.grad = grad;
    }

    public List<String> getVrsteSportova() {
        return vrsteSportova;
    }
    public void setVrsteSportova(List<String> vrsteSportova) {
        this.vrsteSportova = vrsteSportova;
    }

    public List<String> getElementi() {
        return elementi;
    }
    public void setElementi(List<String> elementi) {
        this.elementi = elementi;
    }
}
