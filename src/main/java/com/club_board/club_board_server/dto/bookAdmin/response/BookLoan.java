package com.club_board.club_board_server.dto.bookAdmin.response;

import lombok.Getter;

import java.time.LocalDateTime;

import static com.club_board.club_board_server.service.book.BookAdminService.MAX_LOAN_PERIOD_DAYS;

@Getter
public class BookLoan {
    private Long bookId;
    private String bookTitle;
    private Long reservationId;
    private Long userId;
    private String userName;
    private LocalDateTime borrowDate;
    private LocalDateTime returnDueDate;

    public BookLoan(Long bookId, String bookTitle, Long reservationId, Long userId, String userName, LocalDateTime borrowDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.reservationId = reservationId;
        this.userId = userId;
        this.userName = userName;
        this.borrowDate = borrowDate;
        this.returnDueDate = borrowDate.plusDays(MAX_LOAN_PERIOD_DAYS);
    }
}
