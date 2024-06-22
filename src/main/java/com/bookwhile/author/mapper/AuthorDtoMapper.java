package com.bookwhile.author.mapper;

import com.bookwhile.author.dto.AuthorRequestDto;
import com.bookwhile.author.dto.AuthorResponseDto;
import com.bookwhile.author.entity.AuthorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorDtoMapper {

    List<AuthorResponseDto> toAuthorDtoList(List<AuthorEntity> authorEntityList);

    AuthorResponseDto toAuthorDto(AuthorEntity authorEntity);

    AuthorEntity toAuthorEntity(AuthorRequestDto authorRequestDto);

    void updateAuthorEntity(@MappingTarget AuthorEntity authorEntity, AuthorRequestDto authorRequestDto);
}
