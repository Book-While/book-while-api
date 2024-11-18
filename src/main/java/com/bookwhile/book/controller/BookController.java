package com.bookwhile.book.controller;

import com.bookwhile.book.dto.BookResponseDto;
import com.bookwhile.book.dto.CreateBookRequestDto;
import com.bookwhile.book.dto.UpdateBookRequestDto;
import com.bookwhile.book.mapper.BookModelMapper;
import com.bookwhile.book.model.BookResponse;
import com.bookwhile.book.model.CreateBookRequest;
import com.bookwhile.book.model.UpdateBookRequest;
import com.bookwhile.book.service.BookService;
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
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    private final BookModelMapper bookModelMapper;

    @GetMapping("/")
    public ResponseEntity<BookResponse> getBook(@RequestParam UUID id) {

        BookResponseDto bookResponseDto = bookService.getBook(id);

        BookResponse bookResponse = bookModelMapper.toBookResponse(bookResponseDto);

        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookResponse>> getAllBooks() {

        List<BookResponseDto> bookResponseDtoList = bookService.getAllBooks();

        List<BookResponse> bookResponseList = bookModelMapper.toBookResponseList(bookResponseDtoList);

        return ResponseEntity.ok(bookResponseList);
    }

    @PostMapping("/")
    public ResponseEntity<Void> createBook(@RequestBody CreateBookRequest createBookRequest) {

        CreateBookRequestDto createBookRequestDto = bookModelMapper.toBookRequestDto(createBookRequest);

        bookService.createBook(createBookRequestDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBook(@PathVariable UUID id, @RequestBody UpdateBookRequest updateBookRequest) {

        UpdateBookRequestDto updateBookRequestDto = bookModelMapper.toUpdateBookRequestDto(updateBookRequest);

        bookService.updateBook(id, updateBookRequestDto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {

        bookService.deleteBook(id);

        return ResponseEntity.ok().build();
    }
}
