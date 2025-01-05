package com.club_board.club_board_server.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    @NotBlank
    private String message;

    @NotBlank
    private String accessToken;

    public UserLoginResponse(String message ,String accessToken) {
        this.message = message;
        this.accessToken = accessToken;
    }
}
