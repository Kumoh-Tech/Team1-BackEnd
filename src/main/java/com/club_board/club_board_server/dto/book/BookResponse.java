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
    private Long bookImageId;
    private String bookUrl;
    private int borrowCount;
    private ReservationStatus reservationStatus;


    @Builder
    public BookResponse(Long id,String title, String author, int publishYear, String publisher, BookStatus status, Long bookImageId, String bookUrl,int borrowCount,ReservationStatus reservationStatus) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.publisher = publisher;
        this.status = status;
        this.bookImageId = bookImageId;
        this.bookUrl = bookUrl;
        this.borrowCount=borrowCount;
        this.reservationStatus = reservationStatus;
    }
}
