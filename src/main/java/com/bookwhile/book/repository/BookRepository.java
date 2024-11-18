package com.bookwhile.book.repository;

import com.bookwhile.book.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookRepository extends JpaRepository<BookEntity, UUID> {
}
