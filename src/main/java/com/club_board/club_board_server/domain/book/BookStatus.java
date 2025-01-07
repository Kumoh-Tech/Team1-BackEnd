package com.club_board.club_board_server.domain.book;

public enum BookStatus {
    AVAILABLE("대여 가능"),
    RESERVED("예약 중"),
    FULLY_RESERVED("예약 불가"),
    ;

    private String message;

    BookStatus(String status) {
        this.message = status;
    }

    public String message() {
        return message;
    }
}
