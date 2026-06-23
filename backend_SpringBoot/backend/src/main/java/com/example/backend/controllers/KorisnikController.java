package com.example.backend.controllers;

import com.example.backend.models.Korisnik;
import com.example.backend.models.RezervacijaDTO;
import com.example.backend.models.Sport;
import com.example.backend.config.JwtUtil;
import com.example.backend.db.KorisnikRepo;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/korisnici")
@CrossOrigin(origins = "http://localhost:4200")
public class KorisnikController {

    private KorisnikRepo korisnikRepo = new KorisnikRepo();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Map<String, String> aktivniTokeni = new HashMap<>();
    private Map<String, Long> vremeIstekaTokena = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> prijava(@RequestBody Map<String, String> podaci) {
        String korisnickoIme = podaci.get("korisnickoIme");
        String lozinka = podaci.get("lozinka");

        if (korisnickoIme == null || korisnickoIme.trim().isEmpty() || 
            lozinka == null || lozinka.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Korisnicko ime i lozinka su obavezni.");
        }

        Korisnik korisnik = korisnikRepo.findByKorisnickoIme(korisnickoIme);
        if (korisnik == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pogresno korisnicko ime ili lozinka.");
        }

        if (!passwordEncoder.matches(lozinka, korisnik.getLozinka())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Pogresno korisnicko ime ili lozinka.");
        }

        if ("NA_CEKANJU".equals(korisnik.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vas nalog jos uvek nije odobren od strane administratora.");
        } else if ("ODBIJEN".equals(korisnik.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Vas zahtev za registraciju je odbijen.");
        }

        String token = JwtUtil.generateToken(korisnik.getKorisnickoIme(), korisnik.getUloga());

        Map<String, Object> odgovor = new HashMap<>();
        odgovor.put("token", token);
        odgovor.put("korisnickoIme", korisnik.getKorisnickoIme());
        odgovor.put("uloga", korisnik.getUloga());

        return ResponseEntity.ok(odgovor);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registracija(@RequestBody Korisnik noviKorisnik) {
        if (korisnikRepo.findByKorisnickoIme(noviKorisnik.getKorisnickoIme()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Korisničko ime je zauzeto.");
        }

        String enkriptovanaLozinka = passwordEncoder.encode(noviKorisnik.getLozinka());
        noviKorisnik.setLozinka(enkriptovanaLozinka);

        noviKorisnik.setStatus("NA_CEKANJU");

        boolean uspesno = korisnikRepo.save(noviKorisnik);

        if (uspesno) {
            String token = JwtUtil.generateToken(noviKorisnik.getKorisnickoIme(), noviKorisnik.getUloga());
            
            Map<String, Object> odgovor = new HashMap<>();
            odgovor.put("token", token);
            odgovor.put("korisnickoIme", noviKorisnik.getKorisnickoIme());
            odgovor.put("uloga", noviKorisnik.getUloga());
            
            return ResponseEntity.ok(odgovor);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri čuvanju u bazu.");
        }
    }

    @PostMapping("/zahtev-za-reset")
    public ResponseEntity<?> zahtevZaReset(@RequestBody Map<String, String> podaci) {
        String unos = podaci.get("unos");

        Korisnik korisnik = korisnikRepo.findByKorisnickoImeIliEmail(unos);
        if (korisnik == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Korisnik sa unetim podacima nije pronađen.");
        }

        String token = java.util.UUID.randomUUID().toString();

        aktivniTokeni.put(token, korisnik.getKorisnickoIme());
        vremeIstekaTokena.put(token, System.currentTimeMillis() + (30 * 60 * 1000));

        String generisaniLink = "http://localhost:4200/nova-lozinka/" + token;
        
        Map<String, String> odgovor = new HashMap<>();
        odgovor.put("poruka", "Privremeni link je generisan!");
        odgovor.put("link", generisaniLink);

        return ResponseEntity.ok(odgovor);
    }

    @PostMapping("/promena-lozinke")
    public ResponseEntity<?> promenaLozinke(@RequestBody Map<String, String> podaci) {
        String token = podaci.get("token");
        String novaLozinka = podaci.get("novaLozinka");

        if (!aktivniTokeni.containsKey(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ovaj link je nevalidan ili je već iskorišćen.");
        }

        if (System.currentTimeMillis() > vremeIstekaTokena.get(token)) {
            aktivniTokeni.remove(token);
            vremeIstekaTokena.remove(token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Link je istekao. Morate zatražiti novi.");
        }

        String korisnickoIme = aktivniTokeni.get(token);
        String enkriptovanaLozinka = passwordEncoder.encode(novaLozinka);
        
        boolean uspesno = korisnikRepo.azurirajLozinku(korisnickoIme, enkriptovanaLozinka);

        if (uspesno) {
            aktivniTokeni.remove(token);
            vremeIstekaTokena.remove(token);
            Map<String, String> odgovor = new HashMap<>();
            odgovor.put("poruka", "Lozinka je uspešno promenjena!");
            return ResponseEntity.ok(odgovor);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri čuvanju u bazu.");
        }
    }

    @GetMapping("/dohvatiKorisnika")
public ResponseEntity<Korisnik> dohvatiKorisnika(@RequestParam String korisnickoIme) {
    Korisnik k = korisnikRepo.findByKorisnickoIme(korisnickoIme);
    if (k != null) {
        return new ResponseEntity<>(k, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

    @PostMapping("/azurirajProfil")
    public ResponseEntity<String> azurirajProfil(@RequestBody Korisnik korisnik) {
        boolean uspesno = korisnikRepo.azurirajKorisnika(korisnik);
        if (uspesno) {
            return new ResponseEntity<>("Profil uspešno ažuriran", HttpStatus.OK);
        }
        return new ResponseEntity<>("Greška pri ažuriranju", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/sviSportovi")
    public ResponseEntity<List<Sport>> dohvatiSveSportove() {
        return new ResponseEntity<>(korisnikRepo.dohvatiSveSportove(), HttpStatus.OK);
    }

    @GetMapping("/omiljeniSportovi")
    public ResponseEntity<List<String>> dohvatiOmiljeneSportove(@RequestParam String korisnickoIme) {
        return new ResponseEntity<>(korisnikRepo.dohvatiOmiljeneSportove(korisnickoIme), HttpStatus.OK);
    }

    @PostMapping("/azurirajSportove")
    public ResponseEntity<String> azurirajSportove(@RequestParam String korisnickoIme, @RequestBody List<String> sportovi) {
        if (sportovi.size() > 5) {
            return new ResponseEntity<>("Možete odabrati najviše 5 sportova.", HttpStatus.BAD_REQUEST);
        }
        
        boolean uspesno = korisnikRepo.azurirajOmiljeneSportove(korisnickoIme, sportovi);
        if (uspesno) {
            return new ResponseEntity<>("Sportovi uspešno ažurirani", HttpStatus.OK);
        }
        return new ResponseEntity<>("Greška pri ažuriranju sportova", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/mojeRezervacije")
public ResponseEntity<List<RezervacijaDTO>> mojeRezervacije(@RequestParam String korisnickoIme) {
    List<RezervacijaDTO> rezervacije = korisnikRepo.dohvatiRezervacijeKorisnika(korisnickoIme);
    return new ResponseEntity<>(rezervacije, HttpStatus.OK);
}

@PostMapping("/otkaziRezervaciju")
public ResponseEntity<String> otkaziRezervaciju(@RequestParam int id) {
    boolean uspesno = korisnikRepo.otkaziRezervaciju(id); 
    if (uspesno) {
        return new ResponseEntity<>("Rezervacija je otkazana", HttpStatus.OK);
    }
    return new ResponseEntity<>("Greška pri otkazivanju", HttpStatus.BAD_REQUEST);
}

}