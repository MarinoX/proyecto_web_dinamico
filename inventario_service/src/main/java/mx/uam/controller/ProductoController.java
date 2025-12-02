package mx.uam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.uam.model.dto.ProductoDTO;
import mx.uam.service.ProductService;

@RestController
@RequestMapping("/productos")
@Tag(name = "Producto", description = "Operaciones CRUD relacionadas con los productos")
public class ProductoController {
    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Recupera una lista de todos los productos disponibles en el inventario.")
    public List<ProductoDTO> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto por su ID")
    public ResponseEntity<ProductoDTO> getById(@Parameter(description = "ID del producto") @PathVariable Integer id) {
        ProductoDTO dto = productService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto")
    public ResponseEntity<ProductoDTO> create(@RequestBody ProductoDTO dto) {
        ProductoDTO created = productService.create(dto);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente")
    public ResponseEntity<ProductoDTO> update(@Parameter(description = "ID del producto") @PathVariable Integer id, @RequestBody ProductoDTO dto) {
        ProductoDTO updated = productService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del producto") @PathVariable Integer id) {
        boolean deleted = productService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
