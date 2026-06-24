import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ProdavnicaService } from '../services/prodavnica.service';

@Component({
  selector: 'app-zaposleni-oprema',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './zaposleni-oprema.component.html',
  styleUrls: ['./zaposleni-oprema.component.css']
})
export class ZaposleniOpremaComponent implements OnInit {
  private prodavnicaService = inject(ProdavnicaService);
  private router = inject(Router);

  uloga: string = '';
  
  aktivniTab: string = 'oprema'; // 'oprema' ili 'porudzbine'

  opremaList: any[] = [];
  porudzbine: any[] = [];

  // Forma za opremu
  prikaziFormu: boolean = false;
  idOpreme: number | null = null;
  naziv: string = '';
  sport: string = '';
  cena: number = 0;
  zaliha: number = 0;
  slika: string = '';

  ngOnInit(): void {
    this.uloga = localStorage.getItem('uloga') || '';
    if (this.uloga !== 'ZAPOSLENI') {
      this.router.navigate(['/']);
      return;
    }

    this.ucitajOpremu();
    this.ucitajPorudzbine();
  }

  promeniTab(tab: string) {
    this.aktivniTab = tab;
  }

  ucitajOpremu() {
    this.prodavnicaService.getOprema('SVI').subscribe(res => {
      this.opremaList = res;
    });
  }

  ucitajPorudzbine() {
    this.prodavnicaService.getSvePorudzbine().subscribe(res => {
      this.porudzbine = res;
    });
  }

  novaOprema() {
    this.idOpreme = null;
    this.naziv = '';
    this.sport = '';
    this.cena = 0;
    this.zaliha = 0;
    this.slika = '';
    this.prikaziFormu = true;
  }

  izmeniOpremu(o: any) {
    this.idOpreme = o.id;
    this.naziv = o.naziv;
    this.sport = o.sport;
    this.cena = o.cena;
    this.zaliha = o.zaliha;
    this.slika = o.slika;
    this.prikaziFormu = true;
  }

  odustani() {
    this.prikaziFormu = false;
  }

  sacuvajOpremu() {
    if (!this.naziv || !this.sport || this.cena <= 0 || this.zaliha < 0) {
      alert("Molimo popunite sva polja ispravno.");
      return;
    }

    const oprema = {
      naziv: this.naziv,
      sport: this.sport,
      cena: this.cena,
      zaliha: this.zaliha,
      slika: this.slika
    };

    if (this.idOpreme) {
      this.prodavnicaService.azurirajOpremu(this.idOpreme, oprema).subscribe({
        next: () => {
          alert("Oprema uspešno ažurirana!");
          this.prikaziFormu = false;
          this.ucitajOpremu();
        },
        error: () => alert("Greška pri ažuriranju opreme.")
      });
    } else {
      this.prodavnicaService.dodajOpremu(oprema).subscribe({
        next: () => {
          alert("Oprema uspešno dodata!");
          this.prikaziFormu = false;
          this.ucitajOpremu();
        },
        error: () => alert("Greška pri dodavanju opreme.")
      });
    }
  }

  azurirajStatus(id: number, status: string) {
    this.prodavnicaService.azurirajStatusPorudzbine(id, status).subscribe({
      next: () => {
        alert("Status uspešno ažuriran.");
        this.ucitajPorudzbine();
      },
      error: () => alert("Greška pri ažuriranju statusa.")
    });
  }
}
