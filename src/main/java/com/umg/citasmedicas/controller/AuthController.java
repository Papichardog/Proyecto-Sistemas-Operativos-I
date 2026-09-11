package com.umg.citasmedicas.controller;

import com.umg.citasmedicas.dto.UsuarioAutenticadoResponse;
import com.umg.citasmedicas.model.Administrador;
import com.umg.citasmedicas.model.Medico;
import com.umg.citasmedicas.model.Paciente;
import com.umg.citasmedicas.repository.AdministradorRepository;
import com.umg.citasmedicas.repository.MedicoRepository;
import com.umg.citasmedicas.repository.PacienteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final AdministradorRepository administradorRepository;

    public AuthController(PacienteRepository pacienteRepository,
                          MedicoRepository medicoRepository,
                          AdministradorRepository administradorRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.administradorRepository = administradorRepository;
    }

    // GET /api/auth/me - quién está logueado y con qué rol.
    // No hace falta agregar ninguna regla nueva en SecurityConfig: como
    // no está en la lista de excepciones, ya cae bajo "anyRequest().authenticated()",
    // así que cualquier rol autenticado puede consultar su propio perfil.
    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> quienSoy(Authentication authentication) {
        // Spring Security ya validó las credenciales antes de que este método
        // se ejecute; si llegamos hasta acá, "authentication" es de alguien real.
        String correo = authentication.getName();

        boolean esAdministrador = authentication.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_ADMINISTRADOR"));
        boolean esMedico = authentication.getAuthorities().stream()
                .anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_MEDICO"));

        if (esAdministrador) {
            Administrador administrador = administradorRepository.findByCorreo(correo).orElseThrow();
            return ResponseEntity.ok(new UsuarioAutenticadoResponse(
                    administrador.getIdPersona(),
                    administrador.getNombre(),
                    administrador.getApellido(),
                    administrador.getCorreo(),
                    "ADMINISTRADOR"));
        }

        if (esMedico) {
            Medico medico = medicoRepository.findByCorreo(correo).orElseThrow();
            return ResponseEntity.ok(new UsuarioAutenticadoResponse(
                    medico.getIdPersona(),
                    medico.getNombre(),
                    medico.getApellido(),
                    medico.getCorreo(),
                    "MEDICO"));
        }

        Paciente paciente = pacienteRepository.findByCorreo(correo).orElseThrow();
        return ResponseEntity.ok(new UsuarioAutenticadoResponse(
                paciente.getIdPersona(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getCorreo(),
                "PACIENTE"));
    }
}
