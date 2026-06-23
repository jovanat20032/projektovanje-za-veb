import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { OglasService } from '../services/oglas.service';

@Component({
  selector: 'app-oglasi',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './oglasi.component.html',
  styleUrl: './oglasi.component.css'
})
export class OglasiComponent implements OnInit {

  trenutniKorisnik: string = '';
  sviAktivniOglasi: any[] = [];
  mojiOglasi: any[] = [];

  // Forma za novi oglas
  noviOglas = {
    sport: '',
    grad: '',
    datum: '',
    vreme: '',
    nedostajeIgraca: 1
  };

  constructor(private oglasService: OglasService) {}

  ngOnInit(): void {
    const korisnickoImeStr = localStorage.getItem('korisnickoIme');
    if (korisnickoImeStr) {
      this.trenutniKorisnik = korisnickoImeStr;
    } else {
      this.trenutniKorisnik = 'mina'; // Fallback for testing
    }
    this.ucitajOglase();
  }

  ucitajOglase() {
    // Svi aktivni
    this.oglasService.getAllAktivniOglasi().subscribe(res => {
      this.sviAktivniOglasi = res.filter(o => o.korisnickoIme !== this.trenutniKorisnik);
    });

    // Moji oglasi
    this.oglasService.getOglasiByKorisnik(this.trenutniKorisnik).subscribe(res => {
      this.mojiOglasi = res;
      // Ucitaj zahteve za svaki moj oglas
      this.mojiOglasi.forEach(oglas => {
        this.oglasService.getZahtevi(oglas.id).subscribe(zahtevi => {
          oglas.zahtevi = zahtevi;
        });
      });
    });
  }

  objaviOglas() {
    if (!this.noviOglas.sport || !this.noviOglas.grad || !this.noviOglas.datum || !this.noviOglas.vreme || this.noviOglas.nedostajeIgraca < 1) {
      alert('Sva polja su obavezna.');
      return;
    }

    const oglas = {
      korisnickoIme: this.trenutniKorisnik,
      sport: this.noviOglas.sport,
      grad: this.noviOglas.grad,
      datumVreme: `${this.noviOglas.datum}T${this.noviOglas.vreme}:00`,
      nedostajeIgraca: this.noviOglas.nedostajeIgraca
    };

    this.oglasService.kreirajOglas(oglas).subscribe({
      next: (res) => {
        alert(res.message);
        this.ucitajOglase();
        this.noviOglas = { sport: '', grad: '', datum: '', vreme: '', nedostajeIgraca: 1 };
      },
      error: (err) => {
        alert('Greska pri kreiranju oglasa');
      }
    });
  }

  prijaviSe(oglasId: number) {
    this.oglasService.prijaviSeNaOglas(oglasId, this.trenutniKorisnik).subscribe({
      next: (res) => {
        alert(res.message);
      },
      error: (err) => {
        alert(err.error?.message || 'Greska pri prijavi');
      }
    });
  }

  zatvoriOglas(oglasId: number) {
    this.oglasService.zatvoriOglas(oglasId).subscribe({
      next: (res) => {
        alert(res.message);
        this.ucitajOglase();
      },
      error: (err) => {
        alert('Greska pri zatvaranju oglasa');
      }
    });
  }

  promeniStatusZahteva(zahtevId: number, status: string) {
    this.oglasService.azurirajStatusZahteva(zahtevId, status).subscribe({
      next: (res) => {
        alert(res.message);
        this.ucitajOglase();
      },
      error: (err) => {
        alert('Greska pri promeni statusa');
      }
    });
  }
}
