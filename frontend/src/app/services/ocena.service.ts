import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OcenaService {

  private apiUrl = 'http://localhost:8080/api/ocene';

  constructor(private http: HttpClient) { }

  dohvatiKomentare(objekatId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${objekatId}`);
  }

  dodajKomentarOcenu(ocena: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, ocena);
  }
}
