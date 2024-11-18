package com.bookwhile.author.model;


import com.bookwhile.book.dto.BookDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthorResponse {

    private UUID id;

    private String name;

    private String surname;

    private List<BookDto> bookDtoList;
}
