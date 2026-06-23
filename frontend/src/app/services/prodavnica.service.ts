import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProdavnicaService {

  private apiUrl = 'http://localhost:8080/api/prodavnica';

  constructor(private http: HttpClient) { }

  getOprema(sport: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/oprema?sport=${sport}`);
  }

  naruci(porudzbina: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/naruci`, porudzbina);
  }

  getMojePorudzbine(korisnickoIme: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/porudzbine/${korisnickoIme}`);
  }

  otkaziPorudzbinu(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/porudzbine/${id}/otkazi`, {});
  }
}
