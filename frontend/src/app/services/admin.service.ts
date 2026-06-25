import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Korisnik } from '../models/korisnik';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  uri = 'http://localhost:8080/api/admin';
  private http = inject(HttpClient);

  // Korisnici
  getSviKorisnici(): Observable<Korisnik[]> {
    return this.http.get<Korisnik[]>(`${this.uri}/korisnici`);
  }
  
  updateKorisnik(k: Korisnik): Observable<any> {
    return this.http.put(`${this.uri}/korisnici`, k, { responseType: 'text' });
  }

  obrisiKorisnika(korisnickoIme: string): Observable<any> {
    return this.http.delete(`${this.uri}/korisnici/${korisnickoIme}`, { responseType: 'text' });
  }

  // Zahtevi registracije
  getZahteviKorisnika(): Observable<Korisnik[]> {
    return this.http.get<Korisnik[]>(`${this.uri}/zahtevi/korisnici`);
  }

  odluciOKorisniku(korisnickoIme: string, odluka: string): Observable<any> {
    return this.http.post(`${this.uri}/zahtevi/korisnici/${korisnickoIme}/${odluka}`, null, { responseType: 'text' });
  }

  // Zahtevi objekti
  getZahteviObjekata(): Observable<any[]> {
    return this.http.get<any[]>(`${this.uri}/zahtevi/objekti`);
  }

  odluciOObjektu(id: number, odluka: string): Observable<any> {
    return this.http.post(`${this.uri}/zahtevi/objekti/${id}/${odluka}`, null, { responseType: 'text' });
  }

  // Treneri
  getSviTreneri(): Observable<any[]> {
    return this.http.get<any[]>(`${this.uri}/treneri`);
  }

  deaktivirajTrenera(korisnickoIme: string): Observable<any> {
    return this.http.delete(`${this.uri}/treneri/${korisnickoIme}`, { responseType: 'text' });
  }

  // Sportovi
  dodajSport(naziv: string, timski: boolean): Observable<any> {
    return this.http.post(`${this.uri}/sportovi`, { naziv, timski }, { responseType: 'text' });
  }
}
