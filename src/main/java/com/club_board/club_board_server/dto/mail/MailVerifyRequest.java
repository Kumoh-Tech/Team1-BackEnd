package com.club_board.club_board_server.dto.mail;

import lombok.Getter;

@Getter
public class MailVerifyRequest {
    private String username;
    private int mailCode;

}
