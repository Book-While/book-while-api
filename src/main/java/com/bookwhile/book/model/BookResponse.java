package com.bookwhile.book.model;

import com.bookwhile.author.dto.AuthorDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookResponse {

    private UUID id;

    private AuthorDto authorDto;

    private String name;

    private String isbn;
}
