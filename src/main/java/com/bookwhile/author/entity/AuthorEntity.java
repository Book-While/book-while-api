package com.bookwhile.author.entity;


import com.bookwhile.book.entity.BookEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "AUTHORS")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    //    FIXME: consider removing this and store photos elsewhere
    @Column(name = "author_photo")
    private byte[] authorPhoto;

    @OneToMany(mappedBy = "authorEntity", cascade = CascadeType.REMOVE)
    private List<BookEntity> books;
}
