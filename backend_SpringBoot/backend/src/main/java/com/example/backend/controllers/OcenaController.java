package com.example.backend.controllers;

import com.example.backend.db.OcenaRepo;
import com.example.backend.models.KomentarOcena;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/ocene")
@CrossOrigin(origins = "http://localhost:4200")
public class OcenaController {

    private OcenaRepo ocenaRepo = new OcenaRepo();

    @GetMapping("/{objekatId}")
    public ResponseEntity<?> dohvatiKomentare(@PathVariable int objekatId) {
        List<KomentarOcena> komentari = ocenaRepo.dohvatiKomentare(objekatId);
        return ResponseEntity.ok(komentari);
    }

    @PostMapping("")
    public ResponseEntity<?> dodajKomentarOcenu(@RequestBody KomentarOcena ko) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean uspeh = ocenaRepo.dodajKomentarOcenu(ko);
            if (uspeh) {
                response.put("message", "Uspešno ste ostavili ocenu/komentar!");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Došlo je do greške.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
