import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { CategoriaDTO } from '../models/categoria.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private base = `${environment.apiUrl}/categorias`;
  constructor(private http: HttpClient) {}

  getAll(): Observable<CategoriaDTO[]> {
    return this.http.get<CategoriaDTO[]>(this.base).pipe(catchError(this.handleError));
  }
  getById(id: number) {
    return this.http.get<CategoriaDTO>(`${this.base}/${id}`).pipe(catchError(this.handleError));
  }
  create(dep: CategoriaDTO) {
    return this.http.post<CategoriaDTO>(this.base, dep).pipe(catchError(this.handleError));
  }
  update(id: number, dep: CategoriaDTO) {
    return this.http.put<CategoriaDTO>(`${this.base}/${id}`, dep).pipe(catchError(this.handleError));
  }
  delete(id: number) {
    return this.http.delete(`${this.base}/${id}`).pipe(catchError(this.handleError));
  }
  private handleError(err: any) {
    console.error(err);
    return throwError(() => err);
  }
}
