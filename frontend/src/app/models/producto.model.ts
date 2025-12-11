import { CategoriaDTO } from './categoria.model';

export interface ProductoDTO {
  id?: number;
  nombre: string;
  descripcion?: string;
  precio?: number;
  stock?: number;
  categoria?: Partial<CategoriaDTO>;
}
