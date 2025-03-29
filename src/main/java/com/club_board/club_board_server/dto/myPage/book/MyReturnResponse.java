package com.club_board.club_board_server.dto.myPage.book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyReturnResponse {
    private Long bookId;
    private String bookName;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
}
