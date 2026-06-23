package com.example.backend.controllers;

import com.example.backend.db.ProdavnicaRepo;
import com.example.backend.models.Oprema;
import com.example.backend.models.Porudzbina;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prodavnica")
@CrossOrigin(origins = "http://localhost:4200")
public class ProdavnicaController {

    private ProdavnicaRepo prodavnicaRepo = new ProdavnicaRepo();

    @GetMapping("/oprema")
    public ResponseEntity<?> getOprema(@RequestParam(required = false, defaultValue = "SVI") String sport) {
        List<Oprema> oprema = prodavnicaRepo.getOpremaBySport(sport);
        return ResponseEntity.ok(oprema);
    }

    @PostMapping("/naruci")
    public ResponseEntity<?> naruci(@RequestBody Porudzbina porudzbina) {
        boolean uspeh = prodavnicaRepo.naruci(porudzbina);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Porudžbina uspešno kreirana.\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greška pri kreiranju porudžbine. Nedovoljno zaliha ili sistemska greška.\"}");
        }
    }

    @GetMapping("/porudzbine/{korisnickoIme}")
    public ResponseEntity<?> getMojePorudzbine(@PathVariable String korisnickoIme) {
        List<Porudzbina> porudzbine = prodavnicaRepo.getMojePorudzbine(korisnickoIme);
        return ResponseEntity.ok(porudzbine);
    }

    @PutMapping("/porudzbine/{id}/otkazi")
    public ResponseEntity<?> otkaziPorudzbinu(@PathVariable int id) {
        boolean uspeh = prodavnicaRepo.otkaziPorudzbinu(id);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Porudžbina je uspešno otkazana.\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greška pri otkazivanju. Moguće da porudžbina više nije u statusu NARUČENO.\"}");
        }
    }
}
