package com.lyra.caps.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lyra.caps.model.Paciente;
import com.lyra.caps.service.PacienteService;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/paciente")
public class PacienteController {

    private PacienteService pacienteService;

    // @PostMapping
    // public ResponseEntity<Paciente> criarPaciente(@RequestBody Paciente pacienteDto) {
    //     Paciente  novoPaciente = pacienteService.criarPaciente(pacienteDto);
    //     return ResponseEntity.created(URI.create("/api/paciente/" + pacienteDto.getId())).body(pacienteDto);
    // }


    // @GetMapping("/{id}")
    // public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
    //     return ResponseEntity.ok(pacienteService.buscarPorId(id));
    // }
    

}
