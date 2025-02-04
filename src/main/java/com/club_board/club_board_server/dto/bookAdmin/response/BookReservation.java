package com.club_board.club_board_server.dto.bookAdmin.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookReservation {
    private Long bookId;
    private String bookTitle;
    private Long reservationId;
    private Long userId;
    private String userName;
    private LocalDateTime reservationDate;

    public BookReservation(Long bookId, String bookTitle, Long reservationId, Long userId, String userName, LocalDateTime reservationDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.reservationId = reservationId;
        this.userId = userId;
        this.userName = userName;
        this.reservationDate = reservationDate;
    }
}

