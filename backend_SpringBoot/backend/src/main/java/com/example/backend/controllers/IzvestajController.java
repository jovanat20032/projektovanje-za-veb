package com.example.backend.controllers;

import com.example.backend.db.IzvestajRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/izvestaji")
@CrossOrigin(origins = "http://localhost:4200")
public class IzvestajController {

    private IzvestajRepo izvestajRepo = new IzvestajRepo();

    @GetMapping("/popunjenost")
    public ResponseEntity<?> getPopunjenostTerena(@RequestParam int mesec, @RequestParam int godina, @RequestParam int objekatId) {
        List<Map<String, Object>> izvestaj = izvestajRepo.getPopunjenostTerena(mesec, godina, objekatId);
        return ResponseEntity.ok(izvestaj);
    }

    @GetMapping("/promet-opreme")
    public ResponseEntity<?> getPrometOpreme(@RequestParam int mesec, @RequestParam int godina) {
        List<Map<String, Object>> izvestaj = izvestajRepo.getPrometOpreme(mesec, godina);
        return ResponseEntity.ok(izvestaj);
    }
}
