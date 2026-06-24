import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ObjekatService } from '../services/objekat.service';
import { RezervacijaService } from '../services/rezervacija.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-rezervacija-detalji',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './rezervacija-detalji.component.html',
  styleUrl: './rezervacija-detalji.component.css'
})
export class RezervacijaDetaljiComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private objekatService = inject(ObjekatService);
  private rezervacijaService = inject(RezervacijaService);

  objekatId: number = 0;
  objekat: any = null;
  galerija: string[] = [];
  tereni: any[] = [];
  map: any;

  // Calendar rotation state
  odabraniTipTerena: string = '';
  tereniIstogTipa: any[] = [];
  trenutniTerenIndex: number = 0;
  trenutniTeren: any = null;

  // Calendar data
  daniUNedelji: Date[] = [];
  sati: number[] = [8,9,10,11,12,13,14,15,16,17,18,19,20,21,22];
  rezervacijeZaTeren: any[] = [];
  
  // Reservation form
  rezDatum: string = '';
  rezVremeOd: number = 8;
  rezSport: string = '';
  sportovi: string[] = [];
  isSportZakljucan: boolean = false;

  ngOnInit(): void {
    this.objekatId = Number(this.route.snapshot.paramMap.get('id'));
    
    this.route.queryParams.subscribe(params => {
      if (params['sport']) {
        this.rezSport = params['sport'];
        this.isSportZakljucan = true;
      }
    });

    this.ucitajDetalje();
    this.generisiSateIDane();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
      this.map = null;
    }
  }

  generisiSateIDane() {
    // Generišemo dane za narednih nedelju dana
    let danas = new Date();
    this.daniUNedelji = [];
    for(let i=0; i<7; i++) {
      let d = new Date(danas);
      d.setDate(danas.getDate() + i);
      this.daniUNedelji.push(d);
    }
  }

  ucitajDetalje() {
    this.objekatService.getObjekatDetalji(this.objekatId).subscribe(res => {
      this.objekat = res.objekat;
      this.tereni = res.tereni;
      this.galerija = res.galerija;
      this.sportovi = res.sportovi || [];

      // Ako nije zakljucan i ima sportova, izaberi prvi podrazumevano
      if (!this.isSportZakljucan && this.sportovi.length > 0) {
        this.rezSport = this.sportovi[0];
      }
      
      setTimeout(() => {
        this.initMap();
      }, 0);
      
      this.postaviRadnoVreme();

      // Podrazumevano odaberi prvi tip terena
      if(this.tereni.length > 0) {
        this.odabraniTipTerena = this.tereni[0].tip;
        this.azurirajTereneIstogTipa();
      }
    });
  }

  postaviRadnoVreme() {
    if(this.objekat && this.objekat.radnoVreme) {
      let parts = this.objekat.radnoVreme.split('-');
      if(parts.length == 2) {
        let odSati = parseInt(parts[0].split(':')[0]);
        let doSati = parseInt(parts[1].split(':')[0]);
        this.sati = [];
        for(let i = odSati; i < doSati; i++) {
          this.sati.push(i);
        }
      }
    }
  }

  azurirajTereneIstogTipa() {
    this.tereniIstogTipa = this.tereni.filter(t => t.tip === this.odabraniTipTerena);
    this.trenutniTerenIndex = 0;
    this.postaviTrenutniTeren();
  }

  postaviTrenutniTeren() {
    if(this.tereniIstogTipa.length > 0) {
      this.trenutniTeren = this.tereniIstogTipa[this.trenutniTerenIndex];
      this.ucitajRezervacijeZaTeren();
    } else {
      this.trenutniTeren = null;
      this.rezervacijeZaTeren = [];
    }
  }

  prethodniTeren() {
    if(this.trenutniTerenIndex > 0) {
      this.trenutniTerenIndex--;
      this.postaviTrenutniTeren();
    }
  }

  sledeciTeren() {
    if(this.trenutniTerenIndex < this.tereniIstogTipa.length - 1) {
      this.trenutniTerenIndex++;
      this.postaviTrenutniTeren();
    }
  }

  ucitajRezervacijeZaTeren() {
    if(!this.trenutniTeren) return;
    this.rezervacijaService.getRezervacijeZaTeren(this.trenutniTeren.id).subscribe(res => {
      this.rezervacijeZaTeren = res;
    });
  }

  jeRezervisan(dan: Date, sat: number): boolean {
    // Formatiramo datum i sat da bismo uporedili sa rezervacijama
    // VremeOd/VremeDo u bazi su datetime formata
    for(let rez of this.rezervacijeZaTeren) {
      let rezOd: Date;
      let rezDo: Date;
      if (Array.isArray(rez.vremeOd)) {
        rezOd = new Date(rez.vremeOd[0], rez.vremeOd[1] - 1, rez.vremeOd[2], rez.vremeOd[3], rez.vremeOd[4] || 0);
        rezDo = new Date(rez.vremeDo[0], rez.vremeDo[1] - 1, rez.vremeDo[2], rez.vremeDo[3], rez.vremeDo[4] || 0);
      } else {
        rezOd = new Date(rez.vremeOd);
        rezDo = new Date(rez.vremeDo);
      }
      
      // Proveravamo da li je dan isti i da li sat upada u interval [od, do)
      if(rezOd.getFullYear() === dan.getFullYear() &&
         rezOd.getMonth() === dan.getMonth() &&
         rezOd.getDate() === dan.getDate()) {
           if(sat >= rezOd.getHours() && sat < rezDo.getHours()) {
             return true;
           }
      }
    }
    return false;
  }

  odaberiTerminIzKalendara(dan: Date, sat: number) {
    if(this.jeRezervisan(dan, sat)) return;
    // Formatiraj datum za input yyyy-MM-dd
    let mesec = (dan.getMonth()+1).toString().padStart(2, '0');
    let danStr = dan.getDate().toString().padStart(2, '0');
    this.rezDatum = `${dan.getFullYear()}-${mesec}-${danStr}`;
    this.rezVremeOd = sat;
  }

  rezervisi() {
    if(!this.rezDatum || !this.rezVremeOd || !this.rezSport) {
      alert('Molimo popunite datum, vreme i sport!');
      return;
    }
    
    let vremeOdStr = `${this.rezDatum}T${this.rezVremeOd.toString().padStart(2, '0')}:00:00`;
    let vremeDoStr = `${this.rezDatum}T${(this.rezVremeOd + 1).toString().padStart(2, '0')}:00:00`;

    let username = localStorage.getItem('korisnickoIme') || 'mina';

    let rez = {
      terenId: this.trenutniTeren.id,
      korisnickoIme: username,
      sport: this.rezSport,
      vremeOd: vremeOdStr,
      vremeDo: vremeDoStr,
      status: 'AKTIVNA'
    };

    this.rezervacijaService.dodajRezervaciju(rez as any).subscribe({
      next: (res) => {
        alert(res.message);
        this.ucitajRezervacijeZaTeren();
      },
      error: (err) => {
        alert(err.error.message || 'Greška pri rezervaciji!');
      }
    });
  }

  initMap() {
    if (this.map) {
      this.map.remove();
    }
    
    // Leaflet ikonica workaround
    const iconRetinaUrl = 'assets/marker-icon-2x.png';
    const iconUrl = 'assets/marker-icon.png';
    const shadowUrl = 'assets/marker-shadow.png';
    const iconDefault = L.icon({
      iconRetinaUrl,
      iconUrl,
      shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      tooltipAnchor: [16, -28],
      shadowSize: [41, 41]
    });
    L.Marker.prototype.options.icon = iconDefault;

    // Hardkodovane koordinate centra BGa
    let mapId = 'map-' + this.objekatId;
    this.map = L.map(mapId).setView([44.8125, 20.4612], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    L.marker([44.8125, 20.4612]).addTo(this.map)
      .bindPopup(this.objekat?.naziv || 'Objekat')
      .openPopup();
      
    // Fix za renderovanje sivih pločica
    setTimeout(() => {
      this.map.invalidateSize();
    }, 100);
  }
}
