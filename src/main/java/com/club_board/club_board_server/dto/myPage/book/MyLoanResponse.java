package com.club_board.club_board_server.dto.myPage.book;

import lombok.Getter;
import java.time.LocalDateTime;

import static com.club_board.club_board_server.service.book.BookAdminService.MAX_LOAN_PERIOD_DAYS;

@Getter
public class MyLoanResponse {
    private Long bookId;
    private String bookName;
    private LocalDateTime loanDate;
    private LocalDateTime returnDate;
    private Integer overdueDate;

    public MyLoanResponse(Long bookId, String bookName, LocalDateTime loanDate, Integer overdueDate) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.loanDate = loanDate;
        this.returnDate = loanDate.plusDays(MAX_LOAN_PERIOD_DAYS);
        if (overdueDate < 0) {
            this.overdueDate = 0;
        } else {
            this.overdueDate = overdueDate;
        }
    }
}
