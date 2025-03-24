package com.club_board.club_board_server.controller.file;

import com.club_board.club_board_server.domain.user.CustomUserDetails;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/files")
public class FileController {
    private final S3Service s3Service;

    @PostMapping("/upload-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedUploadUrlResponse>> generatePostFileUploadUrl(
            @RequestBody @Valid PresignedUploadUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PresignedUploadUrlResponse response = s3Service.generatePostFileUploadUrl(request, userDetails.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/download-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedDownloadUrlResponse>> generatePostFileDownloadUrl(@RequestParam Long fileId) {
        PresignedDownloadUrlResponse response = s3Service.generatePostFileDownloadUrl(fileId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @PostMapping("/bookImage/upload-url")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedUploadUrlResponse>> generateBookImageUploadUrl(
            @RequestBody @Valid PresignedUploadUrlRequest request
    ) {
        PresignedUploadUrlResponse response = s3Service.generateBookImageUploadUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    @PostMapping("/bookImage/update-url")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedUploadUrlResponse>> generateBookImageUpdateUrl(
            @RequestBody @Valid PresignedUploadUrlRequest request,
            @RequestParam Long bookImageId
    ) {
        PresignedUploadUrlResponse response = s3Service.generateBookImageUpdateUrl(request, bookImageId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/bookImage/download-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedDownloadUrlResponse>> generateBookImageDownloadUrl(@RequestParam Long bookImageId) {
        PresignedDownloadUrlResponse response = s3Service.generateBookImageDownloadUrl(bookImageId);
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

}
