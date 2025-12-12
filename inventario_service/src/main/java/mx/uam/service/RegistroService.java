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
                .map(existing -> updatePartial(existing, dto))
                .orElse(null);
    }
    
    @Transactional
    protected RegistroDTO updatePartial(Registro existing, RegistroDTO dto) {
        // Solo actualiza campos que NO son null 
        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId()).orElse(null);
            existing.setProducto(producto);
        }
        if (dto.getCantidad() != null) {
            existing.setCantidad(dto.getCantidad());
        }
        if (dto.getTipo() != null) {
            existing.setTipo(dto.getTipo());
        }

        Registro updated = registroRepository.save(existing);
        return toDTO(updated);
    }

    public boolean delete(Integer id) {
        return registroRepository.findById(id).map(registro -> {
            
            Producto producto = registro.getProducto();
            if (producto != null) {
                int efecto = effectForTipo(registro.getTipo(), registro.getCantidad());
                
                producto.setStock(safeStock(producto.getStock() - efecto));
                productoRepository.save(producto);
            }
            registroRepository.delete(registro);
            return true;
        }).orElse(false);
    }

    @Transactional
    protected RegistroDTO saveAndAdjustStock(Registro existing, RegistroDTO dto) {
        
        Integer newProductoId = dto.getProductoId();
        Integer newCantidad = dto.getCantidad() != null ? dto.getCantidad() : 0;
        String newTipo = dto.getTipo();

        Producto newProducto = null;
        if (newProductoId != null) {
            newProducto = productoRepository.findById(newProductoId).orElse(null);
        }

        // Validar stock suficiente para salidas
        if (newProducto != null && isSalida(newTipo)) {
            int stockActual = newProducto.getStock() != null ? newProducto.getStock() : 0;
            if (stockActual < newCantidad) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto '" + newProducto.getNombre() + "': " +
                    "disponible " + stockActual + ", solicitado " + newCantidad
                );
            }
        }

        if (existing != null) {
            Producto oldProducto = existing.getProducto();
            if (oldProducto != null) {
                int oldEfecto = effectForTipo(existing.getTipo(), existing.getCantidad());
                oldProducto.setStock(safeStock(oldProducto.getStock() - oldEfecto));
                productoRepository.save(oldProducto);
            }
           
            existing.setCantidad(newCantidad);
            existing.setTipo(newTipo);
            existing.setFecha(LocalDateTime.now());
            existing.setProducto(newProducto);

            Registro saved = registroRepository.save(existing);

          
            if (newProducto != null) {
                int newEfecto = effectForTipo(newTipo, newCantidad);
                newProducto.setStock(safeStock(newProducto.getStock() + newEfecto));
                productoRepository.save(newProducto);
            }
            return toDTO(saved);
        }

       
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
        return 0;
    }

    private boolean isSalida(String tipo) {
        if (tipo == null) return false;
        String t = tipo.trim().toLowerCase();
        return t.equals("salida") || t.equals("out") || t.equals("egreso");
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
