import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Korisnik } from '../models/korisnik';
import { Observable } from 'rxjs';

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
}
