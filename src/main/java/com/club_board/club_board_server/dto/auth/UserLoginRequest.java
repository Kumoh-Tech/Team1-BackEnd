package com.club_board.club_board_server.dto.auth;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

}
