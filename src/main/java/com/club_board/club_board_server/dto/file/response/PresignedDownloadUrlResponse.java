package com.club_board.club_board_server.dto.file.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PresignedDownloadUrlResponse {
    private String url;

    @Builder.Default
    private String method = "GET";
}
