package com.lyra.caps.mapper;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.model.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    Paciente toEntity(PacienteDto dto);
    PacienteResponseDto toResponseDto(Paciente entity);
    void updateEntityFromDto(PacienteDto dto, @MappingTarget Paciente entity);
}