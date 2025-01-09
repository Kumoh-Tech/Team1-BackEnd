package com.club_board.club_board_server.dto.book;
import com.club_board.club_board_server.domain.book.BookStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private int publishYear;
    private String publisher;
    private BookStatus status;


    @Builder
    public BookResponse(Long id,String title, String author, int publishYear, String publisher, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.status = status;
    }
}
