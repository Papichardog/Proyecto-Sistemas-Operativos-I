package com.umg.citasmedicas.repository;

import com.umg.citasmedicas.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Repositorio para gestionar el acceso a datos de los administradores.
// Extiende JpaRepository para heredar las operaciones CRUD básicas.
}
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
    // Busca un administrador por su correo (usado en autenticación).
    Optional<Administrador> findByCorreo(String correo);
}
