package com.club_board.club_board_server.domain;

public enum Role {
    ROLE_OWNER("회장"),
    ROLE_ADMIN("운영진"),
    ROLE_USER("일반 유저");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }
}
