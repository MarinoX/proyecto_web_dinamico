import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ProductoDTO } from '../../models/producto.model';
import { ProductoService } from '../../services/producto.service';
import { CategoriaService } from '../../services/categoria.service';
import { CategoriaDTO } from '../../models/categoria.model';

@Component({
  selector: 'app-producto-form',
  templateUrl: './producto-form.component.html'
})
export class ProductoFormComponent implements OnInit {
  form!: FormGroup;
  categorias: CategoriaDTO[] = [];

  constructor(
    private fb: FormBuilder,
    private servicio: ProductoService,
    private categoriaService: CategoriaService,
    private dialogRef: MatDialogRef<ProductoFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { producto?: ProductoDTO }
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: [this.data.producto?.nombre || '', Validators.required],
      descripcion: [this.data.producto?.descripcion || ''],
      precio: [this.data.producto?.precio ?? null, [Validators.min(0)]],
      stock: [this.data.producto?.stock ?? null, [Validators.min(0)]],
      categoriaId: [this.data.producto?.categoria?.id ?? null]
    });

    this.categoriaService.getAll().subscribe({
      next: data => this.categorias = data,
      error: err => console.error(err)
    });
  }

  save() {
    if (this.form.invalid) return;
    const raw = this.form.value;
    const payload: ProductoDTO = {
      nombre: raw.nombre,
      descripcion: raw.descripcion || undefined,
      precio: raw.precio !== null && raw.precio !== '' ? Number(raw.precio) : undefined,
      stock: raw.stock !== null && raw.stock !== '' ? Number(raw.stock) : undefined,
      categoria: raw.categoriaId != null ? { id: Number(raw.categoriaId) } : undefined
    };
    if (this.data.producto?.id) {
      this.servicio.update(this.data.producto.id, payload).subscribe({
        next: res => this.dialogRef.close(true),
        error: err => console.error(err)
      });
    } else {
      this.servicio.create(payload).subscribe({
        next: res => this.dialogRef.close(true),
        error: err => console.error(err)
      });
    }
  }

  close() {
    this.dialogRef.close(false);
  }
}
