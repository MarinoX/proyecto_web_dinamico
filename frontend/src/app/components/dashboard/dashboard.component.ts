import { Component, OnInit } from '@angular/core';
import { ProductoService } from '../../services/producto.service';
import { CategoriaService } from '../../services/categoria.service';
import { RegistroService } from '../../services/registro.service';
import { ProductoDTO } from '../../models/producto.model';
import { CategoriaDTO } from '../../models/categoria.model';
import { RegistroDTO } from '../../models/registro.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  totalProductos: number = 0;
  totalCategorias: number = 0;
  totalRegistros: number = 0;
  productosStockBajo: ProductoDTO[] = [];
  registrosHoy: RegistroDTO[] = [];
  ingresosDia: number = 0;
  salidasDia: number = 0;

  loading: boolean = true;
  errorMsg: string = '';

  constructor(
    private productoSvc: ProductoService,
    private categoriaSvc: CategoriaService,
    private registroSvc: RegistroService
  ) {}

  ngOnInit(): void {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    this.loading = true;
    this.errorMsg = '';

    Promise.all([
      this.productoSvc.getAll().toPromise(),
      this.categoriaSvc.getAll().toPromise(),
      this.registroSvc.getAll().toPromise()
    ])
      .then(([productos, categorias, registros]) => {
        this.totalProductos = (productos || []).length;
        this.totalCategorias = (categorias || []).length;
        this.totalRegistros = (registros || []).length;

        // Productos con stock bajo 
        this.productosStockBajo = (productos || []).filter(p => (p.stock || 0) < 10);

        // Registros de hoy
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        const manana = new Date(hoy);
        manana.setDate(manana.getDate() + 1);

        this.registrosHoy = (registros || []).filter(r => {
          if (!r.fecha) return false;
          const f = new Date(r.fecha);
          return f >= hoy && f < manana;
        });

        // Contar ingresos y salidas de hoy
        this.ingresosDia = this.registrosHoy.filter(r => r.tipo === 'INGRESO').reduce((sum, r) => sum + (r.cantidad || 0), 0);
        this.salidasDia = this.registrosHoy.filter(r => r.tipo === 'SALIDA').reduce((sum, r) => sum + (r.cantidad || 0), 0);

        this.loading = false;
      })
      .catch(err => {
        console.error('Error cargando estadísticas:', err);
        this.errorMsg = 'Error al cargar estadísticas. Intenta de nuevo.';
        this.loading = false;
      });
  }

  recargar() {
    this.cargarEstadisticas();
  }
}
