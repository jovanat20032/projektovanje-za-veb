import { Routes } from '@angular/router';
import { PocetnaComponent } from './pocetna/pocetna.component';
import { PrijavaComponent } from './prijava/prijava.component';
import { RegistracijaComponent } from './registracija/registracija.component';
import { ProfilComponent } from './profil/profil.component';
import { ZaboravljenaLozinkaComponent } from './zaboravljena-lozinka/zaboravljena-lozinka.component';
import { NovaLozinkaComponent } from './nova-lozinka/nova-lozinka.component';
import { ObjekatDetaljiComponent } from './objekat-detalji/objekat-detalji.component';

import { PretragaRezervacijaComponent } from './pretraga-rezervacija/pretraga-rezervacija.component';
import { RezervacijaDetaljiComponent } from './rezervacija-detalji/rezervacija-detalji.component';
import { OglasiComponent } from './oglasi/oglasi.component';
import { TreneriComponent } from './treneri/treneri.component';
import { ProdavnicaComponent } from './prodavnica/prodavnica.component';
import { StatistikaComponent } from './statistika/statistika.component';

export const routes: Routes = [
    { path: "", component: PocetnaComponent },
    { path: "prijava", component: PrijavaComponent },
    { path: "registracija", component: RegistracijaComponent },
    { path: "profil", component: ProfilComponent },
    { path: "zaboravljena-lozinka", component: ZaboravljenaLozinkaComponent },
    { path: "nova-lozinka/:token", component: NovaLozinkaComponent },
    { path: "objekat-detalji/:id", component: ObjekatDetaljiComponent },
    { path: "pretraga-rezervacija", component: PretragaRezervacijaComponent },
    { path: "rezervacija-detalji/:id", component: RezervacijaDetaljiComponent },
    { path: "oglasi", component: OglasiComponent },
    { path: "treneri", component: TreneriComponent },
    { path: "prodavnica", component: ProdavnicaComponent },
    { path: "statistika", component: StatistikaComponent }
];
