package com.club_board.club_board_server.domain.user;

public enum Role {
    OWNER("회장"),
    ADMIN("운영진"),
    USER("일반 회원");

    private final String displayName;
    Role(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
