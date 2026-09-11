package com.umg.citasmedicas.dto;

// DTO = Data Transfer Object: un "molde" de respuesta hecho a medida
// para el frontend, en vez de devolver la entidad completa (que traería
// el hash de la contraseña, listas de citas, etc. — cosas que acá no
// hacen falta y que además no queremos exponer).
public record UsuarioAutenticadoResponse(
        Integer id,
        String nombre,
        String apellido,
        String correo,
        String rol
) {
}
