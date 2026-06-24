import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Rezervacija } from '../models/rezervacija';

@Injectable({
  providedIn: 'root'
})
export class RezervacijaService {
  uri = 'http://localhost:8080/api/rezervacije';
  private http = inject(HttpClient);

  getRezervacijeZaTeren(terenId: number): Observable<Rezervacija[]> {
    return this.http.get<Rezervacija[]>(`${this.uri}/teren/${terenId}`);
  }

  dodajRezervaciju(rezervacija: Rezervacija): Observable<any> {
    return this.http.post<any>(`${this.uri}`, rezervacija);
  }

  getRezervacijeZaZaposlenog(korisnickoIme: string): Observable<Rezervacija[]> {
    return this.http.get<Rezervacija[]>(`${this.uri}/zaposleni/${korisnickoIme}`);
  }

  azurirajStatusRezervacije(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.uri}/${id}/status`, { status });
  }

  pomeriRezervaciju(id: number, vremeOd: string, vremeDo: string): Observable<any> {
    return this.http.put<any>(`${this.uri}/${id}/pomeri`, { vremeOd, vremeDo });
  }
}
