import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { KorisnikService } from '../services/korisnik.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-prijava',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './prijava.component.html',
  styleUrl: './prijava.component.css'
})
export class PrijavaComponent {
  private korisnikService = inject(KorisnikService);
  private router = inject(Router);

  korisnickoIme: string = "";
  lozinka: string = "";
  poruka: string = "";

  prijavaNaSistem() {
    this.korisnikService.prijavaNaSistem(this.korisnickoIme, this.lozinka).subscribe({
      next: (odgovorSaBekenda) => {
  
        if (odgovorSaBekenda && odgovorSaBekenda.token) {
          this.poruka = '';
          
          localStorage.setItem('jwt_token', odgovorSaBekenda.token);
          localStorage.setItem('uloga', odgovorSaBekenda.uloga);
          localStorage.setItem('korisnickoIme', this.korisnickoIme);
          
          if (odgovorSaBekenda.uloga == "ADMIN") {
            this.router.navigate(['admin-dashboard']);
          } else if (odgovorSaBekenda.uloga == "ZAPOSLENI") {
            this.router.navigate(['zaposleni-kalendar']);
          } else {
            this.router.navigate(['profil']);
          }
        }
      },
      error: () => {
        this.poruka = 'Pogrešno korisničko ime ili lozinka';
      }
    });
  }
  registracija(){
    this.router.navigate(['registracija'])
  }
  zaboravljena(){
    this.router.navigate(['zaboravljena-lozinka'])
  }
}
