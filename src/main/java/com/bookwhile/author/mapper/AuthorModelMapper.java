package com.bookwhile.author.mapper;

import com.bookwhile.author.dto.AuthorRequestDto;
import com.bookwhile.author.dto.AuthorResponseDto;
import com.bookwhile.author.model.AuthorRequest;
import com.bookwhile.author.model.AuthorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorModelMapper {

    List<AuthorResponse> toAuthorList(List<AuthorResponseDto> authorResponseDtoList);

    @Mapping(target = "surname", ignore = true)
    AuthorResponse toAuthorResponse(AuthorResponseDto authorResponseDto);

    AuthorRequestDto toAuthorRequestDto(AuthorRequest authorRequest);
}
