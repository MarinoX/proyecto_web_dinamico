import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RegistroDTO } from '../models/registro.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RegistroService {
  private base = `${environment.apiUrl}/registros`;
  constructor(private http: HttpClient) {}

  getAll(): Observable<RegistroDTO[]> {
    return this.http.get<RegistroDTO[]>(this.base).pipe(catchError(this.handleError));
  }

  getById(id: number) {
    return this.http.get<RegistroDTO>(`${this.base}/${id}`).pipe(catchError(this.handleError));
  }

  create(reg: RegistroDTO) {
    return this.http.post<RegistroDTO>(this.base, reg).pipe(catchError(this.handleError));
  }

  update(id: number, reg: RegistroDTO) {
    return this.http.put<RegistroDTO>(`${this.base}/${id}`, reg).pipe(catchError(this.handleError));
  }

  delete(id: number) {
    return this.http.delete(`${this.base}/${id}`).pipe(catchError(this.handleError));
  }

  getByProductoId(productoId: number) {
    return this.http.get<RegistroDTO[]>(`${this.base}/producto/${productoId}`).pipe(catchError(this.handleError));
  }

  private handleError(err: any) {
    console.error(err);
    return throwError(() => err);
  }
}
