package com.example.backend.db;

import java.util.List;

import com.example.backend.models.Objekat;
import com.example.backend.models.Promocija;

public interface ObjekatRepoInterface {
    int getUkupanBrojAktivnih();
    List<Objekat> getTop3Aktivna();
    List<Objekat> pretraga(String naziv, String grad, String sport);
    List<String> getSviGradovi();
    List<Promocija> getTop3Promocije();
    Objekat getById(int id);
    List<String> getSlikeZaObjekat(int objekatId);
}