package com.bookwhile.author.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthorResponseDto {

    private UUID id;

    private String name;

    private String surname;
}
