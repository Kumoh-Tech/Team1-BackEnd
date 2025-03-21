package com.club_board.club_board_server.dto.book;
import com.club_board.club_board_server.domain.book.BookStatus;
import com.club_board.club_board_server.domain.book.ReservationStatus;
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
    private String bookUrl;
    private Long bookImageId;
    private int borrowCount;
    private ReservationStatus reservationStatus;


    @Builder
    public BookResponse(Long id,String title, String author, int publishYear, String publisher, BookStatus status, String bookUrl, Long bookImageId, int borrowCount,ReservationStatus reservationStatus) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.status = status;
        this.bookUrl = bookUrl;
        this.bookImageId=bookImageId;
        this.borrowCount=borrowCount;
        this.reservationStatus = reservationStatus;
    }
}
