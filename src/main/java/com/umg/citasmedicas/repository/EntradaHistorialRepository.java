package com.umg.citasmedicas.repository;

import com.umg.citasmedicas.model.EntradaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaHistorialRepository extends JpaRepository<EntradaHistorial, Integer> {
    List<EntradaHistorial> findByHistorialIdHistorial(Integer idHistorial);
}
