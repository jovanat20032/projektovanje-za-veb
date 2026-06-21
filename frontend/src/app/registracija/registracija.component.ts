import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';
import { Korisnik } from '../models/korisnik';

@Component({
  selector: 'app-registracija',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './registracija.component.html',
  styleUrl: './registracija.component.css'
})
export class RegistracijaComponent {
  private korisnikService = inject(KorisnikService);
  private router = inject(Router);

  noviKorisnik: Korisnik = new Korisnik();
  poruka: string = "";

  registrujSe() {
   const lozinkaRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[a-zA-Z].{7,11}$/;

    if (!lozinkaRegex.test(this.noviKorisnik.lozinka)) {
      this.poruka = "Lozinka mora imati 8-12 karaktera, pocinjati slovom, i sadrzati bar jedno veliko slovo, broj i specijalni karakter.";
      return;
    }

    this.korisnikService.register(this.noviKorisnik).subscribe({
      next: (odgovor) => {
        alert("Zahtev za registraciju uspesno poslat! Ceka se odobrenje administratora.");
        this.router.navigate(['/prijava']);
      },
      error: (greska) => {
        if (greska.status === 400) {
          this.poruka = "Korisnicko ime je vec zauzeto.";
        } else {
          this.poruka = "Doslo je do greske na serveru.";
        }
      }
    });
  }
}
