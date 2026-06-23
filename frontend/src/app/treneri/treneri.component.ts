import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TrenerService } from '../services/trener.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-treneri',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './treneri.component.html',
  styleUrl: './treneri.component.css'
})
export class TreneriComponent implements OnInit {

  objekti: any[] = [];
  sviSportovi: any[] = [];
  
  izabraniObjekat: number = 0;
  izabraniSport: string = '';

  treneri: any[] = [];
  
  trenutniKorisnik: string = '';

  // Polja za zakazivanje
  otvorenaFormaZa: string | null = null;
  rezDatum: string = '';
  rezVreme: string = '';

  constructor(
    private trenerService: TrenerService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const korisnickoImeStr = localStorage.getItem('korisnickoIme');
    if (korisnickoImeStr) {
      this.trenutniKorisnik = korisnickoImeStr;
    } else {
      this.trenutniKorisnik = 'mina'; // Fallback
    }

    this.ucitajObjekteISportove();
  }

  ucitajObjekteISportove() {
    this.http.get<any[]>('http://localhost:8080/api/objekti/pretraga-sportista').subscribe(res => {
      this.objekti = res;
    });
    this.http.get<any[]>('http://localhost:8080/api/korisnici/sviSportovi').subscribe(res => {
      this.sviSportovi = res;
    });
  }

  pretraziTrenere() {
    if (this.izabraniObjekat && this.izabraniSport) {
      this.trenerService.getTreneriByObjekatAndSport(this.izabraniObjekat, this.izabraniSport).subscribe(res => {
        this.treneri = res;
        this.otvorenaFormaZa = null;
      });
    } else {
      alert('Molimo izaberite objekat i sport.');
    }
  }

  otvoriFormu(trenerKorisnickoIme: string) {
    this.otvorenaFormaZa = trenerKorisnickoIme;
    this.rezDatum = '';
    this.rezVreme = '';
  }

  zakazi(trenerKorisnickoIme: string) {
    if (!this.rezDatum || !this.rezVreme) {
      alert('Morate uneti datum i vreme.');
      return;
    }

    const rezervacija = {
      trenerKorisnickoIme: trenerKorisnickoIme,
      sportistaKorisnickoIme: this.trenutniKorisnik,
      datumVreme: `${this.rezDatum}T${this.rezVreme}:00`
    };

    this.trenerService.zakaziTrening(rezervacija).subscribe({
      next: (res) => {
        alert(res.message);
        this.otvorenaFormaZa = null;
      },
      error: (err) => {
        alert('Greska pri zakazivanju treninga');
      }
    });
  }
}
