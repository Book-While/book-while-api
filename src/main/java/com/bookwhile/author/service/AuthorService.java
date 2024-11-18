package com.bookwhile.author.service;

import com.bookwhile.author.dto.AuthorRequestDto;
import com.bookwhile.author.dto.AuthorResponseDto;
import com.bookwhile.author.entity.AuthorEntity;
import com.bookwhile.author.mapper.AuthorDtoMapper;
import com.bookwhile.author.repository.AuthorRepository;
import com.bookwhile.book.dto.BookDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    private final AuthorDtoMapper authorDtoMapper;

    public List<AuthorResponseDto> getAllAuthors() {
        List<AuthorEntity> authorList = authorRepository.findAll();
        return authorDtoMapper.toAuthorDtoList(authorList);
    }

    public AuthorResponseDto getAuthor(UUID id) {

        AuthorEntity authorEntity = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));

        List<BookDto> bookDtoList = authorDtoMapper.toBookDtoList(authorEntity.getBooks());

        return authorDtoMapper.toAuthorDto(authorEntity, bookDtoList);
    }

    public void createAuthor(AuthorRequestDto authorRequestDto) {

        AuthorEntity authorEntity = authorDtoMapper.toAuthorEntity(authorRequestDto);
        authorRepository.save(authorEntity);
    }

    public void updateAuthor(UUID id, AuthorRequestDto authorRequestDto) {

        AuthorEntity authorEntity = authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));

        authorDtoMapper.updateAuthorEntity(authorEntity, authorRequestDto);

        authorRepository.save(authorEntity);
    }

    public void deleteAuthor(UUID id) {

        authorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Author not found"));

        authorRepository.deleteById(id);
    }
}
