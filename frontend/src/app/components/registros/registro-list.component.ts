import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { RegistroDTO } from '../../models/registro.model';
import { RegistroService } from '../../services/registro.service';
import { RegistroFormComponent } from './registro-form.component';
import { ViewDialogComponent } from '../../shared/view-dialog.component';

@Component({
  selector: 'app-registro-list',
  templateUrl: './registro-list.component.html',
  styleUrls: ['./registro-list.component.css']
})
export class RegistroListComponent implements OnInit {
  displayedColumns: string[] = ['id','productoId','cantidad','tipo','fecha','actions'];
  dataSource = new MatTableDataSource<RegistroDTO>([]);
  allRegistros: RegistroDTO[] = [];

  // filtros
  tipoFiltro: string = 'TODOS';
  fechaDesde?: string;
  fechaHasta?: string;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('fromDate') fromDateInput!: ElementRef<HTMLInputElement>;
  @ViewChild('toDate') toDateInput!: ElementRef<HTMLInputElement>;

  constructor(private servicio: RegistroService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.servicio.getAll().subscribe({
      next: data => {
        this.allRegistros = data;
        this.aplicarFiltros();
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: err => console.error(err)
    });
  }

  aplicarFiltros() {
    let filtered = this.allRegistros.slice();

    // filtro por tipo
    if (this.tipoFiltro && this.tipoFiltro !== 'TODOS') {
      filtered = filtered.filter(r => {
        const tipo = (r.tipo || '').toUpperCase();
        const filtro = this.tipoFiltro.toUpperCase();
        // ENTRADA
        if (filtro === 'ENTRADA') {
          return tipo === 'ENTRADA' || tipo === 'INGRESO';
        }
        return tipo === filtro;
      });
    }

    // filtro por fecha
    const desde = this.fechaDesde ? new Date(this.fechaDesde) : undefined;
    const hasta = this.fechaHasta ? new Date(this.fechaHasta) : undefined;
    if (desde) {
      desde.setHours(0,0,0,0);
    }
    if (hasta) {
      hasta.setHours(23,59,59,999);
    }
    if (desde || hasta) {
      filtered = filtered.filter(r => {
        if (!r.fecha) return false;
        const f = new Date(r.fecha);
        if (isNaN(f.getTime())) return false;
        if (desde && f < desde) return false;
        if (hasta && f > hasta) return false;
        return true;
      });
    }

    this.dataSource.data = filtered;
    if (this.paginator) this.dataSource.paginator = this.paginator;
    if (this.sort) this.dataSource.sort = this.sort;
  }

  cambiarTipo(tipo: string) {
    this.tipoFiltro = tipo || 'TODOS';
    this.aplicarFiltros();
  }

  cambiarFechaDesde(value?: Date) {
    if (value) {
      this.fechaDesde = this.formatDateISO(value);
    } else {
      this.fechaDesde = undefined;
    }
    this.aplicarFiltros();
  }

  cambiarFechaHasta(value?: Date) {
    if (value) {
      this.fechaHasta = this.formatDateISO(value);
    } else {
      this.fechaHasta = undefined;
    }
    this.aplicarFiltros();
  }

  private formatDateISO(d: Date): string {
    if (!d) return '';
    return new Date(d.getFullYear(), d.getMonth(), d.getDate()).toISOString().split('T')[0];
  }

  limpiarFiltros() {
    this.tipoFiltro = 'TODOS';
    this.fechaDesde = undefined;
    this.fechaHasta = undefined;
    try { if (this.fromDateInput) this.fromDateInput.nativeElement.value = ''; } catch(e) {}
    try { if (this.toDateInput) this.toDateInput.nativeElement.value = ''; } catch(e) {}
    this.aplicarFiltros();
  }

  openCreate() {
    const dialogRef = this.dialog.open(RegistroFormComponent, { width: '500px', data: { } });
    dialogRef.afterClosed().subscribe(res => { if (res) this.load(); });
  }

  edit(row: RegistroDTO) {
    const dialogRef = this.dialog.open(RegistroFormComponent, { width: '500px', data: { registro: row } });
    dialogRef.afterClosed().subscribe(res => { if (res) this.load(); });
  }

  view(id?: number) {
    if (!id) return;
    this.servicio.getById(id).subscribe({
      next: data => this.dialog.open(ViewDialogComponent, { width: '600px', data: { title: `registro ${id}`, entity: data } }),
      error: err => console.error(err)
    });
  }

  delete(id?: number) {
    if (!id) return;
    if (!confirm('Eliminar registro?')) return;
    this.servicio.delete(id).subscribe({ next: () => this.load(), error: err => console.error(err) });
  }
}
