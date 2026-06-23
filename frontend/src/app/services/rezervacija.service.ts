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
}
