package com.club_board.club_board_server.dto.file.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PresignedUploadUrlRequest {
    @NotBlank
    private String fileName;
    @NotBlank
    private String contentType;
}
