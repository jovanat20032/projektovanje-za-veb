import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../services/admin.service';
import { Korisnik } from '../models/korisnik';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-zahtevi',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-zahtevi.component.html',
  styleUrls: ['./admin-zahtevi.component.css']
})
export class AdminZahteviComponent implements OnInit {
  adminService = inject(AdminService);
  zahteviKorisnici: Korisnik[] = [];
  zahteviObjekti: any[] = [];

  ngOnInit(): void {
    this.ucitajKorisnike();
    this.ucitajObjekte();
  }

  ucitajKorisnike() {
    this.adminService.getZahteviKorisnika().subscribe(res => {
      this.zahteviKorisnici = res;
    });
  }

  ucitajObjekte() {
    this.adminService.getZahteviObjekata().subscribe(res => {
      this.zahteviObjekti = res;
    });
  }

  odluciOKorisniku(korisnickoIme: string, odluka: string) {
    this.adminService.odluciOKorisniku(korisnickoIme, odluka).subscribe(res => {
      alert(res);
      this.ucitajKorisnike();
    });
  }

  odluciOObjektu(id: number, odluka: string) {
    this.adminService.odluciOObjektu(id, odluka).subscribe(res => {
      alert(res);
      this.ucitajObjekte();
    });
  }
}
