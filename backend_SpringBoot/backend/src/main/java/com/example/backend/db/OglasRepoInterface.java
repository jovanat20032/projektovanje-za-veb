package com.example.backend.db;

import com.example.backend.models.Oglas;
import com.example.backend.models.ZahtevOglasa;

import java.util.List;

public interface OglasRepoInterface {
    List<Oglas> getAllAktivniOglasi();
    List<Oglas> getOglasiByKorisnik(String korisnickoIme);
    boolean kreirajOglas(Oglas oglas);
    boolean zatvoriOglas(int oglasId);
    boolean prijaviSeNaOglas(int oglasId, String korisnickoIme);
    List<ZahtevOglasa> getZahteviZaOglas(int oglasId);
    boolean azurirajStatusZahteva(int zahtevId, String status);
}
