import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Korisnik } from '../models/korisnik';
import { Observable } from 'rxjs';
import { Sport } from '../models/sport';
import { Rezervacija } from '../models/rezervacija';

@Injectable({
  providedIn: 'root'
})
export class KorisnikService {

  uri = 'http://localhost:8080/api/korisnici';
  constructor() { }
  private http = inject(HttpClient)
  
  prijavaNaSistem(korisnickoIme: string, lozinka: string) {
    const data = {
      korisnickoIme: korisnickoIme,
      lozinka: lozinka,
    };
    return this.http.post<any>(`${this.uri}/login`, data);
  }

  register(korisnik: Korisnik): Observable<any> {
    return this.http.post(`${this.uri}/register`, korisnik);
  }

  zahtevZaReset(unos: string) {
    return this.http.post<any>(`${this.uri}/zahtev-za-reset`, { unos: unos });
  }

  promenaLozinke(token: string, novaLozinka: string) {
    return this.http.post<any>(`${this.uri}/promena-lozinke`, { token: token, novaLozinka: novaLozinka });
  }

  dohvatiKorisnika(korisnickoIme: string): Observable<Korisnik> {
    return this.http.get<Korisnik>(`${this.uri}/dohvatiKorisnika?korisnickoIme=${korisnickoIme}`);
  }

  azurirajProfil(korisnik: Korisnik): Observable<any> {
    return this.http.post(`${this.uri}/azurirajProfil`, korisnik, { responseType: 'text' });
  }

  dohvatiSveSportove(): Observable<Sport[]> {
    return this.http.get<Sport[]>(`${this.uri}/sviSportovi`);
  }

  dohvatiOmiljeneSportove(korisnickoIme: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.uri}/omiljeniSportovi?korisnickoIme=${korisnickoIme}`);
  }

  azurirajSportove(korisnickoIme: string, sportovi: string[]): Observable<any> {
    return this.http.post(`${this.uri}/azurirajSportove?korisnickoIme=${korisnickoIme}`, sportovi, { responseType: 'text' });
  }

dohvatiMojeRezervacije(korisnickoIme: string): Observable<Rezervacija[]> {
  return this.http.get<Rezervacija[]>(`${this.uri}/mojeRezervacije?korisnickoIme=${korisnickoIme}`);
}

otkaziRezervaciju(id: number): Observable<any> {
  return this.http.post(`${this.uri}/otkaziRezervaciju?id=${id}`, {}, { responseType: 'text' });
}
}
