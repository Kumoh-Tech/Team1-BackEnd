package com.club_board.club_board_server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String student_id;
}
