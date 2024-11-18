package com.bookwhile.author.repository;

import com.bookwhile.author.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<AuthorEntity, UUID> {
}
