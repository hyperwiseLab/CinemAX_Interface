package com.cinemax.domain.syntax.mapper;

import com.cinemax.core.config.GlobalMapperConfig;
import com.cinemax.domain.syntax.dto.SyntaxDetailDto;
import com.cinemax.domain.syntax.entity.SyntaxDetail;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * SyntaxDetail 엔티티와 DTO 간 변환을 위한 Mapper
 */
@Mapper(config = GlobalMapperConfig.class)
public interface SyntaxDetailMapper {

    SyntaxDetailDto toDto(SyntaxDetail entity);

    List<SyntaxDetailDto> toDto(List<SyntaxDetail> entities);
}
