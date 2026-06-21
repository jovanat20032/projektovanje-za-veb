import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Objekat } from '../models/objekat';
import { Router } from '@angular/router';
import { ObjekatService } from '../services/objekat.service';
import { Promocija } from '../models/promocija';
import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'app-pocetna',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './pocetna.component.html',
  styleUrl: './pocetna.component.css'
})
export class PocetnaComponent implements OnInit{
  private objekatService = inject(ObjekatService);
  private router = inject(Router);

  ukupanBrojAktivnih: number = 0;
  top3Objekti: any[] = [];
  
  gradovi: string[] = [];
  pretNaziv: string = '';
  pretGrad: string = '';
  pretSport: string = '';
  pretTipTerena: string = ''; 

  rezultatiPretrage: any[] = [];
  promocije: Promocija[] = [];
  
  sortKolona: string = '';
  sortSmer: 'asc' | 'desc' = 'asc';

  ngOnInit(): void {
    this.ucitajStatistiku();
    this.ucitajGradove();
  }

  ucitajStatistiku() {
    this.objekatService.getStatistika().subscribe(res => {
      this.ukupanBrojAktivnih = res.ukupanBroj;
      this.top3Objekti = res.top3;
      this.promocije = res.promocije;
    });
  }

  ucitajGradove() {
    this.objekatService.getGradovi().subscribe(res => {
      this.gradovi = res;
    });
  }

  pretrazi() {
    this.objekatService.pretraga(this.pretNaziv, this.pretGrad, this.pretSport).subscribe(res => {
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
    this.router.navigate(['objekat-detalji', id]);
  }
}
