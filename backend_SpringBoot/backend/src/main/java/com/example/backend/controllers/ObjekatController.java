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

    @GetMapping("/zaposleni")
    public ResponseEntity<?> getObjektiZaposlenog(@RequestParam String korisnickoIme) {
        List<com.example.backend.models.ZaposleniObjekatDTO> objekti = objekatRepo.dohvatiObjekteZaZaposlenog(korisnickoIme);
        return ResponseEntity.ok(objekti);
    }

    @PostMapping("")
    public ResponseEntity<?> dodajObjekat(@RequestBody com.example.backend.models.ObjekatCreateDTO dto, @RequestParam String korisnickoIme) {
        int noviId = objekatRepo.dodajObjekat(dto, korisnickoIme);
        if (noviId != -1) {
            if (dto.getTereni() != null) {
                for (Teren t : dto.getTereni()) {
                    terenRepo.dodajTeren(t, noviId);
                }
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "Uspešno dodat objekat");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Greška pri dodavanju objekta");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> azurirajObjekat(@PathVariable int id, @RequestBody com.example.backend.models.ObjekatCreateDTO dto) {
        boolean success = objekatRepo.azurirajObjekat(id, dto);
        if (success) {
            terenRepo.obrisiTereneZaObjekat(id);
            if (dto.getTereni() != null) {
                for (Teren t : dto.getTereni()) {
                    terenRepo.dodajTeren(t, id);
                }
            }
            Map<String, String> response = new HashMap<>();
            response.put("message", "Uspešno ažuriran objekat");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Greška pri ažuriranju objekta");
    }
}
