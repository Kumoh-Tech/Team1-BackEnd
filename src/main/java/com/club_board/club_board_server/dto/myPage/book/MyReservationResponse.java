package com.club_board.club_board_server.dto.myPage.book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyReservationResponse {
    private Long bookId;
    private String bookName;
    private LocalDateTime reservationDate;
}
