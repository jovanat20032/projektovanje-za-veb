import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ObjekatService } from '../services/objekat.service';
import { OcenaService } from '../services/ocena.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-objekat-detalji',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './objekat-detalji.component.html',
  styleUrl: './objekat-detalji.component.css'
})
export class ObjekatDetaljiComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private objekatService = inject(ObjekatService);
  private ocenaService = inject(OcenaService);

  objekat: any = null;
  tereni: any[] = [];
  galerija: string[] = [];
  
  komentari: any[] = [];
  noviKomentarTekst: string = '';
  ulogovanKorisnik: string = '';

  ngOnInit() {
    this.ulogovanKorisnik = localStorage.getItem('korisnickoIme') || '';

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.ucitajDetalje(+idParam);
      this.ucitajKomentare(+idParam);
    }
  }

  ucitajDetalje(id: number) {
    this.objekatService.getObjekatDetalji(id).subscribe({
      next: (res: any) => {
        this.objekat = res.objekat;
        this.tereni = res.tereni;
        this.galerija = res.galerija || [];
      },
      error: () => {
        alert('Došlo je do greške ili objekat ne postoji.');
        this.nazad();
      }
    });
  }

  ucitajKomentare(id: number) {
    this.ocenaService.dohvatiKomentare(id).subscribe({
      next: (res: any[]) => {
        this.komentari = res;
      },
      error: (err) => console.error(err)
    });
  }

  ostaviOcenu(reakcija: string | null) {
    if (!this.ulogovanKorisnik) {
      alert("Morate biti ulogovani da biste ocenjivali i komentarisali.");
      return;
    }

    const ocenaDto = {
      objekatId: this.objekat.id,
      korisnickoIme: this.ulogovanKorisnik,
      tekst: this.noviKomentarTekst,
      reakcija: reakcija
    };

    this.ocenaService.dodajKomentarOcenu(ocenaDto).subscribe({
      next: (res) => {
        alert(res.message);
        this.noviKomentarTekst = '';
        this.ucitajKomentare(this.objekat.id);
        this.ucitajDetalje(this.objekat.id); // Da osvežimo broj lajkova
      },
      error: (err) => {
        alert(err.error.message || 'Došlo je do greške.');
      }
    });
  }

  nazad() {
    this.router.navigate(['/']);
  }
}
