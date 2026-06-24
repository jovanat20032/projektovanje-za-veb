import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { PromocijaService } from '../services/promocija.service';
import { ObjekatService } from '../services/objekat.service';

@Component({
  selector: 'app-zaposleni-promocije',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './zaposleni-promocije.component.html',
  styleUrls: ['./zaposleni-promocije.component.css']
})
export class ZaposleniPromocijeComponent implements OnInit {
  private promocijaService = inject(PromocijaService);
  private objekatService = inject(ObjekatService);
  private router = inject(Router);

  uloga: string = '';
  korisnickoIme: string = '';

  promocije: any[] = [];
  objekti: any[] = [];

  prikaziFormu: boolean = false;
  
  // Forma
  idPromocije: number | null = null;
  naziv: string = '';
  objekatId: number = 0;
  vaziOd: string = '';
  vaziDo: string = '';
  popust: string = '';
  sport: string = '';

  ngOnInit(): void {
    this.uloga = localStorage.getItem('uloga') || '';
    this.korisnickoIme = localStorage.getItem('korisnickoIme') || '';

    if (this.uloga !== 'ZAPOSLENI') {
      this.router.navigate(['/']);
      return;
    }

    this.ucitajPodatke();
  }

  ucitajPodatke() {
    this.promocijaService.getPromocijeZaZaposlenog(this.korisnickoIme).subscribe(res => {
      this.promocije = res;
    });
    this.objekatService.getObjektiZaZaposlenog(this.korisnickoIme).subscribe(res => {
      this.objekti = res;
    });
  }

  novaPromocija() {
    this.idPromocije = null;
    this.naziv = '';
    this.objekatId = this.objekti.length > 0 ? this.objekti[0].id : 0;
    this.vaziOd = '';
    this.vaziDo = '';
    this.popust = '';
    this.sport = '';
    this.prikaziFormu = true;
  }

  izmeniPromociju(p: any) {
    this.idPromocije = p.id;
    this.naziv = p.naziv;
    this.objekatId = p.objekatId;
    this.vaziOd = p.vaziOd;
    this.vaziDo = p.vaziDo;
    this.popust = p.popust;
    this.sport = p.sport;
    this.prikaziFormu = true;
  }

  odustani() {
    this.prikaziFormu = false;
  }

  sacuvajPromociju() {
    if (!this.naziv || !this.vaziOd || !this.vaziDo || !this.popust || !this.sport) {
      alert("Molimo popunite sva obavezna polja.");
      return;
    }

    const promocija = {
      naziv: this.naziv,
      objekatId: this.objekatId,
      vaziOd: this.vaziOd,
      vaziDo: this.vaziDo,
      popust: this.popust,
      sport: this.sport
    };

    if (this.idPromocije) {
      this.promocijaService.azurirajPromociju(this.idPromocije, promocija).subscribe({
        next: () => {
          alert("Promocija uspešno ažurirana!");
          this.prikaziFormu = false;
          this.ucitajPodatke();
        },
        error: () => alert("Greška pri ažuriranju promocije.")
      });
    } else {
      this.promocijaService.dodajPromociju(promocija).subscribe({
        next: () => {
          alert("Promocija uspešno kreirana!");
          this.prikaziFormu = false;
          this.ucitajPodatke();
        },
        error: () => alert("Greška pri kreiranju promocije.")
      });
    }
  }
}
