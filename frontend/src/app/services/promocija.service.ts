import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PromocijaService {
  private apiUrl = 'http://localhost:8080/api/promocije';
  private http = inject(HttpClient);

  getPromocijeZaZaposlenog(korisnickoIme: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/zaposleni/${korisnickoIme}`);
  }

  dodajPromociju(promocija: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, promocija);
  }

  azurirajPromociju(id: number, promocija: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, promocija);
  }
}
