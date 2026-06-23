package com.example.backend.controllers;

import com.example.backend.db.ObjekatRepo;
import com.example.backend.db.TerenRepo;
import com.example.backend.models.Objekat;
import com.example.backend.models.Promocija;
import com.example.backend.models.Teren;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/objekti")
@CrossOrigin(origins = "http://localhost:4200")
public class ObjekatController {

    private ObjekatRepo objekatRepo = new ObjekatRepo();
    private TerenRepo terenRepo = new TerenRepo();

    @GetMapping("/statistika")
    public ResponseEntity<?> getStatistika() {
        int ukupanBroj = objekatRepo.getUkupanBrojAktivnih();
        List<Objekat> top3 = objekatRepo.getTop3Aktivna();
        List<Promocija> promocije = objekatRepo.getTop3Promocije();

        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("ukupanBroj", ukupanBroj);
        odgovor.put("top3", top3);
        odgovor.put("promocije", promocije);

        return ResponseEntity.ok(odgovor);
    }

    @GetMapping("/pretraga")
    public ResponseEntity<?> pretraga(@RequestParam(required = false) String naziv, 
                                      @RequestParam(required = false) String grad,
                                      @RequestParam(required = false) String sport) {
        List<Objekat> rezultati = objekatRepo.pretraga(naziv, grad, sport);
        return ResponseEntity.ok(rezultati);
    }

    @GetMapping("/gradovi")
    public ResponseEntity<?> getGradovi() {
        List<String> gradovi = objekatRepo.getSviGradovi();
        return ResponseEntity.ok(gradovi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getObjekatDetalji(@PathVariable int id) {
        Objekat objekat = objekatRepo.getById(id);
        if (objekat == null) {
            return ResponseEntity.notFound().build();
        }
        List<Teren> tereni = terenRepo.getTereniZaObjekat(id);
        List<String> galerija = objekatRepo.getSlikeZaObjekat(id);
        List<String> sportovi = objekatRepo.getSportoviZaObjekat(id);

        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("objekat", objekat);
        odgovor.put("tereni", tereni);
        odgovor.put("galerija", galerija);
        odgovor.put("sportovi", sportovi);
        
        return ResponseEntity.ok(odgovor);
    }

    @GetMapping("/pretraga-sportista")
    public ResponseEntity<?> pretragaSportista(@RequestParam(required = false) String naziv, 
                                      @RequestParam(required = false) String grad,
                                      @RequestParam(required = false) String sport,
                                      @RequestParam(required = false) String tipTerena,
                                      @RequestParam(required = false, defaultValue = "false") boolean slobodniDanas) {
        List<Objekat> rezultati = objekatRepo.pretragaSportista(naziv, grad, sport, tipTerena, slobodniDanas);
        return ResponseEntity.ok(rezultati);
    }

}
