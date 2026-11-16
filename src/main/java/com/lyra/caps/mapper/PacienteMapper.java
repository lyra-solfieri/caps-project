package com.lyra.caps.mapper;

import com.lyra.caps.dto.PacienteDto;
import com.lyra.caps.dto.PacienteResponseDto;
import com.lyra.caps.model.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PacienteMapper {
    Paciente toEntity(PacienteDto dto);
    PacienteResponseDto toResponseDto(Paciente entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evolucoes", ignore = true)
    @Mapping(target = "atendimentos", ignore = true)
    @Mapping(target = "anamneses", ignore = true)
    @Mapping(target = "endereco.id", ignore = true)
    void updateEntityFromDto(PacienteDto dto, @MappingTarget Paciente entity);
}