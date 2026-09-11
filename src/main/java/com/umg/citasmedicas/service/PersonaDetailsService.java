package com.umg.citasmedicas.service;

import com.umg.citasmedicas.repository.AdministradorRepository;
import com.umg.citasmedicas.repository.MedicoRepository;
import com.umg.citasmedicas.repository.PacienteRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaDetailsService implements UserDetailsService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final AdministradorRepository administradorRepository;

    public PersonaDetailsService(PacienteRepository pacienteRepository,
                                 MedicoRepository medicoRepository,
                                 AdministradorRepository administradorRepository) {
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        // Spring Security llama a esto con lo que el usuario mandó como
        // "username" en Basic Auth; nosotros usamos el correo. Como la
        // jerarquía de Persona está repartida en 3 tablas (estrategia
        // JOINED), probamos una por una hasta encontrarlo.

        Optional<com.umg.citasmedicas.model.Administrador> administrador = administradorRepository.findByCorreo(correo);
        if (administrador.isPresent()) {
            return construirUserDetails(administrador.get().getCorreo(), administrador.get().getPassword(), "ADMINISTRADOR");
        }

        Optional<com.umg.citasmedicas.model.Medico> medico = medicoRepository.findByCorreo(correo);
        if (medico.isPresent()) {
            return construirUserDetails(medico.get().getCorreo(), medico.get().getPassword(), "MEDICO");
        }

        Optional<com.umg.citasmedicas.model.Paciente> paciente = pacienteRepository.findByCorreo(correo);
        if (paciente.isPresent()) {
            return construirUserDetails(paciente.get().getCorreo(), paciente.get().getPassword(), "PACIENTE");
        }

        throw new UsernameNotFoundException("No existe un usuario con el correo: " + correo);
    }

    private UserDetails construirUserDetails(String correo, String passwordEncriptada, String rol) {
        return User.builder()
                .username(correo)
                .password(passwordEncriptada)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + rol)))
                .build();
    }
}
