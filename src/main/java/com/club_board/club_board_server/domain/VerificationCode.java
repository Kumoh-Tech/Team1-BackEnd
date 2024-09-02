package com.club_board.club_board_server.domain;

import java.time.LocalDateTime;

public class VerificationCode {

    private int code;
    private LocalDateTime timestamp;

    public VerificationCode(int code, LocalDateTime timestamp) {
        this.code=code;
        this.timestamp=timestamp;
    }

    public int getCode(){
        return code;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }
}
