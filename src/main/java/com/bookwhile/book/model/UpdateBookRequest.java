package com.bookwhile.book.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookRequest {

    private String name;

    private String isbn;
}
