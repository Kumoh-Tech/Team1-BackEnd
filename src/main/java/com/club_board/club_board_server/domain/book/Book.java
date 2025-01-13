package com.club_board.club_board_server.domain.book;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "author", nullable = false)
    private String author;

    @Setter
    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Setter
    @Column(name = "publish_year", nullable = false)
    private int publishYear;

    @Setter
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Setter
    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private BookImage bookImage;

    @Builder
    public Book(String title, String author, String publisher, int publishYear) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.status = BookStatus.AVAILABLE;
    }
}
