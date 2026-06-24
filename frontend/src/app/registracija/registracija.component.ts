import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';
import { Korisnik } from '../models/korisnik';
import { Sport } from '../models/sport';

@Component({
  selector: 'app-registracija',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './registracija.component.html',
  styleUrl: './registracija.component.css'
})
export class RegistracijaComponent implements OnInit {
  private korisnikService = inject(KorisnikService);
  private router = inject(Router);

  noviKorisnik: Korisnik = new Korisnik();
  poruka: string = "";
  
  sviSportovi: Sport[] = [];
  odabraniSportovi: string[] = [];
  
  odabranaSlika: File | null = null;
  generisaniAvatarUrl: string | null = null;

  ngOnInit(): void {
    this.korisnikService.dohvatiSveSportove().subscribe(sportovi => {
      this.sviSportovi = sportovi;
    });
  }

  naOdabirSlike(event: any) {
    const fajl = event.target.files[0];
    if (fajl) {
      this.odabranaSlika = fajl;
      this.generisaniAvatarUrl = null; // Ponisti avatar ako odabere fajl
    }
  }

  generisiAvatar() {
    const seed = Math.random().toString(36).substring(7);
    this.generisaniAvatarUrl = `https://api.dicebear.com/9.x/pixel-art/svg?seed=${seed}`;
  }

  sacuvajAvatar() {
    if (!this.generisaniAvatarUrl) return;
    
    fetch(this.generisaniAvatarUrl)
      .then(res => res.blob())
      .then(blob => {
        const file = new File([blob], "avatar.svg", { type: "image/svg+xml" });
        this.odabranaSlika = file;
        alert("Avatar uspesno preuzet i postavljen za profilnu sliku!");
      })
      .catch(err => console.error("Greska pri preuzimanju avatara", err));
  }

  registrujSe() {
    const lozinkaRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[a-zA-Z].{7,11}$/;

    if (!lozinkaRegex.test(this.noviKorisnik.lozinka)) {
      this.poruka = "Lozinka mora imati 8-12 karaktera, pocinjati slovom, i sadrzati bar jedno veliko slovo, broj i specijalni karakter.";
      return;
    }

    if (this.odabraniSportovi.length > 5) {
      this.poruka = "Mozete odabrati maksimalno 5 sportova.";
      return;
    }

    const formData = new FormData();
    formData.append("korisnik", JSON.stringify(this.noviKorisnik));
    formData.append("sportovi", JSON.stringify(this.odabraniSportovi));
    
    if (this.odabranaSlika) {
      formData.append("slika", this.odabranaSlika);
    }

    this.korisnikService.register(formData).subscribe({
      next: (odgovor) => {
        alert("Zahtev za registraciju uspesno poslat! Ceka se odobrenje administratora.");
        this.router.navigate(['/prijava']);
      },
      error: (greska) => {
        if (greska.status === 400) {
          this.poruka = "Korisnicko ime je vec zauzeto.";
        } else {
          this.poruka = "Doslo je do greske na serveru.";
        }
      }
    });
  }
}
