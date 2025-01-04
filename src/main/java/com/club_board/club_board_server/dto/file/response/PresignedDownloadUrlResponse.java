package com.club_board.club_board_server.dto.file.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PresignedDownloadUrlResponse {
    private String url;
    private String method;
}
