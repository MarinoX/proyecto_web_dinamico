import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoriaDTO } from '../../models/categoria.model';
import { CategoriaService } from '../../services/categoria.service';

@Component({
  selector: 'app-categoria-form',
  templateUrl: './categoria-form.component.html'
})
export class CategoriaFormComponent implements OnInit {
  form!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private servicio: CategoriaService,
    private dialogRef: MatDialogRef<CategoriaFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { categoria?: CategoriaDTO }
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: [this.data.categoria?.nombre || '', Validators.required],
      descripcion: [this.data.categoria?.descripcion || '']
    });
  }

  save() {
    if (this.form.invalid) return;
    const payload: CategoriaDTO = this.form.value;
    if (this.data.categoria?.id) {
      this.servicio.update(this.data.categoria.id, payload).subscribe({
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
