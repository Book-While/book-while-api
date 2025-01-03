package com.bookwhile.author.dto;


import com.bookwhile.book.dto.BookDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AuthorResponseDto {

    private UUID id;

    private String name;

    private String surname;

    private List<BookDto> bookDtoList;
}
