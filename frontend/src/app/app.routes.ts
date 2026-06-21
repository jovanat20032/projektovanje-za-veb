import { Routes } from '@angular/router';
import { PocetnaComponent } from './pocetna/pocetna.component';
import { PrijavaComponent } from './prijava/prijava.component';
import { RegistracijaComponent } from './registracija/registracija.component';
import { ProfilComponent } from './profil/profil.component';
import { ZaboravljenaLozinkaComponent } from './zaboravljena-lozinka/zaboravljena-lozinka.component';
import { NovaLozinkaComponent } from './nova-lozinka/nova-lozinka.component';
import { ObjekatDetaljiComponent } from './objekat-detalji/objekat-detalji.component';

export const routes: Routes = [
    { path: "", component: PocetnaComponent },
    { path: "prijava", component: PrijavaComponent },
    { path: "registracija", component: RegistracijaComponent },
    { path: "profil", component: ProfilComponent },
    { path: "zaboravljena-lozinka", component: ZaboravljenaLozinkaComponent },
    { path: "nova-lozinka/:token", component: NovaLozinkaComponent },
    { path: "objekat-detalji/:id", component: ObjekatDetaljiComponent }
];
