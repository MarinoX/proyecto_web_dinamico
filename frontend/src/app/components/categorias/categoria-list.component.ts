import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { CategoriaDTO } from '../../models/categoria.model';
import { CategoriaService } from '../../services/categoria.service';
import { CategoriaFormComponent } from './categoria-form.component';
import { ViewDialogComponent } from '../../shared/view-dialog.component';

@Component({
  selector: 'app-categoria-list',
  templateUrl: './categoria-list.component.html',
  styleUrls: ['./categoria-list.component.css']
})
export class CategoriaListComponent implements OnInit {
  displayedColumns: string[] = ['id','nombre','descripcion','actions'];
  dataSource = new MatTableDataSource<CategoriaDTO>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(private servicio: CategoriaService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.servicio.getAll().subscribe({
      next: data => {
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: err => console.error(err)
    });
  }

  openCreate() {
    const dialogRef = this.dialog.open(CategoriaFormComponent, { width: '400px', data: { }});
    dialogRef.afterClosed().subscribe(res => {
      if (res) this.load();
    });
  }

  edit(row: CategoriaDTO) {
    const dialogRef = this.dialog.open(CategoriaFormComponent, { width: '400px', data: { categoria: row }});
    dialogRef.afterClosed().subscribe(res => {
      if (res) this.load();
    });
  }

  view(id?: number) {
    if (!id) return;
    this.servicio.getById(id).subscribe({
      next: data => {
        this.dialog.open(ViewDialogComponent, { width: '500px', data: { title: `categoria ${id}`, entity: data }});
      },
      error: err => console.error(err)
    });
  }

  delete(id?: number) {
    if (!id) return;
    if (!confirm('Eliminar categoría?')) return;
    this.servicio.delete(id).subscribe({
      next: () => this.load(),
      error: err => console.error(err)
    });
  }
}
