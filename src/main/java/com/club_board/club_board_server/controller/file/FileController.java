package com.club_board.club_board_server.controller.file;

import com.club_board.club_board_server.dto.file.request.PresignedUploadUrlRequest;
import com.club_board.club_board_server.dto.file.response.PresignedDownloadUrlResponse;
import com.club_board.club_board_server.dto.file.response.PresignedUploadUrlResponse;
import com.club_board.club_board_server.response.ResponseBody;
import com.club_board.club_board_server.response.ResponseUtil;
import com.club_board.club_board_server.service.file.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/files")
public class FileController {
    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseEntity<ResponseBody<PresignedUploadUrlResponse>> generateUploadUrl(
            @RequestBody @Valid PresignedUploadUrlRequest request
    ) {
        Long userId = 1L;
        PresignedUploadUrlResponse response = s3Service.generateUploadUrl(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/download-url")
    public ResponseEntity<ResponseBody<PresignedDownloadUrlResponse>> generateDownloadUrl(@RequestParam Long fileId) {
        PresignedDownloadUrlResponse response = s3Service.generateDownloadUrl(fileId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

}
