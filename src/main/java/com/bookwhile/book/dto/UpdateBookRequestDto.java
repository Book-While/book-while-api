package com.bookwhile.book.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequestDto {

    private String name;

    private String isbn;
}
