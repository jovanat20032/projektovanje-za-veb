import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../services/admin.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-sportovi',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-sportovi.component.html',
  styleUrls: ['./admin-sportovi.component.css']
})
export class AdminSportoviComponent {
  adminService = inject(AdminService);
  
  naziv: string = '';
  timski: boolean = false;

  dodajSport() {
    if (!this.naziv.trim()) {
      alert("Unesite naziv sporta.");
      return;
    }

    this.adminService.dodajSport(this.naziv, this.timski).subscribe(res => {
      alert(res);
      this.naziv = '';
      this.timski = false;
    });
  }
}
