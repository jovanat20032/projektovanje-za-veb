import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ObjekatService {
  uri = 'http://localhost:8080/api/objekti';
  private http = inject(HttpClient);

  getStatistika(): Observable<any> {
    return this.http.get<any>(`${this.uri}/statistika`);
  }

  getGradovi(): Observable<string[]> {
    return this.http.get<string[]>(`${this.uri}/gradovi`);
  }

  pretraga(naziv: string, grad: string, sport: string): Observable<any[]> {
    let params = new HttpParams();
    if (naziv) params = params.set('naziv', naziv);
    if (grad) params = params.set('grad', grad);
    if (sport) params = params.set('sport', sport);
    
    return this.http.get<any[]>(`${this.uri}/pretraga`, { params });
  }

  pretragaSportista(naziv: string, grad: string, sport: string, tipTerena: string, slobodniDanas: boolean): Observable<any[]> {
    let params = new HttpParams();
    if (naziv) params = params.set('naziv', naziv);
    if (grad) params = params.set('grad', grad);
    if (sport) params = params.set('sport', sport);
    if (tipTerena) params = params.set('tipTerena', tipTerena);
    if (slobodniDanas) params = params.set('slobodniDanas', slobodniDanas.toString());
    
    return this.http.get<any[]>(`${this.uri}/pretraga-sportista`, { params });
  }

  getObjekatDetalji(id: number): Observable<any> {
    return this.http.get<any>(`${this.uri}/${id}`);
  }
}