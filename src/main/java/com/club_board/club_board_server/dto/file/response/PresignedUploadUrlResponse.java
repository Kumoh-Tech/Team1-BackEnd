package com.club_board.club_board_server.dto.file.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
@Builder
public class PresignedUploadUrlResponse {
    private String url;

    @Builder.Default
    private String method = "PUT";

    private Map<String, String> headers;
}
