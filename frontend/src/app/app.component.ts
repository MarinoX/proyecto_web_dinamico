import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <mat-toolbar class="app-toolbar" color="primary">
      <div class="toolbar-content">
        <div class="title">INVENTARIO</div>
        <div class="toolbar-actions-box">
          <nav class="toolbar-actions">
            <button mat-flat-button routerLink="/dashboard">Dashboard</button>
            <button mat-flat-button routerLink="/productos">Productos</button>
            <button mat-flat-button routerLink="/categorias">Categorías</button>
            <button mat-flat-button routerLink="/registros">Registros</button>
          </nav>
        </div>
      </div>
    </mat-toolbar>

    <div class="container">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {
}
