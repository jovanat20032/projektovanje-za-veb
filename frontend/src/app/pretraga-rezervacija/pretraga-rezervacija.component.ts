import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ObjekatService } from '../services/objekat.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-pretraga-rezervacija',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './pretraga-rezervacija.component.html',
  styleUrl: './pretraga-rezervacija.component.css'
})
export class PretragaRezervacijaComponent implements OnInit {
  private objekatService = inject(ObjekatService);
  private router = inject(Router);

  gradovi: string[] = [];
  pretNaziv: string = '';
  pretGrad: string = '';
  pretSport: string = '';
  pretTipTerena: string = ''; 
  slobodniDanas: boolean = false;

  rezultatiPretrage: any[] = [];
  
  sortKolona: string = '';
  sortSmer: 'asc' | 'desc' = 'asc';

  ngOnInit(): void {
    this.ucitajGradove();
  }

  ucitajGradove() {
    this.objekatService.getGradovi().subscribe(res => {
      this.gradovi = res;
    });
  }

  pretrazi() {
    this.objekatService.pretragaSportista(this.pretNaziv, this.pretGrad, this.pretSport, this.pretTipTerena, this.slobodniDanas).subscribe(res => {
      this.rezultatiPretrage = res;
    });
  }

  sortiraj(kolona: string) {
    if (this.sortKolona === kolona) {
      this.sortSmer = this.sortSmer === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKolona = kolona;
      this.sortSmer = 'asc';
    }

    this.rezultatiPretrage.sort((a, b) => {
      let valA = a[kolona] || '';
      let valB = b[kolona] || '';
      
      if (valA < valB) return this.sortSmer === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortSmer === 'asc' ? 1 : -1;
      return 0;
    });
  }

  idiNaDetalje(id: number) {
    this.router.navigate(['/rezervacija-detalji', id], { queryParams: { sport: this.pretSport } });
  }
}
