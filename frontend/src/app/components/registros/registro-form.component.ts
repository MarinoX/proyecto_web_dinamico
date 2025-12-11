import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RegistroDTO } from '../../models/registro.model';
import { RegistroService } from '../../services/registro.service';
import { ProductoService } from '../../services/producto.service';
import { ProductoDTO } from '../../models/producto.model';

@Component({
  selector: 'app-registro-form',
  templateUrl: './registro-form.component.html'
})
export class RegistroFormComponent implements OnInit {
  form!: FormGroup;
  productos: ProductoDTO[] = [];
  errorMsg: string = '';

  constructor(
    private fb: FormBuilder,
    private servicio: RegistroService,
    private productoService: ProductoService,
    private dialogRef: MatDialogRef<RegistroFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { registro?: RegistroDTO; productoId?: number; productoNombre?: string }
  ) {}

  ngOnInit(): void {
    const productoIdInicial = this.data?.productoId ?? this.data?.registro?.productoId ?? null;
    const tipoInicial = this.data?.registro?.tipo || 'ENTRADA'; // por defecto ENTRADA desde productos
    
    this.form = this.fb.group({
      productoId: [productoIdInicial, Validators.required],
      cantidad: [this.data?.registro?.cantidad ?? 0, [Validators.required, Validators.min(1)]],
      tipo: [tipoInicial, Validators.required]
    });

    this.productoService.getAll().subscribe({ 
      next: data => this.productos = data, 
      error: err => console.error(err) 
    });
  }

  save() {
    if (this.form.invalid) return;
    this.errorMsg = '';
    const raw = this.form.getRawValue();
    
    // Si viene productoId desde productos, usarlo; si no, usar el del form
    const productoId = this.data?.productoId || raw.productoId;
    
    const payload: RegistroDTO = {
      productoId: Number(productoId),
      cantidad: Number(raw.cantidad),
      tipo: raw.tipo
    };

    if (this.data?.registro?.id) {
      this.servicio.update(this.data.registro.id, payload).subscribe({ 
        next: () => this.dialogRef.close(true), 
        error: (err) => {
          this.errorMsg = err.error || 'Error al actualizar el registro';
        }
      });
    } else {
      this.servicio.create(payload).subscribe({ 
        next: () => this.dialogRef.close(true), 
        error: (err) => {
          this.errorMsg = err.error || 'Error al crear el registro';
        }
      });
    }
  }

  close() { this.dialogRef.close(false); }
}
