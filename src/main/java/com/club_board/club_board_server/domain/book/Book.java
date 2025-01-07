package com.club_board.club_board_server.domain.book;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "publish_year", nullable = false)
    private int publishYear;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Builder
    public Book(String title, String author, String publisher, int publishYear) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.status = BookStatus.AVAILABLE;
    }
}
