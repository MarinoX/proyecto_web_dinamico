package mx.uam.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.model.dto.CategoriaDTO;
import mx.uam.model.dto.ProductoDTO;
import mx.uam.model.entity.Categoria;
import mx.uam.model.entity.Producto;
import mx.uam.repository.CategoriaRepository;
import mx.uam.repository.ProductoRepository;
import mx.uam.repository.RegistroRepository;
    
@Service
public class ProductService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private RegistroRepository registroRepository;

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
        if (id == null || dto == null) {
            return null;
        }
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (!productoOpt.isPresent()) {
            return null;
        }
        Producto producto = productoOpt.get();
        
        // Solo actualiza campos que NO son null (update parcial)
        if (dto.getNombre() != null) {
            producto.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            producto.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrecio() != null) {
            producto.setPrecio(dto.getPrecio());
        }
        // Actualizar categoría solo si se proporciona
        if (dto.getCategoria() != null && dto.getCategoria().getId() != null) {
            Optional<Categoria> categoriaOpt = categoriaRepository.findById(dto.getCategoria().getId());
            if (categoriaOpt.isPresent()) {
                producto.setCategoria(categoriaOpt.get());
            }
        }
        // NUNCA actualizar stock directamente - solo a través de registros de entrada/salida
        
        Producto updated = productoRepository.save(producto);
        return toDTO(updated);
    }

    @Transactional
    public boolean delete(Integer id) {
        if (id == null || !productoRepository.existsById(id)) {
            return false;
        }
        // Eliminar todos los registros de stock asociados al producto
        registroRepository.findByProductoId(id).forEach(registro -> registroRepository.delete(registro));
        // Luego eliminar el producto
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
