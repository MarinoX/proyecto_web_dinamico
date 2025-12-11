import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductoListComponent } from './components/productos/producto-list.component';
import { CategoriaListComponent } from './components/categorias/categoria-list.component';
import { RegistroListComponent } from './components/registros/registro-list.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'productos', component: ProductoListComponent },
  { path: 'categorias', component: CategoriaListComponent },
  { path: 'registros', component: RegistroListComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
