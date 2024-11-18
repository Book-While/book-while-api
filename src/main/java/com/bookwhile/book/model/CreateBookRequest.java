package com.bookwhile.book.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBookRequest {

    @NotBlank
    private UUID authorId;

    @Size(max = 255)
    @NotBlank
    private String name;

    @Size(max = 17)
    @NotBlank
    private String isbn;
}
