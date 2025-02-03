package com.club_board.club_board_server.domain.mail;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class VerificationCode {

    private int code;
    private LocalDateTime timestamp;

    public VerificationCode(int code, LocalDateTime timestamp) {
        this.code=code;
        this.timestamp=timestamp;
    }
}
