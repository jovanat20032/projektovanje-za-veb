import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IzvestajService {
  uri = 'http://localhost:8080/api/izvestaji';
  private http = inject(HttpClient);

  getPopunjenostTerena(mesec: number, godina: number, objekatId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.uri}/popunjenost?mesec=${mesec}&godina=${godina}&objekatId=${objekatId}`);
  }

  getPrometOpreme(mesec: number, godina: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.uri}/promet-opreme?mesec=${mesec}&godina=${godina}`);
  }
}
