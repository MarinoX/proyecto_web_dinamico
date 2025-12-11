import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { ProductoDTO } from '../../models/producto.model';
import { ProductoService } from '../../services/producto.service';
import { CategoriaDTO } from '../../models/categoria.model';
import { CategoriaService } from '../../services/categoria.service';
import { ProductoFormComponent } from './producto-form.component';
import { ViewDialogComponent } from '../../shared/view-dialog.component';
import { RegistroFormComponent } from '../registros/registro-form.component';

@Component({
  selector: 'app-producto-list',
  templateUrl: './producto-list.component.html',
  styleUrls: ['./producto-list.component.css'] 
})
export class ProductoListComponent implements OnInit {
  displayedColumns: string[] = ['id','nombre','descripcion','precio','stock','categoria','actions'];
  dataSource = new MatTableDataSource<ProductoDTO>([]);
  categorias: CategoriaDTO[] = [];
  selectedCategoriaId: number | null = null;
  private allProducts: ProductoDTO[] = [];
 
  @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;
  @ViewChild('minPriceInput') minPriceInput!: ElementRef<HTMLInputElement>;
  @ViewChild('maxPriceInput') maxPriceInput!: ElementRef<HTMLInputElement>;
  @ViewChild('minStockInput') minStockInput!: ElementRef<HTMLInputElement>;
  
  searchTerm: string = '';
  minPrice: number | null = null;
  maxPrice: number | null = null;
  minStock: number | null = null;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private servicio: ProductoService,
    private categoriaService: CategoriaService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.load();
    this.categoriaService.getAll().subscribe({
      next: data => this.categorias = data,
      error: err => console.error(err)
    });
  }

  load() {
    this.servicio.getAll().subscribe({
      next: data => {
        this.allProducts = data;
        this.aplicarFiltros();
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
      },
      error: err => console.error(err)
    });
  }

  aplicarFiltros() {
    let filtered = this.allProducts;

    if (this.selectedCategoriaId != null) {
      filtered = filtered.filter(p => p.categoria?.id === this.selectedCategoriaId);
    }

    if (this.searchTerm && this.searchTerm.trim() !== '') {
      const q = this.searchTerm.trim().toLowerCase();
      filtered = filtered.filter(p => p.nombre.toLowerCase().includes(q));
    }

    if (this.minPrice != null) {
      filtered = filtered.filter(p => (p.precio ?? 0) >= this.minPrice!);
    }
    if (this.maxPrice != null) {
      filtered = filtered.filter(p => (p.precio ?? 0) <= this.maxPrice!);
    }

    if (this.minStock != null) {
      filtered = filtered.filter(p => (p.stock ?? 0) >= this.minStock!);
    }

    this.dataSource.data = filtered;
    if (this.dataSource.paginator) this.dataSource.paginator.firstPage();
  }
  buscarPorNombre(value: string) {
    this.searchTerm = value;
    this.aplicarFiltros();
  }

  cambiarPrecioMin(value: string) {
    this.minPrice = value === '' ? null : Number(value);
    this.aplicarFiltros();
  }

  cambiarPrecioMax(value: string) {
    this.maxPrice = value === '' ? null : Number(value);
    this.aplicarFiltros();
  }

  cambiarStockMin(value: string) {
    this.minStock = value === '' ? null : Number(value);
    this.aplicarFiltros();
  }

  limpiarFiltros() {
    this.selectedCategoriaId = null;
    this.searchTerm = '';
    this.minPrice = null;
    this.maxPrice = null;
    this.minStock = null;
    
    try {
      if (this.searchInput) this.searchInput.nativeElement.value = '';
      if (this.minPriceInput) this.minPriceInput.nativeElement.value = '';
      if (this.maxPriceInput) this.maxPriceInput.nativeElement.value = '';
      if (this.minStockInput) this.minStockInput.nativeElement.value = '';
    } catch (e) {
      
    }
    this.aplicarFiltros();
  }

  cambiarCategoria(value: any) {
    this.selectedCategoriaId = value == null ? null : Number(value);
    this.aplicarFiltros();
  }

  openCreate() {
    const dialogRef = this.dialog.open(ProductoFormComponent, { width: '400px', data: { }});
    dialogRef.afterClosed().subscribe(res => {
      if (res) this.load();
    });
  }

  edit(row: ProductoDTO) {
    const dialogRef = this.dialog.open(ProductoFormComponent, { width: '400px', data: { producto: row }});
    dialogRef.afterClosed().subscribe(res => {
      if (res) this.load();
    });
  }

  view(id?: number) {
    if (!id) return;
    this.servicio.getById(id).subscribe({
      next: data => {
        this.dialog.open(ViewDialogComponent, { width: '500px', data: { title: `producto ${id}`, entity: data }});
      },
      error: err => console.error(err)
    });
  }

  delete(id?: number) {
    if (!id) return;
    if (!confirm('Eliminar producto?')) return;
    this.servicio.delete(id).subscribe({
      next: () => this.load(),
      error: err => console.error(err)
    });
  }

  abrirRegistroStock(producto: ProductoDTO) {
    if (!producto.id) return;
    const dialogRef = this.dialog.open(RegistroFormComponent, { 
      width: '400px', 
      data: { productoId: producto.id, productoNombre: producto.nombre }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        // Recargar productos para actualizar stock en tiempo real
        this.load();
      }
    });
  }
}
