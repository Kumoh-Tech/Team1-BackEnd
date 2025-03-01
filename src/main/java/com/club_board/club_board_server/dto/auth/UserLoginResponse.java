package com.club_board.club_board_server.dto.auth;

import com.club_board.club_board_server.domain.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    @NotBlank
    private String message;

    @NotBlank
    private String accessToken;

    @NotBlank
    private Role role;

    public UserLoginResponse(String message ,String accessToken, Role role) {
        this.message = message;
        this.accessToken = accessToken;
        this.role=role;
    }
}
