package com.cinemax.domain.syntax.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.syntax.dto.SyntaxIdDto;
import com.cinemax.domain.syntax.entity.Syntax;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Syntax 엔티티와 SyntaxIdDto 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class, uses = SyntaxDetailMapper.class)
public interface SyntaxMapper {

    @Mapping(source = "syntaxDetails", target = "syntaxDetail")
    SyntaxIdDto toSyntaxIdDto(Syntax syntax);

    List<SyntaxIdDto> toSyntaxIdDto(List<Syntax> syntaxes);
}
