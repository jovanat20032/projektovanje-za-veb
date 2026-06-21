import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ObjekatService } from '../services/objekat.service';

@Component({
  selector: 'app-objekat-detalji',
  standalone: true,
  imports: [],
  templateUrl: './objekat-detalji.component.html',
  styleUrl: './objekat-detalji.component.css'
})
export class ObjekatDetaljiComponent implements OnInit{
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private objekatService = inject(ObjekatService);

  objekat: any = null;
  tereni: any[] = [];
  galerija: string[] = [];

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.ucitajDetalje(+idParam);
    }
  }

  ucitajDetalje(id: number) {
    this.objekatService.getObjekatDetalji(id).subscribe({
      next: (res: any) => {
        this.objekat = res.objekat;
        this.tereni = res.tereni;
        this.galerija = res.galerija || [];
      },
      error: () => {
        alert('Došlo je do greške ili objekat ne postoji.');
        this.nazad();
      }
    });
  }

  nazad() {
    this.router.navigate(['/']);
  }
}
