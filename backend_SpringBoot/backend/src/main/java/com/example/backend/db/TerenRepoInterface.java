package com.example.backend.db;

import java.util.List;

import com.example.backend.models.Teren;

public interface TerenRepoInterface {
    List<Teren> getTereniZaObjekat(int objekatId);
}
