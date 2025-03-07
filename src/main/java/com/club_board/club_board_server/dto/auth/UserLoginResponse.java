package com.club_board.club_board_server.dto.auth;

import com.club_board.club_board_server.domain.user.Department;
import com.club_board.club_board_server.domain.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLoginResponse {

    @NotBlank
    private String message;

    @NotBlank
    private String accessToken;

    @NotBlank
    private Role role;

    @NotBlank
    private String name;

    @NotBlank
    private String department;


    @Builder
    public UserLoginResponse(String message ,String accessToken,Role role, String name, String department) {
        this.message = message;
        this.accessToken = accessToken;
        this.role=role;
        this.name = name;
        this.department = department;
    }
}
