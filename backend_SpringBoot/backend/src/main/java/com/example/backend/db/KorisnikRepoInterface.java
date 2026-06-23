package com.example.backend.db;

import java.util.List;

import com.example.backend.models.Korisnik;
import com.example.backend.models.RezervacijaDTO;
import com.example.backend.models.Sport;

public interface KorisnikRepoInterface {
    Korisnik findByKorisnickoIme(String korisnickoIme);
    boolean save(Korisnik korisnik);
    Korisnik findByKorisnickoImeIliEmail(String unos);
    boolean azurirajLozinku(String korisnickoIme, String enkriptovanaLozinka);
    boolean azurirajKorisnika(Korisnik korisnik);
    List<Sport> dohvatiSveSportove();
    List<String> dohvatiOmiljeneSportove(String korisnickoIme);
    boolean azurirajOmiljeneSportove(String korisnickoIme, List<String> sportovi);
    List<RezervacijaDTO> dohvatiRezervacijeKorisnika(String korisnickoIme);
    boolean otkaziRezervaciju(int id);
}