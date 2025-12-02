package mx.uam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uam.model.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

}
