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
}
