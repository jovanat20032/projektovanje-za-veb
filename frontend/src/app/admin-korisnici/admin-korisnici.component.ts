import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../services/admin.service';
import { Korisnik } from '../models/korisnik';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-korisnici',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-korisnici.component.html',
  styleUrls: ['./admin-korisnici.component.css']
})
export class AdminKorisniciComponent implements OnInit {
  adminService = inject(AdminService);
  korisnici: Korisnik[] = [];

  izabraniKorisnik: Korisnik | null = null;

  ngOnInit(): void {
    this.ucitajKorisnike();
  }

  ucitajKorisnike() {
    this.adminService.getSviKorisnici().subscribe(res => {
      this.korisnici = res;
    });
  }

  izmeni(k: Korisnik) {
    this.izabraniKorisnik = { ...k };
  }

  sacuvajIzmene() {
    if (this.izabraniKorisnik) {
      this.adminService.updateKorisnik(this.izabraniKorisnik).subscribe(res => {
        alert(res);
        this.izabraniKorisnik = null;
        this.ucitajKorisnike();
      });
    }
  }

  otkaziIzmene() {
    this.izabraniKorisnik = null;
  }

  obrisi(korisnickoIme: string) {
    if (confirm("Da li ste sigurni da zelite da obrisete ovog korisnika?")) {
      this.adminService.obrisiKorisnika(korisnickoIme).subscribe(res => {
        alert(res);
        this.ucitajKorisnike();
      });
    }
  }
}
