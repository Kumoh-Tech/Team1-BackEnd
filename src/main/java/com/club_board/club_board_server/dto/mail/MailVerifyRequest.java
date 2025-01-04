package com.club_board.club_board_server.dto.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailVerifyRequest {

    @NotBlank
    private String username;

    @NotBlank
    private int mailCode;

}
