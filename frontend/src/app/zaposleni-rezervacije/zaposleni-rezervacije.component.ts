import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { RezervacijaService } from '../services/rezervacija.service';
import { TrenerService } from '../services/trener.service';

@Component({
  selector: 'app-zaposleni-rezervacije',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './zaposleni-rezervacije.component.html',
  styleUrls: ['./zaposleni-rezervacije.component.css']
})
export class ZaposleniRezervacijeComponent implements OnInit {
  private rezervacijaService = inject(RezervacijaService);
  private trenerService = inject(TrenerService);
  private router = inject(Router);

  rezervacije: any[] = [];
  treninzi: any[] = [];
  uloga: string = '';
  korisnickoIme: string = '';

  ngOnInit(): void {
    this.uloga = localStorage.getItem('uloga') || '';
    this.korisnickoIme = localStorage.getItem('korisnickoIme') || '';

    if (this.uloga !== 'ZAPOSLENI') {
      this.router.navigate(['/']);
      return;
    }

    this.ucitajSve();
  }

  ucitajSve() {
    this.rezervacijaService.getRezervacijeZaZaposlenog(this.korisnickoIme).subscribe(res => {
      this.rezervacije = res;
    });
    this.trenerService.getTreninziZaZaposlenog(this.korisnickoIme).subscribe(res => {
      this.treninzi = res;
    });
  }

  isActionVisible(vremeOd: string | Date, status: string): boolean {
    if (status !== 'AKTIVNA' && status !== 'ZAKAZAN') {
      return false; // vec je promenjeno
    }
    const datum = new Date(vremeOd);
    const sad = new Date();
    const rok = new Date(datum.getTime() + 10 * 60000); // do 10 minuta nakon pocetka
    return sad <= rok;
  }

  potvrdiRezervaciju(id: number) {
    this.rezervacijaService.azurirajStatusRezervacije(id, 'POTVRDJENA').subscribe(() => {
      this.ucitajSve();
    });
  }

  odjaviRezervaciju(id: number) {
    this.rezervacijaService.azurirajStatusRezervacije(id, 'NIJE_DOSAO').subscribe(() => {
      this.ucitajSve();
    });
  }

  potvrdiTrening(id: number) {
    this.trenerService.azurirajStatusTreninga(id, 'ODRZAN').subscribe(() => {
      this.ucitajSve();
    });
  }

  odjaviTrening(id: number) {
    this.trenerService.azurirajStatusTreninga(id, 'NIJE_DOSAO').subscribe(() => {
      this.ucitajSve();
    });
  }
}
