package com.example.backend.db;

import com.example.backend.models.Korisnik;

public interface KorisnikRepoInterface {
    Korisnik findByKorisnickoIme(String korisnickoIme);
    boolean save(Korisnik korisnik);
    Korisnik findByKorisnickoImeIliEmail(String unos);
    boolean azurirajLozinku(String korisnickoIme, String enkriptovanaLozinka);
}