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
import mx.uam.model.dto.CategoriaDTO;
import mx.uam.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
@Tag(name = "Categoría", description = "Operaciones CRUD relacionadas con las categorías de productos")
public class CategoriaController {
    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Recupera una lista de todas las categorías de productos disponibles en el inventario.")
    public List<CategoriaDTO> findAll() {
        return categoriaService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Retorna una categoría por su ID")
    public ResponseEntity<CategoriaDTO> getById(@Parameter(description = "ID de la categoría") @PathVariable Integer id) {
        CategoriaDTO dto = categoriaService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría")
    public ResponseEntity<CategoriaDTO> create(@RequestBody CategoriaDTO dto) {
        CategoriaDTO created = categoriaService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    public ResponseEntity<CategoriaDTO> update(@Parameter(description = "ID de la categoría") @PathVariable Integer id, @RequestBody CategoriaDTO dto) {
        CategoriaDTO updated = categoriaService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);        
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría por su ID")
    public ResponseEntity<Void> delete(@Parameter(description = "ID de la categoría") @PathVariable Integer id) {
        boolean deleted = categoriaService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    

}
