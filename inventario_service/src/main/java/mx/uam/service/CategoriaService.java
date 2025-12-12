package mx.uam.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.model.dto.CategoriaDTO;
import mx.uam.model.entity.Categoria;
import mx.uam.repository.CategoriaRepository;
import mx.uam.repository.ProductoRepository;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriarepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<CategoriaDTO> findAll(){
        return categoriarepository.findAll().stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
    }

    public CategoriaDTO findById(Integer id) {
        if (id == null) {
            return null;
        }
        Optional<Categoria> categoria = categoriarepository.findById(id);
        return categoria.map(this::toDTO).orElse(null);
    }

    public CategoriaDTO create(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        Categoria saved = categoriarepository.save(categoria);
        return toDTO(saved);
    }

    public CategoriaDTO update(Integer id, CategoriaDTO dto) {
        if (id == null || dto == null) {
            return null;
        }
        Optional<Categoria> categoriaOpt = categoriarepository.findById(id);
        if (!categoriaOpt.isPresent()) {
            return null;
        }
        Categoria categoria = categoriaOpt.get();
        // Solo actualiza campos que NO son null (update parcial)
        if (dto.getNombre() != null) {
            categoria.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            categoria.setDescripcion(dto.getDescripcion());
        }
        Categoria updated = categoriarepository.save(categoria);
        return toDTO(updated);
    }


    @Transactional
    public boolean delete(Integer id) {
        if (id == null || !categoriarepository.existsById(id)) {
            return false;
        }
        // Validar que no hay productos asociados
        long productosCount = productoRepository.findAll().stream()
            .filter(p -> p.getCategoria() != null && p.getCategoria().getId().equals(id))
            .count();
        
        if (productosCount > 0) {
            throw new IllegalArgumentException(
                "No se puede eliminar la categoría porque tiene " + productosCount + " producto(s) asociado(s)"
            );
        }
        
        categoriarepository.deleteById(id);
        return true;
    }

    private CategoriaDTO toDTO(Categoria categoria){
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

}
