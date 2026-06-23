package com.example.backend.controllers;

import com.example.backend.db.OglasRepo;
import com.example.backend.models.Oglas;
import com.example.backend.models.ZahtevOglasa;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oglasi")
@CrossOrigin(origins = "http://localhost:4200")
public class OglasController {

    private OglasRepo oglasRepo = new OglasRepo();

    @GetMapping("")
    public ResponseEntity<?> getAllAktivniOglasi() {
        List<Oglas> oglasi = oglasRepo.getAllAktivniOglasi();
        return ResponseEntity.ok(oglasi);
    }

    @GetMapping("/korisnik/{korisnickoIme}")
    public ResponseEntity<?> getOglasiByKorisnik(@PathVariable String korisnickoIme) {
        List<Oglas> oglasi = oglasRepo.getOglasiByKorisnik(korisnickoIme);
        return ResponseEntity.ok(oglasi);
    }

    @PostMapping("")
    public ResponseEntity<?> kreirajOglas(@RequestBody Oglas oglas) {
        boolean uspeh = oglasRepo.kreirajOglas(oglas);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Oglas uspesno kreiran\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri kreiranju oglasa\"}");
        }
    }

    @PutMapping("/{id}/zatvori")
    public ResponseEntity<?> zatvoriOglas(@PathVariable int id) {
        boolean uspeh = oglasRepo.zatvoriOglas(id);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Oglas zatvoren\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri zatvaranju oglasa\"}");
        }
    }

    @PostMapping("/{id}/prijava")
    public ResponseEntity<?> prijaviSe(@PathVariable int id, @RequestBody Map<String, String> payload) {
        String korisnickoIme = payload.get("korisnickoIme");
        boolean uspeh = oglasRepo.prijaviSeNaOglas(id, korisnickoIme);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Prijava uspesno poslata\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Vec ste prijavljeni ili je doslo do greske\"}");
        }
    }

    @GetMapping("/{id}/zahtevi")
    public ResponseEntity<?> getZahtevi(@PathVariable int id) {
        List<ZahtevOglasa> zahtevi = oglasRepo.getZahteviZaOglas(id);
        return ResponseEntity.ok(zahtevi);
    }

    @PutMapping("/zahtev/{id}/status")
    public ResponseEntity<?> azurirajStatusZahteva(@PathVariable int id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        boolean uspeh = oglasRepo.azurirajStatusZahteva(id, status);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Status azuriran\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri azuriranju statusa\"}");
        }
    }
}
