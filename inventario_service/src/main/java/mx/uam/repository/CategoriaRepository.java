package mx.uam.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.uam.model.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

}
