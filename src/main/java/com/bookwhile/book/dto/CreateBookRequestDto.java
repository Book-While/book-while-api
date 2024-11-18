package com.bookwhile.book.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBookRequestDto {

    private UUID authorId;

    private String name;

    private String isbn;
}
