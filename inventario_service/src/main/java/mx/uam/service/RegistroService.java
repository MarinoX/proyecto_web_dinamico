package mx.uam.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mx.uam.model.dto.RegistroDTO;
import mx.uam.model.entity.Producto;
import mx.uam.model.entity.Registro;
import mx.uam.repository.ProductoRepository;
import mx.uam.repository.RegistroRepository;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<RegistroDTO> findAll() {
        return registroRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RegistroDTO findById(Integer id) {
        return registroRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public List<RegistroDTO> findByProductoId(Integer productoId) {
        return registroRepository.findByProductoId(productoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RegistroDTO create(RegistroDTO dto) {
        return saveAndAdjustStock(null, dto);
    }

    public RegistroDTO update(Integer id, RegistroDTO dto) {
        return registroRepository.findById(id)
                .map(existing -> saveAndAdjustStock(existing, dto))
                .orElse(null);
    }

    public boolean delete(Integer id) {
        return registroRepository.findById(id).map(registro -> {
            // revert stock effect
            Producto producto = registro.getProducto();
            if (producto != null) {
                int efecto = effectForTipo(registro.getTipo(), registro.getCantidad());
                // revert -> subtract efecto
                producto.setStock(safeStock(producto.getStock() - efecto));
                productoRepository.save(producto);
            }
            registroRepository.delete(registro);
            return true;
        }).orElse(false);
    }

    @Transactional
    protected RegistroDTO saveAndAdjustStock(Registro existing, RegistroDTO dto) {
        // existing == null -> create; otherwise update existing
        Integer newProductoId = dto.getProductoId();
        Integer newCantidad = dto.getCantidad() != null ? dto.getCantidad() : Integer.valueOf(0);
        String newTipo = dto.getTipo();

        Producto newProducto = null;
        if (newProductoId != null) {
            newProducto = productoRepository.findById(newProductoId).orElse(null);
        }

        // revert existing effect if present
        if (existing != null) {
            Producto oldProducto = existing.getProducto();
            if (oldProducto != null) {
                int oldEfecto = effectForTipo(existing.getTipo(), existing.getCantidad());
                oldProducto.setStock(safeStock(oldProducto.getStock() - oldEfecto));
                productoRepository.save(oldProducto);
            }
            // update fields
            existing.setCantidad(newCantidad);
            existing.setTipo(newTipo);
            existing.setFecha(LocalDateTime.now());
            existing.setProducto(newProducto);

            Registro saved = registroRepository.save(existing);

            // apply new effect
            if (newProducto != null) {
                int newEfecto = effectForTipo(newTipo, newCantidad);
                newProducto.setStock(safeStock(newProducto.getStock() + newEfecto));
                productoRepository.save(newProducto);
            }
            return toDTO(saved);
        }

        // create new registro
        Registro registro = new Registro();
        registro.setCantidad(newCantidad);
        registro.setTipo(newTipo);
        registro.setFecha(LocalDateTime.now());
        registro.setProducto(newProducto);

        Registro saved = registroRepository.save(registro);

        if (newProducto != null) {
            int newEfecto = effectForTipo(newTipo, newCantidad);
            newProducto.setStock(safeStock(newProducto.getStock() + newEfecto));
            productoRepository.save(newProducto);
        }

        return toDTO(saved);
    }

    private int effectForTipo(String tipo, Integer cantidad) {
        if (tipo == null) return 0;
        int qty = cantidad != null ? cantidad : 0;
        String t = tipo.trim().toLowerCase();
        if (t.equals("entrada") || t.equals("in") || t.equals("ingreso")) return qty;
        if (t.equals("salida") || t.equals("out") || t.equals("egreso")) return -qty;
        // default: treat unknown as 0
        return 0;
    }

    private Integer safeStock(Integer stock) {
        if (stock == null) return 0;
        return Math.max(stock, 0);
    }

    private RegistroDTO toDTO(Registro registro) {
        RegistroDTO dto = new RegistroDTO();
        dto.setId(registro.getId());
        dto.setCantidad(registro.getCantidad());
        dto.setTipo(registro.getTipo());
        dto.setFecha(registro.getFecha());

        if (registro.getProducto() != null) {
            dto.setProductoId(registro.getProducto().getId());
        }

        return dto;
    }
}
