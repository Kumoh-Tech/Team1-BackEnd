package com.club_board.club_board_server.domain.book;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_image_id")
    private Long id;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public BookImage(String url) {
        this.url = url;
    }
}
