import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';
import { Korisnik } from '../models/korisnik';
import { Sport } from '../models/sport';
import { Rezervacija } from '../models/rezervacija';
import { TrenerService } from '../services/trener.service';
import { ProdavnicaService } from '../services/prodavnica.service';

import { ObjekatService } from '../services/objekat.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit {
  private korisnikService = inject(KorisnikService);
  private trenerService = inject(TrenerService);
  private prodavnicaService = inject(ProdavnicaService);
  private objekatService = inject(ObjekatService);
  private router = inject(Router);

  odjaviSe(): void {
    localStorage.clear();
    this.router.navigate(['/']);
  }

  korisnik: Korisnik = new Korisnik();
  poruka: string = '';

  sviSportovi: Sport[] = [];
  odabraniSportovi: string[] = [];
  porukaSportovi: string = '';

  rezervacije: Rezervacija[] = [];
  mojiTreninzi: any[] = [];
  mojePorudzbine: any[] = [];
  
  mojiObjekti: any[] = [];
  uloga: string = '';

  ngOnInit(): void {
    const ulogovan = localStorage.getItem('korisnickoIme'); 
    this.uloga = localStorage.getItem('uloga') || '';
    
    if (ulogovan) {
      this.korisnikService.dohvatiKorisnika(ulogovan).subscribe({
        next: (k: Korisnik) => {
          this.korisnik = k;
        },
        error: (err) => {
          console.error("Greška pri dohvatanju podataka", err);
        }
      });
      
      if (this.uloga === 'ZAPOSLENI') {
        this.objekatService.getObjektiZaZaposlenog(ulogovan).subscribe({
          next: (objekti) => this.mojiObjekti = objekti,
          error: (err) => console.error("Greška pri dohvatanju objekata", err)
        });
      }

      this.korisnikService.dohvatiSveSportove().subscribe({
        next: (sportovi) => {
          console.log("3. SVI SPORTOVI SA BEKENDA:", sportovi);
          this.sviSportovi = sportovi;
        },
        error: (err) => console.error("Greška pri dohvatanju svih sportova:", err)
      });

      this.korisnikService.dohvatiOmiljeneSportove(ulogovan).subscribe({
        next: (omiljeni) => {
          console.log("4. OMILJENI SPORTOVI KORISNIKA:", omiljeni);
          this.odabraniSportovi = omiljeni;
        },
        error: (err) => console.error("Greška pri dohvatanju omiljenih sportova:", err)
      });
      this.korisnikService.dohvatiMojeRezervacije(ulogovan).subscribe({
        next: (rez) => {
          this.rezervacije = rez;
        },
        error: (err) => console.error("Greška pri dohvatanju rezervacija:", err)
      });

      this.trenerService.getTreninziZaSportistu(ulogovan).subscribe({
        next: (treninzi) => {
          this.mojiTreninzi = treninzi;
        },
        error: (err) => console.error("Greška pri dohvatanju treninga:", err)
      });

      this.ucitajPorudzbine(ulogovan);
    }
  }

  ucitajPorudzbine(korisnickoIme: string) {
    this.prodavnicaService.getMojePorudzbine(korisnickoIme).subscribe({
      next: (porudzbine) => {
        this.mojePorudzbine = porudzbine;
      },
      error: (err) => console.error("Greška pri dohvatanju porudžbina:", err)
    });
  }

  otkaziPorudzbinu(id: number) {
    if (confirm('Da li ste sigurni da želite da otkažete ovu porudžbinu?')) {
      this.prodavnicaService.otkaziPorudzbinu(id).subscribe({
        next: (res) => {
          alert(res.message);
          this.ucitajPorudzbine(this.korisnik.korisnickoIme);
        },
        error: (err) => alert(err.error.message || 'Greška pri otkazivanju.')
      });
    }
  }

  azuriraj(): void {
    const formData = new FormData();
    formData.append("korisnik", JSON.stringify(this.korisnik));
    if (this.novaSlikaFajl) {
      formData.append("slika", this.novaSlikaFajl);
    }
    this.korisnikService.azurirajProfil(formData).subscribe({
      next: (odgovor) => {
        this.poruka = 'Podaci su uspešno ažurirani!';
      },
      error: (err) => {
        this.poruka = 'Došlo je do greške pri ažuriranju.';
        console.error(err);
      }
    });
  }
  azurirajSportove(): void {
    if (this.odabraniSportovi.length > 5) {
      this.porukaSportovi = 'Možete odabrati najviše 5 sportova!';
      return;
    }

    if (this.korisnik.korisnickoIme) {
      this.korisnikService.azurirajSportove(this.korisnik.korisnickoIme, this.odabraniSportovi).subscribe({
        next: (odgovor) => this.porukaSportovi = 'Omiljeni sportovi su uspešno ažurirani!',
        error: (err) => this.porukaSportovi = 'Došlo je do greške pri ažuriranju sportova.'
      });
    }
  }
  mozeDaOtkaze(rez: Rezervacija): boolean {
    if (!rez || !rez.vremeOd) return false;

    if (rez.status === 'AKTIVNA') return true; 
    
    if (rez.status === 'POTVRDJENA') {
      const vremePocetka = new Date(rez.vremeOd).getTime();
      const sada = new Date().getTime();
      const razlikaSati = (vremePocetka - sada) / (1000 * 60 * 60);
      
      return razlikaSati >= 12;
    }
    
    return false;
  }

  otkazi(id: number): void {
    if (confirm('Da li ste sigurni da želite da otkažete ovu rezervaciju?')) {
      this.korisnikService.otkaziRezervaciju(id).subscribe({
        next: (odgovor) => {
          alert('Rezervacija uspešno otkazana.');
          const ulogovan = localStorage.getItem('korisnickoIme');
          if (ulogovan) {
            this.korisnikService.dohvatiMojeRezervacije(ulogovan).subscribe(rez => {
              this.rezervacije = rez;
            });
          }
        },
        error: (err) => {
          alert('Došlo je do greške pri otkazivanju.');
          console.error(err);
        }
      });
    }
  }
  trenutnoSortiranje: { kolona: string, rastuce: boolean } = { kolona: '', rastuce: true };

  sortiraj(kolona: keyof Rezervacija): void {
    if (this.trenutnoSortiranje.kolona === kolona) {
      this.trenutnoSortiranje.rastuce = !this.trenutnoSortiranje.rastuce;
    } else {
      this.trenutnoSortiranje = { kolona, rastuce: true };
    }

    this.rezervacije.sort((a, b) => {
      let vrednostA = a[kolona] as any;
      let vrednostB = b[kolona] as any;

      if (vrednostA < vrednostB) return this.trenutnoSortiranje.rastuce ? -1 : 1;
      if (vrednostA > vrednostB) return this.trenutnoSortiranje.rastuce ? 1 : -1;
      return 0;
    });
  }

  naGreskuSlike(event: any) {
    if (!event.target.src.endsWith('default_avatar.png')) {
      event.target.src = 'http://localhost:8080/uploads/default_avatar.png';
    }
  }

  novaSlikaFajl: File | null = null;

  naOdabirSlike(event: any) {
    const fajl = event.target.files[0];
    if (fajl) {
      this.novaSlikaFajl = fajl;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const img = document.querySelector('img[alt="Profilna slika"]') as HTMLImageElement;
        if (img) img.src = e.target.result;
      };
      reader.readAsDataURL(fajl);
    }
  }
}