package com.example.backend.controllers;

import com.example.backend.db.TrenerRepo;
import com.example.backend.models.Trener;
import com.example.backend.models.TreningRezervacija;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treneri")
@CrossOrigin(origins = "http://localhost:4200")
public class TrenerController {

    private TrenerRepo trenerRepo = new TrenerRepo();

    @GetMapping("/objekat/{objekatId}/sport/{sport}")
    public ResponseEntity<?> getTreneriByObjekatAndSport(@PathVariable int objekatId, @PathVariable String sport) {
        List<Trener> treneri = trenerRepo.getTreneriByObjekatAndSport(objekatId, sport);
        return ResponseEntity.ok(treneri);
    }

    @PostMapping("/zakazi")
    public ResponseEntity<?> zakaziTrening(@RequestBody TreningRezervacija rezervacija) {
        boolean uspeh = trenerRepo.zakaziTrening(rezervacija);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Trening uspesno zakazan\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri zakazivanju treninga\"}");
        }
    }

    @GetMapping("/sportista/{korisnickoIme}")
    public ResponseEntity<?> getTreninziZaSportistu(@PathVariable String korisnickoIme) {
        List<TreningRezervacija> treninzi = trenerRepo.getTreninziZaSportistu(korisnickoIme);
        return ResponseEntity.ok(treninzi);
    }

    @GetMapping("/zaposleni/{korisnickoIme}/treninzi")
    public ResponseEntity<?> getTreninziZaZaposlenog(@PathVariable String korisnickoIme) {
        List<TreningRezervacija> treninzi = trenerRepo.getTreninziZaZaposlenog(korisnickoIme);
        return ResponseEntity.ok(treninzi);
    }

    @PutMapping("/treninzi/{id}/status")
    public ResponseEntity<?> azurirajStatusTreninga(@PathVariable int id, @RequestBody java.util.Map<String, String> body) {
        String status = body.get("status");
        boolean uspeh = trenerRepo.azurirajStatusTreninga(id, status);
        if (uspeh) {
            return ResponseEntity.ok().body("{\"message\": \"Status uspesno azuriran\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"message\": \"Greska pri azuriranju statusa\"}");
        }
    }

    @GetMapping("/teren/{terenId}")
    public ResponseEntity<?> getTreninziZaTeren(@PathVariable int terenId) {
        List<TreningRezervacija> treninzi = trenerRepo.getTreninziZaTeren(terenId);
        return ResponseEntity.ok(treninzi);
    }

    @PutMapping("/{id}/pomeri")
    public ResponseEntity<?> pomeriTrening(@PathVariable int id, @RequestBody java.util.Map<String, String> body) {
        try {
            java.time.LocalDateTime novoVreme = java.time.LocalDateTime.parse(body.get("novoVreme"));
            boolean uspeh = trenerRepo.pomeriTrening(id, novoVreme);
            if (uspeh) {
                return ResponseEntity.ok().body("{\"message\": \"Trening uspesno pomeren\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Greska pri pomeranju treninga\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Nevalidan format datuma\"}");
        }
    }
}
