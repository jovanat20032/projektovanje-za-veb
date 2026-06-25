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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import com.example.backend.models.LoginDTO;
import com.example.backend.models.ZahtevResetDTO;
import com.example.backend.models.PromenaLozinkeDTO;

@RestController
@RequestMapping("/api/korisnici")
@CrossOrigin(origins = "http://localhost:4200")
public class KorisnikController {

    private KorisnikRepo korisnikRepo = new KorisnikRepo();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Map<String, String> aktivniTokeni = new HashMap<>();
    private Map<String, Long> vremeIstekaTokena = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> prijava(@Valid @RequestBody LoginDTO loginDTO) {
        String korisnickoIme = loginDTO.getKorisnickoIme();
        String lozinka = loginDTO.getLozinka();

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

    @PostMapping(value = "/register", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registracija(
            @RequestPart("korisnik") String korisnikJson,
            @RequestPart(value = "sportovi", required = false) String sportoviJson,
            @RequestPart(value = "slika", required = false) org.springframework.web.multipart.MultipartFile slika) {
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Korisnik noviKorisnik = mapper.readValue(korisnikJson, Korisnik.class);

            if (korisnikRepo.findByKorisnickoIme(noviKorisnik.getKorisnickoIme()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Korisničko ime je zauzeto.");
            }

            String enkriptovanaLozinka = passwordEncoder.encode(noviKorisnik.getLozinka());
            noviKorisnik.setLozinka(enkriptovanaLozinka);
            noviKorisnik.setStatus("NA_CEKANJU");

            if (slika != null && !slika.isEmpty()) {
                String fileName = java.util.UUID.randomUUID().toString() + "_" + slika.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                java.nio.file.Files.copy(slika.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                noviKorisnik.setProfilnaSlika(fileName);
            } else {
                noviKorisnik.setProfilnaSlika("default_avatar.png");
            }

            boolean uspesno = korisnikRepo.save(noviKorisnik);

            if (uspesno) {
                if (sportoviJson != null && !sportoviJson.isEmpty()) {
                    List<String> sportovi = mapper.readValue(sportoviJson, mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                    if (sportovi != null && !sportovi.isEmpty()) {
                        if (sportovi.size() > 5) {
                            sportovi = sportovi.subList(0, 5);
                        }
                        korisnikRepo.azurirajOmiljeneSportove(noviKorisnik.getKorisnickoIme(), sportovi);
                    }
                }

                String token = JwtUtil.generateToken(noviKorisnik.getKorisnickoIme(), noviKorisnik.getUloga());
                
                Map<String, Object> odgovor = new HashMap<>();
                odgovor.put("token", token);
                odgovor.put("korisnickoIme", noviKorisnik.getKorisnickoIme());
                odgovor.put("uloga", noviKorisnik.getUloga());
                
                return ResponseEntity.ok(odgovor);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška pri čuvanju u bazu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Došlo je do greške na serveru.");
        }
    }

    @PostMapping("/zahtev-za-reset")
    public ResponseEntity<?> zahtevZaReset(@Valid @RequestBody ZahtevResetDTO zahtevDTO) {
        String unos = zahtevDTO.getUnos();

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
    public ResponseEntity<?> promenaLozinke(@Valid @RequestBody PromenaLozinkeDTO promenaDTO) {
        String token = promenaDTO.getToken();
        String novaLozinka = promenaDTO.getNovaLozinka();

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

    @PostMapping(value = "/azurirajProfil", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> azurirajProfil(
            @RequestPart("korisnik") String korisnikJson,
            @RequestPart(value = "slika", required = false) org.springframework.web.multipart.MultipartFile slika) {
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Korisnik korisnik = mapper.readValue(korisnikJson, Korisnik.class);

            if (slika != null && !slika.isEmpty()) {
                String fileName = java.util.UUID.randomUUID().toString() + "_" + slika.getOriginalFilename();
                java.nio.file.Path uploadPath = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                java.nio.file.Path filePath = uploadPath.resolve(fileName);
                java.nio.file.Files.copy(slika.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                korisnik.setProfilnaSlika(fileName);
            }

            boolean uspesno = korisnikRepo.azurirajKorisnika(korisnik);
            if (uspesno) {
                return new ResponseEntity<>("Profil uspešno ažuriran", HttpStatus.OK);
            }
            return new ResponseEntity<>("Greška pri ažuriranju", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Greška na serveru", HttpStatus.INTERNAL_SERVER_ERROR);
        }
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