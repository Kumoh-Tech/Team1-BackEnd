package com.club_board.club_board_server.dto.bookAdmin.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookReservation {
    private Long bookId;
    private String bookTitle;
    private Long reservationId;
    private Long userId;
    private String userName;
    private LocalDateTime reservationDate;
}

