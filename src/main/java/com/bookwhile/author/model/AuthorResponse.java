package com.bookwhile.author.model;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthorResponse {

    private UUID id;

    private String name;

    private String surname;
}
