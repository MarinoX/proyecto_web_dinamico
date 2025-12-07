package mx.uam.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.uam.model.entity.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Integer> {
    List<Registro> findByProductoId(Integer productoId);
}
