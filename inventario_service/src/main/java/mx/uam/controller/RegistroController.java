package mx.uam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import mx.uam.model.dto.RegistroDTO;
import mx.uam.service.RegistroService;

@RestController
@RequestMapping("/registros")
@Tag(name = "Registro", description = "Operaciones CRUD relacionadas con los registros de stock de productos")
public class RegistroController {
    @Autowired
    private RegistroService registroService;

    @GetMapping
    @Operation(summary = "Obtener todos los registros", description = "Recupera una lista de todos los registros de stock")
    public List<RegistroDTO> findAll() {
        return registroService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro por ID", description = "Retorna un registro por su ID")
    public ResponseEntity<RegistroDTO> getById(@Parameter(description = "ID del registro") @PathVariable Integer id) {
        RegistroDTO dto = registroService.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener registros por producto", description = "Retorna todos los registros de un producto específico")
    public List<RegistroDTO> getByProductoId(@Parameter(description = "ID del producto") @PathVariable Integer productoId) {
        return registroService.findByProductoId(productoId);
    }

    @PostMapping
    @Operation(summary = "Crear registro", description = "Crea un nuevo registro de stock")
    public ResponseEntity<RegistroDTO> create(@RequestBody RegistroDTO dto) {
        RegistroDTO created = registroService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro", description = "Actualiza un registro existente")
    public ResponseEntity<RegistroDTO> update(@Parameter(description = "ID del registro") @PathVariable Integer id, @RequestBody RegistroDTO dto) {
        RegistroDTO updated = registroService.update(id, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro", description = "Elimina un registro por su ID")
    public ResponseEntity<Void> delete(@Parameter(description = "ID del registro") @PathVariable Integer id) {
        boolean deleted = registroService.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInsufficientStock(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
