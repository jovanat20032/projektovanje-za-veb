import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StatistikaService {

  private apiUrl = 'http://localhost:8080/api/statistika';

  constructor(private http: HttpClient) { }

  getRezervacijePoSportu(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/rezervacije-po-sportu`);
  }

  getMesecnaAktivnost(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/mesecna-aktivnost`);
  }

  getPotrosnja(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/potrosnja`);
  }
}
