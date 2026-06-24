import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ObjekatService } from '../services/objekat.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';

@Component({
  selector: 'app-upravljanje-objektom',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './upravljanje-objektom.component.html',
  styleUrls: ['./upravljanje-objektom.component.css']
})
export class UpravljanjeObjektomComponent implements OnInit {
  private objekatService = inject(ObjekatService);
  private korisnikService = inject(KorisnikService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  objekatId: number | null = null;
  poruka: string = '';
  porukaGreska: string = '';

  sviSportovi: any[] = [];

  dto: any = {
    naziv: '',
    grad: '',
    adresa: '',
    maticniBroj: '',
    pib: '',
    cenaPoSatu: 0,
    radnoVreme: '',
    dozvoljeniMinusi: 0,
    tereni: [],
    vrsteSportova: []
  };

  noviTeren: any = {
    naziv: '',
    tip: 'OTVORENI',
    kapacitet: 10,
    opisOpreme: ''
  };

  ngOnInit(): void {
    const uloga = localStorage.getItem('uloga');
    if (uloga !== 'ZAPOSLENI') {
      this.router.navigate(['/']);
      return;
    }

    this.korisnikService.dohvatiSveSportove().subscribe({
      next: (sportovi) => this.sviSportovi = sportovi,
      error: (err) => console.error(err)
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.objekatId = parseInt(idParam, 10);
      this.ucitajObjekat(this.objekatId);
    }
  }

  ucitajObjekat(id: number) {
    this.objekatService.getObjekatDetalji(id).subscribe({
      next: (res) => {
        this.dto.naziv = res.objekat.naziv;
        this.dto.grad = res.objekat.grad;
        this.dto.adresa = res.objekat.adresa;
        this.dto.maticniBroj = res.objekat.maticniBroj;
        this.dto.pib = res.objekat.pib;
        this.dto.cenaPoSatu = res.objekat.cenaPoSatu;
        this.dto.radnoVreme = res.objekat.radnoVreme;
        this.dto.dozvoljeniMinusi = res.objekat.dozvoljeniMinusi;
        this.dto.tereni = res.tereni || [];
        this.dto.vrsteSportova = res.sportovi || [];
      },
      error: (err) => console.error("Greška pri dohvatanju objekta", err)
    });
  }

  dodajTeren() {
    if (!this.noviTeren.naziv) {
      this.porukaGreska = "Naziv terena je obavezan.";
      return;
    }
    if (this.noviTeren.opisOpreme && this.noviTeren.opisOpreme.length > 300) {
      this.porukaGreska = "Opis opreme može imati najviše 300 karaktera.";
      return;
    }
    this.porukaGreska = '';
    this.dto.tereni.push({...this.noviTeren});
    this.noviTeren = { naziv: '', tip: 'teren', kapacitet: 10, opisOpreme: '' };
  }

  obrisiTeren(index: number) {
    this.dto.tereni.splice(index, 1);
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        try {
          const jsonData = JSON.parse(e.target.result);
          this.dto = { ...this.dto, ...jsonData };
          if (!this.dto.tereni) this.dto.tereni = [];
          this.poruka = "Podaci uspešno učitani iz fajla!";
          this.porukaGreska = '';
        } catch (error) {
          this.porukaGreska = "Greška pri parsiranju JSON fajla.";
          this.poruka = '';
        }
      };
      reader.readAsText(file);
    }
  }

  sacuvaj() {
    this.poruka = '';
    this.porukaGreska = '';
    
    if (this.objekatId) {
      this.objekatService.azurirajObjekat(this.objekatId, this.dto).subscribe({
        next: (res) => {
          this.poruka = "Objekat je uspešno ažuriran!";
          setTimeout(() => this.router.navigate(['/profil']), 1500);
        },
        error: (err) => this.porukaGreska = "Došlo je do greške pri ažuriranju."
      });
    } else {
      const korisnickoIme = localStorage.getItem('korisnickoIme');
      if (!korisnickoIme) return;

      this.objekatService.dodajObjekat(this.dto, korisnickoIme).subscribe({
        next: (res) => {
          this.poruka = "Objekat je uspešno dodat!";
          setTimeout(() => this.router.navigate(['/profil']), 1500);
        },
        error: (err) => this.porukaGreska = "Došlo je do greške pri dodavanju."
      });
    }
  }
}
