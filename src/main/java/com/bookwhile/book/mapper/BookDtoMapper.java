package com.bookwhile.book.mapper;

import com.bookwhile.author.dto.AuthorDto;
import com.bookwhile.author.entity.AuthorEntity;
import com.bookwhile.book.dto.BookResponseDto;
import com.bookwhile.book.dto.CreateBookRequestDto;
import com.bookwhile.book.dto.UpdateBookRequestDto;
import com.bookwhile.book.entity.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookDtoMapper {

    @Mapping(target = "name", source = "bookRequestDto.name")
    @Mapping(target = "authorEntity", source = "authorEntity")
    BookEntity toBookEntity(CreateBookRequestDto bookRequestDto, AuthorEntity authorEntity);

    @Mapping(target = "id", source = "bookEntity.id")
    @Mapping(target = "name", source = "bookEntity.name")
    BookResponseDto toBookResponseDto(BookEntity bookEntity, AuthorDto authorDto);

    List<BookResponseDto> toBookResponseDtoList(List<BookEntity> bookList);

    BookEntity updateBookEntity(@MappingTarget BookEntity bookEntity, UpdateBookRequestDto bookRequestDto);

    AuthorDto toAuthorDto(AuthorEntity authorEntity);
}
