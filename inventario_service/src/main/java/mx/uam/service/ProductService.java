package mx.uam.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.uam.model.dto.CategoriaDTO;
import mx.uam.model.dto.ProductoDTO;
import mx.uam.model.entity.Categoria;
import mx.uam.model.entity.Producto;
import mx.uam.repository.CategoriaRepository;
import mx.uam.repository.ProductoRepository;
    
@Service
public class ProductService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO findById(Integer id) {
        if (id == null) {
            return null;
        }
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(this::toDTO).orElse(null);
    }


    public ProductoDTO create(ProductoDTO dto) {
        if (dto == null || dto.getCategoria() == null || dto.getCategoria().getId() == null) {
            return null;
        }
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoria().getId());
        if (!categoriaOpt.isPresent()) {
            return null;
        }
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoriaOpt.get());
        Producto saved = productoRepository.save(producto);
        return toDTO(saved);
    }

    public ProductoDTO update(Integer id, ProductoDTO dto) {
        if (id == null || dto == null || dto.getCategoria() == null || dto.getCategoria().getId() == null) {
            return null;
        }
        Optional<Producto> productoOpt = productoRepository.findById(id);
        Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoria().getId());
        if (!productoOpt.isPresent() || !categoriaOpt.isPresent()) {
            return null;
        }
        Producto producto = productoOpt.get();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoriaOpt.get());
        Producto updated = productoRepository.save(producto);
        return toDTO(updated);
    }

    public boolean delete(Integer id) {
        if (id == null || !productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }


    private ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        if (producto.getCategoria() != null) {
            CategoriaDTO categoriaDTO = new CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setNombre(producto.getCategoria().getNombre());
            categoriaDTO.setDescripcion(producto.getCategoria().getDescripcion());
            dto.setCategoria(categoriaDTO);
        }
        return dto;
    }

}
