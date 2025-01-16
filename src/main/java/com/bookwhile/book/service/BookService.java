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
import com.bookwhile.exception.BookWhileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.bookwhile.constant.Constant.AUTHOR_NOT_FOUND;
import static com.bookwhile.constant.Constant.BOOK_NOT_FOUND;


@RequiredArgsConstructor
@Service
public class BookService {

    private final BookDtoMapper bookDtoMapper;

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    public BookResponseDto getBook(UUID id) {

        BookEntity bookEntity = bookRepository.findById(id)
            .orElseThrow(() -> new BookWhileException(BOOK_NOT_FOUND));

        AuthorDto authorDto = bookDtoMapper.toAuthorDto(bookEntity.getAuthorEntity());

        return bookDtoMapper.toBookResponseDto(bookEntity, authorDto);
    }

    public List<BookResponseDto> getAllBooks() {

        List<BookEntity> bookList = bookRepository.findAll();

        return bookDtoMapper.toBookResponseDtoList(bookList);
    }

    public void createBook(CreateBookRequestDto createBookRequestDto) {

        AuthorEntity authorEntity = authorRepository.findById(createBookRequestDto.getAuthorId())
            .orElseThrow(() -> new BookWhileException(AUTHOR_NOT_FOUND));

        BookEntity bookEntity = bookDtoMapper.toBookEntity(createBookRequestDto, authorEntity);

        bookRepository.save(bookEntity);
    }

    public void updateBook(UUID id, UpdateBookRequestDto updateBookRequestDto) {

        BookEntity bookEntity = bookRepository.findById(id)
            .orElseThrow(() -> new BookWhileException(BOOK_NOT_FOUND));

        bookDtoMapper.updateBookEntity(bookEntity, updateBookRequestDto);

        bookRepository.save(bookEntity);
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }
}
