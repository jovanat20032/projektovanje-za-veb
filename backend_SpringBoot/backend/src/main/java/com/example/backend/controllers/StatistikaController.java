package com.example.backend.controllers;

import com.example.backend.db.StatistikaRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistika")
@CrossOrigin(origins = "http://localhost:4200")
public class StatistikaController {

    private StatistikaRepo statistikaRepo = new StatistikaRepo();

    @GetMapping("/rezervacije-po-sportu")
    public ResponseEntity<Map<String, Integer>> getRezervacijePoSportu() {
        return ResponseEntity.ok(statistikaRepo.getRezervacijePoSportu());
    }

    @GetMapping("/mesecna-aktivnost")
    public ResponseEntity<Map<String, Integer>> getMesecnaAktivnost() {
        return ResponseEntity.ok(statistikaRepo.getMesecnaAktivnost());
    }

    @GetMapping("/potrosnja")
    public ResponseEntity<Map<String, Double>> getPotrosnjaPoSportisti() {
        return ResponseEntity.ok(statistikaRepo.getPotrosnjaPoSportisti());
    }
}
