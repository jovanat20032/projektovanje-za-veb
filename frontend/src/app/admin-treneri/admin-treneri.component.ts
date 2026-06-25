import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../services/admin.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-treneri',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-treneri.component.html',
  styleUrls: ['./admin-treneri.component.css']
})
export class AdminTreneriComponent implements OnInit {
  adminService = inject(AdminService);
  treneri: any[] = [];

  ngOnInit(): void {
    this.ucitajTrenere();
  }

  ucitajTrenere() {
    this.adminService.getSviTreneri().subscribe(res => {
      this.treneri = res;
    });
  }

  deaktiviraj(korisnickoIme: string) {
    if (confirm(`Da li ste sigurni da zelite da deaktivirate trenera: ${korisnickoIme}? On ce i dalje biti zaposleni, ali nece vise biti trener.`)) {
      this.adminService.deaktivirajTrenera(korisnickoIme).subscribe(res => {
        alert(res);
        this.ucitajTrenere();
      });
    }
  }
}
