package com.example.backend.controllers;

import com.example.backend.db.AdminRepo;
import com.example.backend.models.Korisnik;
import com.example.backend.models.Objekat;
import com.example.backend.models.Trener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private AdminRepo adminRepo = new AdminRepo();

    @GetMapping("/korisnici")
    public ResponseEntity<List<Korisnik>> getSviKorisnici() {
        return ResponseEntity.ok(adminRepo.getSviKorisnici());
    }

    @PutMapping("/korisnici")
    public ResponseEntity<String> updateKorisnikAdmin(@RequestBody Korisnik k) {
        boolean uspesno = adminRepo.updateKorisnikAdmin(k);
        if (uspesno) return ResponseEntity.ok("Korisnik uspesno azuriran.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska pri azuriranju korisnika.");
    }

    @DeleteMapping("/korisnici/{korisnickoIme}")
    public ResponseEntity<String> obrisiKorisnika(@PathVariable String korisnickoIme) {
        boolean uspesno = adminRepo.obrisiKorisnika(korisnickoIme);
        if (uspesno) return ResponseEntity.ok("Korisnik uspesno obrisan.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska pri brisanju korisnika.");
    }

    @GetMapping("/zahtevi/korisnici")
    public ResponseEntity<List<Korisnik>> getZahteviKorisnika() {
        return ResponseEntity.ok(adminRepo.getZahteviZaRegistraciju());
    }

    @PostMapping("/zahtevi/korisnici/{korisnickoIme}/{odluka}")
    public ResponseEntity<String> odluciOKorisniku(@PathVariable String korisnickoIme, @PathVariable String odluka) {
        boolean uspesno = adminRepo.odluciOZahtevuKorisnika(korisnickoIme, odluka);
        if (uspesno) return ResponseEntity.ok("Uspesno obradjen zahtev.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska.");
    }

    @GetMapping("/zahtevi/objekti")
    public ResponseEntity<List<Objekat>> getZahteviObjekata() {
        return ResponseEntity.ok(adminRepo.getZahteviZaObjekte());
    }

    @PostMapping("/zahtevi/objekti/{id}/{odluka}")
    public ResponseEntity<String> odluciOObjektu(@PathVariable int id, @PathVariable String odluka) {
        boolean uspesno = adminRepo.odluciOZahtevuObjekta(id, odluka);
        if (uspesno) return ResponseEntity.ok("Uspesno obradjen zahtev objekta.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska.");
    }

    @GetMapping("/treneri")
    public ResponseEntity<List<Trener>> getSviTreneri() {
        return ResponseEntity.ok(adminRepo.getSviTreneri());
    }

    @DeleteMapping("/treneri/{korisnickoIme}")
    public ResponseEntity<String> deaktivirajTrenera(@PathVariable String korisnickoIme) {
        boolean uspesno = adminRepo.deaktivirajTrenera(korisnickoIme);
        if (uspesno) return ResponseEntity.ok("Trener deaktiviran.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska.");
    }

    @PostMapping("/sportovi")
    public ResponseEntity<String> dodajSport(@RequestBody Map<String, Object> podaci) {
        String naziv = (String) podaci.get("naziv");
        boolean timski = (boolean) podaci.get("timski");
        boolean uspesno = adminRepo.dodajSport(naziv, timski);
        if (uspesno) return ResponseEntity.ok("Sport dodat.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Greska pri dodavanju sporta.");
    }
}
