package com.example.backend.controllers;

import com.example.backend.db.RezervacijaRepo;
import com.example.backend.models.Rezervacija;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rezervacije")
@CrossOrigin(origins = "http://localhost:4200")
public class RezervacijaController {

    private RezervacijaRepo rezervacijaRepo = new RezervacijaRepo();

    @GetMapping("/teren/{terenId}")
    public ResponseEntity<?> getRezervacijeZaTeren(@PathVariable int terenId) {
        List<Rezervacija> rezervacije = rezervacijaRepo.getRezervacijeZaTeren(terenId);
        return ResponseEntity.ok(rezervacije);
    }

    @PostMapping("")
    public ResponseEntity<?> dodajRezervaciju(@RequestBody Rezervacija rezervacija) {
        boolean uspeh = rezervacijaRepo.dodajRezervaciju(rezervacija);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Rezervacija uspesna\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Termin je vec zauzet, doslo do greske, ili nemate pravo rezervacije zbog previse nedolazaka.\"}");
        }
    }

    @GetMapping("/zaposleni/{korisnickoIme}")
    public ResponseEntity<?> getRezervacijeZaZaposlenog(@PathVariable String korisnickoIme) {
        List<Rezervacija> rezervacije = rezervacijaRepo.getRezervacijeZaZaposlenog(korisnickoIme);
        return ResponseEntity.ok(rezervacije);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> azurirajStatusRezervacije(@PathVariable int id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        boolean uspeh = rezervacijaRepo.azurirajStatus(id, status);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Status uspesno azuriran\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri azuriranju statusa\"}");
        }
    }

    @PutMapping("/{id}/pomeri")
    public ResponseEntity<?> pomeriRezervaciju(@PathVariable int id, @RequestBody java.util.Map<String, String> body) {
        try {
            java.time.LocalDateTime novoVremeOd = java.time.LocalDateTime.parse(body.get("vremeOd"));
            java.time.LocalDateTime novoVremeDo = java.time.LocalDateTime.parse(body.get("vremeDo"));
            
            boolean uspeh = rezervacijaRepo.pomeriRezervaciju(id, novoVremeOd, novoVremeDo);
            if (uspeh) {
                return ResponseEntity.ok().body("{\"message\": \"Rezervacija uspesno pomerena\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Termin je zauzet ili se desila greska\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Nevalidan format datuma\"}");
        }
    }
}
