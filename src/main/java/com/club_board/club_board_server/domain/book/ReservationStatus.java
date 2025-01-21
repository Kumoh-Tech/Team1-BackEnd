package com.club_board.club_board_server.domain.book;

public enum ReservationStatus {
    RESERVED("예약 중"),
    BORROWING("대출 중"),
    OVERDUE("연체(대출 중)"),
    RETURNED("반납"),
    OVERDUE_RETURNED("연체(반납)");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
