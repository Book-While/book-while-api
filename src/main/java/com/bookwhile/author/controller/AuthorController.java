package com.bookwhile.author.controller;

import com.bookwhile.author.dto.AuthorRequestDto;
import com.bookwhile.author.dto.AuthorResponseDto;
import com.bookwhile.author.mapper.AuthorModelMapper;
import com.bookwhile.author.model.AuthorRequest;
import com.bookwhile.author.model.AuthorResponse;
import com.bookwhile.author.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    private final AuthorModelMapper authorModelMapper;

    @GetMapping("/all")
    public ResponseEntity<List<AuthorResponse>> getAllAuthors() {

        List<AuthorResponseDto> authorResponseDtoList = authorService.getAllAuthors();

        List<AuthorResponse> authorResponseList = authorModelMapper.toAuthorList(authorResponseDtoList);

        return ResponseEntity.ok(authorResponseList);
    }

    @GetMapping("/")
    public ResponseEntity<AuthorResponse> getAuthor(@RequestParam UUID id) {

        AuthorResponseDto authorResponseDto = authorService.getAuthor(id);

        AuthorResponse authorResponse = authorModelMapper.toAuthorResponse(authorResponseDto);

        return ResponseEntity.ok(authorResponse);
    }

    @PostMapping("/")
    public ResponseEntity<Void> createAuthor(@RequestBody @Valid AuthorRequest authorRequest) {

        AuthorRequestDto authorRequestDto = authorModelMapper.toAuthorRequestDto(authorRequest);

        authorService.createAuthor(authorRequestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAuthor(@PathVariable UUID id, @RequestBody @Valid AuthorRequest authorRequest) {

        AuthorRequestDto authorRequestDto = authorModelMapper.toAuthorRequestDto(authorRequest);

        authorService.updateAuthor(id, authorRequestDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID id) {

        authorService.deleteAuthor(id);

        return ResponseEntity.ok().build();
    }
}
