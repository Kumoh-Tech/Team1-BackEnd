package com.club_board.club_board_server.dto.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MailRequest {

    @NotBlank
    private String username;
}
