import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OglasService {
  private apiUrl = 'http://localhost:8080/api/oglasi';

  constructor(private http: HttpClient) { }

  getAllAktivniOglasi(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getOglasiByKorisnik(korisnickoIme: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/korisnik/${korisnickoIme}`);
  }

  kreirajOglas(oglas: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, oglas);
  }

  zatvoriOglas(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/zatvori`, {});
  }

  prijaviSeNaOglas(id: number, korisnickoIme: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/prijava`, { korisnickoIme });
  }

  getZahtevi(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/zahtevi`);
  }

  azurirajStatusZahteva(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/zahtev/${id}/status`, { status });
  }
}
