package com.bookwhile.book.dto;

import com.bookwhile.author.dto.AuthorDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookResponseDto {

    private UUID id;

    private AuthorDto authorDto;

    private String name;

    private String isbn;
}
