package com.bookwhile.book.service;

import com.bookwhile.author.dto.AuthorDto;
import com.bookwhile.author.entity.AuthorEntity;
import com.bookwhile.author.repository.AuthorRepository;
import com.bookwhile.book.dto.BookResponseDto;
import com.bookwhile.book.dto.CreateBookRequestDto;
import com.bookwhile.book.dto.UpdateBookRequestDto;
import com.bookwhile.book.entity.BookEntity;
import com.bookwhile.book.mapper.BookDtoMapper;
import com.bookwhile.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookService {

    private final BookDtoMapper bookDtoMapper;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    public BookResponseDto getBook(UUID id) {

        BookEntity bookEntity = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        AuthorDto authorDto = bookDtoMapper.toAuthorDto(bookEntity.getAuthorEntity());

        return bookDtoMapper.toBookResponseDto(bookEntity, authorDto);
    }

    public List<BookResponseDto> getAllBooks() {

        List<BookEntity> bookList = bookRepository.findAll();

        return bookDtoMapper.toBookResponseDtoList(bookList);
    }

    public void createBook(CreateBookRequestDto createBookRequestDto) {

        AuthorEntity authorEntity = authorRepository.findById(createBookRequestDto.getAuthorId())
            .orElseThrow(() -> new RuntimeException("Author not found"));

        BookEntity bookEntity = bookDtoMapper.toBookEntity(createBookRequestDto, authorEntity);

        bookRepository.save(bookEntity);
    }

    public void updateBook(UUID id, UpdateBookRequestDto updateBookRequestDto) {

        BookEntity bookEntity = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        bookDtoMapper.updateBookEntity(bookEntity, updateBookRequestDto);

        bookRepository.save(bookEntity);
    }

    public void deleteBook(UUID id) {

        bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));

        bookRepository.deleteById(id);
    }


}
