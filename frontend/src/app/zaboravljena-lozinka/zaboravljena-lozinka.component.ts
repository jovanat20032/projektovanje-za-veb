import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';

@Component({
  selector: 'app-zaboravljena-lozinka',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './zaboravljena-lozinka.component.html',
  styleUrl: './zaboravljena-lozinka.component.css'
})
export class ZaboravljenaLozinkaComponent {
  
  private korisnikService = inject(KorisnikService);
  
  unos: string = "";
  poruka: string = "";
  dobijeniLink: string = "";

  private router = inject(Router);

  posaljiZahtev() {
    this.korisnikService.zahtevZaReset(this.unos).subscribe({
      next: (odgovor) => {
        this.poruka = "Zahtev odobren! (link za promenu ispod)";
        this.dobijeniLink = odgovor.link;
      },
      error: (err) => {
        this.poruka = err.error || "Korisnik nije pronađen.";
        this.dobijeniLink = "";
      }
    });
  }
  
  nazadPrijava(){
    this.router.navigate(['prijava'])
  }
}
