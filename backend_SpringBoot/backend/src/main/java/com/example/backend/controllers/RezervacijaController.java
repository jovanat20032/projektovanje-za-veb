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
            return ResponseEntity.badRequest().body("{\"message\": \"Termin je vec zauzet ili je doslo do greske\"}");
        }
    }
}
