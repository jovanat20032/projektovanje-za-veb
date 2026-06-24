import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrenerService {

  private apiUrl = 'http://localhost:8080/api/treneri';

  constructor(private http: HttpClient) { }

  getTreneriByObjekatAndSport(objekatId: number, sport: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/objekat/${objekatId}/sport/${sport}`);
  }

  zakaziTrening(rezervacija: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/zakazi`, rezervacija);
  }

  getTreninziZaSportistu(korisnickoIme: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/sportista/${korisnickoIme}`);
  }

  getTreninziZaZaposlenog(korisnickoIme: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/zaposleni/${korisnickoIme}/treninzi`);
  }

  azurirajStatusTreninga(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/treninzi/${id}/status`, { status });
  }

  getTreninziZaTeren(terenId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/teren/${terenId}`);
  }

  pomeriTrening(id: number, novoVreme: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/pomeri`, { novoVreme });
  }
}
