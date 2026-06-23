package com.example.backend.db;

import java.util.List;
import com.example.backend.models.Rezervacija;

public interface RezervacijaRepoInterface {
    List<Rezervacija> getRezervacijeZaTeren(int terenId);
    boolean dodajRezervaciju(Rezervacija rezervacija);
}
