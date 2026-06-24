import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ObjekatService } from '../services/objekat.service';
import { IzvestajService } from '../services/izvestaj.service';
import { jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-zaposleni-izvestaji',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './zaposleni-izvestaji.component.html',
  styleUrls: ['./zaposleni-izvestaji.component.css']
})
export class ZaposleniIzvestajiComponent implements OnInit {
  objekatService = inject(ObjekatService);
  izvestajService = inject(IzvestajService);

  objekti: any[] = [];
  izabranObjekat: number = 0;
  izabranMesec: number = new Date().getMonth() + 1;
  izabranaGodina: number = new Date().getFullYear();

  meseci = [
    { vrednost: 1, naziv: 'Januar' }, { vrednost: 2, naziv: 'Februar' },
    { vrednost: 3, naziv: 'Mart' }, { vrednost: 4, naziv: 'April' },
    { vrednost: 5, naziv: 'Maj' }, { vrednost: 6, naziv: 'Jun' },
    { vrednost: 7, naziv: 'Jul' }, { vrednost: 8, naziv: 'Avgust' },
    { vrednost: 9, naziv: 'Septembar' }, { vrednost: 10, naziv: 'Oktobar' },
    { vrednost: 11, naziv: 'Novembar' }, { vrednost: 12, naziv: 'Decembar' }
  ];

  ngOnInit(): void {
    const korisnickoIme = localStorage.getItem('korisnickoIme');
    if (korisnickoIme) {
      this.objekatService.getObjektiZaZaposlenog(korisnickoIme).subscribe(res => {
        this.objekti = res;
        if (this.objekti.length > 0) {
          this.izabranObjekat = this.objekti[0].id;
        }
      });
    }
  }

  generisiIzvestajPopunjenosti() {
    if (!this.izabranObjekat) {
      alert("Odaberite objekat!");
      return;
    }
    this.izvestajService.getPopunjenostTerena(this.izabranMesec, this.izabranaGodina, this.izabranObjekat).subscribe({
      next: (data) => {
        const doc = new jsPDF();
        doc.text(`Izvestaj o popunjenosti terena - ${this.izabranMesec}/${this.izabranaGodina}`, 14, 20);

        const tableData = data.map(item => [
          item.naziv,
          item.ukupnoSati.toFixed(2),
          item.ukupnoMogucihSati.toFixed(2),
          item.procenatPopunjenosti.toFixed(2) + '%'
        ]);

        autoTable(doc, {
          startY: 30,
          head: [['Naziv terena', 'Rezervisano sati', 'Ukupno mogucih sati', 'Popunjenost (%)']],
          body: tableData,
        });

        doc.save(`Izvestaj_Popunjenost_${this.izabranMesec}_${this.izabranaGodina}.pdf`);
      },
      error: (err) => console.error(err)
    });
  }

  generisiIzvestajOpreme() {
    this.izvestajService.getPrometOpreme(this.izabranMesec, this.izabranaGodina).subscribe({
      next: (data) => {
        const doc = new jsPDF();
        doc.text(`Izvestaj o prometu opreme - ${this.izabranMesec}/${this.izabranaGodina}`, 14, 20);

        const tableData = data.map(item => [
          item.naziv,
          item.ukupnoKolicina.toString(),
          item.ukupnaZarada.toFixed(2) + ' RSD'
        ]);

        autoTable(doc, {
          startY: 30,
          head: [['Naziv opreme', 'Prodata kolicina', 'Ukupna zarada']],
          body: tableData,
        });

        doc.save(`Izvestaj_Oprema_${this.izabranMesec}_${this.izabranaGodina}.pdf`);
      },
      error: (err) => console.error(err)
    });
  }
}
