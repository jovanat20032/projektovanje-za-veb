import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ProdavnicaService } from '../services/prodavnica.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-prodavnica',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './prodavnica.component.html',
  styleUrl: './prodavnica.component.css'
})
export class ProdavnicaComponent implements OnInit {

  sviSportovi: any[] = [];
  izabraniSport: string = 'SVI';
  opremaKatalog: any[] = [];
  
  korpa: any[] = [];
  ukupnaCena: number = 0;
  
  trenutniKorisnik: string = '';

  constructor(
    private prodavnicaService: ProdavnicaService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const korisnickoImeStr = localStorage.getItem('korisnickoIme');
    if (korisnickoImeStr) {
      this.trenutniKorisnik = korisnickoImeStr;
    } else {
      this.trenutniKorisnik = 'mina'; // Fallback
    }

    this.ucitajSportove();
    this.ucitajOpremu();
  }

  ucitajSportove() {
    this.http.get<any[]>('http://localhost:8080/api/korisnici/sviSportovi').subscribe(res => {
      this.sviSportovi = res;
    });
  }

  ucitajOpremu() {
    this.prodavnicaService.getOprema(this.izabraniSport).subscribe(res => {
      this.opremaKatalog = res;
    });
  }

  promeniSport() {
    this.ucitajOpremu();
  }

  dodajUKorpu(artikal: any) {
    // Provera zaliha na frontendu (dodatno se proverava na back-u)
    let kolicinaUKorpi = 0;
    const postojeci = this.korpa.find(s => s.opremaId === artikal.id);
    if (postojeci) {
      kolicinaUKorpi = postojeci.kolicina;
    }

    if (kolicinaUKorpi + 1 > artikal.zaliha) {
      alert('Nema dovoljno na zalihama!');
      return;
    }

    if (postojeci) {
      postojeci.kolicina++;
    } else {
      this.korpa.push({
        opremaId: artikal.id,
        naziv: artikal.naziv,
        kolicina: 1,
        cenaPoKomadu: artikal.cena
      });
    }
    this.izracunajUkupno();
  }

  izbaciIzKorpe(index: number) {
    this.korpa.splice(index, 1);
    this.izracunajUkupno();
  }

  izracunajUkupno() {
    this.ukupnaCena = this.korpa.reduce((sum, stavka) => sum + (stavka.kolicina * stavka.cenaPoKomadu), 0);
  }

  naruci() {
    if (this.korpa.length === 0) {
      alert('Korpa je prazna!');
      return;
    }

    const porudzbina = {
      korisnickoIme: this.trenutniKorisnik,
      ukupnaCena: this.ukupnaCena,
      stavke: this.korpa.map(s => ({
        opremaId: s.opremaId,
        kolicina: s.kolicina,
        cenaPoKomadu: s.cenaPoKomadu
      }))
    };

    this.prodavnicaService.naruci(porudzbina).subscribe({
      next: (res) => {
        alert(res.message);
        this.korpa = [];
        this.izracunajUkupno();
        this.ucitajOpremu(); // Refresh zaliha
      },
      error: (err) => {
        alert(err.error.message || 'Greška pri naručivanju.');
      }
    });
  }
}
