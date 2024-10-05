package com.club_board.club_board_server.dto.auth;

import lombok.Getter;

@Getter
public class UserLoginResponse {

    private int statusCode;
    private String message;
    private String accessToken;
    private String refreshToken;

    public UserLoginResponse(int statusCode,String message ,String accessToken, String refreshToken) {
        this.statusCode = statusCode;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
