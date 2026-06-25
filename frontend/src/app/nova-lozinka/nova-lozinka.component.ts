import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { KorisnikService } from '../services/korisnik.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nova-lozinka',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './nova-lozinka.component.html'
})
export class NovaLozinkaComponent implements OnInit {
  private korisnikService = inject(KorisnikService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  tokenIzUrla: string = "";
  novaLozinka: string = "";
  potvrdaLozinke: string = "";
  poruka: string = "";

  ngOnInit() {
    this.tokenIzUrla = this.route.snapshot.paramMap.get('token') || "";
  }

  promeniLozinku() {
    if (this.novaLozinka !== this.potvrdaLozinke) {
      this.poruka = "Lozinke se ne poklapaju!";
      return;
    }

    const lozinkaRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?])[a-zA-Z].{7,11}$/;
    if (!lozinkaRegex.test(this.novaLozinka)) {
      this.poruka = "Lozinka mora imati 8-12 karaktera, počinjati slovom, i sadržati bar jedno veliko slovo, broj i specijalni karakter.";
      return;
    }

    this.korisnikService.promenaLozinke(this.tokenIzUrla, this.novaLozinka).subscribe({
      next: () => {
        alert("Vaša lozinka je uspešno promenjena!");
        this.router.navigate(['/prijava']);
      },
      error: (err) => {
        this.poruka = err.error || "Došlo je do greške. Moguće je da je link istekao.";
      }
    });
  }
}