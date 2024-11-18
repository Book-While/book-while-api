package com.bookwhile.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookDto {

    private UUID id;

    private String name;

    private String isbn;
}
