package com.club_board.club_board_server.dto.file.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PresignedProfileImageUrlRequest {
    @NotBlank
    private String contentType;
}
