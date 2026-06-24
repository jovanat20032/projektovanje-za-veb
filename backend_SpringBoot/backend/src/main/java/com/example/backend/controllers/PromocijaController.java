package com.example.backend.controllers;

import com.example.backend.db.PromocijaRepo;
import com.example.backend.models.Promocija;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promocije")
@CrossOrigin(origins = "http://localhost:4200")
public class PromocijaController {

    private PromocijaRepo promocijaRepo = new PromocijaRepo();

    @GetMapping("/zaposleni/{korisnickoIme}")
    public ResponseEntity<?> getPromocijeZaZaposlenog(@PathVariable String korisnickoIme) {
        List<Promocija> promocije = promocijaRepo.getPromocijeZaZaposlenog(korisnickoIme);
        return ResponseEntity.ok(promocije);
    }

    @PostMapping("")
    public ResponseEntity<?> dodajPromociju(@RequestBody Promocija promocija) {
        boolean uspeh = promocijaRepo.dodajPromociju(promocija);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Promocija uspesno kreirana\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri kreiranju promocije\"}");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> azurirajPromociju(@PathVariable int id, @RequestBody Promocija promocija) {
        promocija.setId(id);
        boolean uspeh = promocijaRepo.azurirajPromociju(promocija);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Promocija uspesno azurirana\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri azuriranju promocije\"}");
        }
    }
}
