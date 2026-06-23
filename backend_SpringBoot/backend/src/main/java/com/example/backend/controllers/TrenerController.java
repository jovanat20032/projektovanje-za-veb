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
}
