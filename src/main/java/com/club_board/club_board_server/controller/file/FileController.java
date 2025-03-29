package com.club_board.club_board_server.controller.file;

import com.club_board.club_board_server.domain.user.CustomUserDetails;
import com.club_board.club_board_server.dto.file.request.PresignedProfileImageUrlRequest;
import com.club_board.club_board_server.dto.file.request.PresignedUploadUrlRequest;
import com.club_board.club_board_server.dto.file.response.PresignedDownloadUrlResponse;
import com.club_board.club_board_server.dto.file.response.PresignedProfileImageUrlResponse;
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

    @PostMapping("/postFile/upload-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedUploadUrlResponse>> generatePostFileUploadUrl(
            @RequestBody @Valid PresignedUploadUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PresignedUploadUrlResponse response = s3Service.generatePostFileUploadUrl(request, userDetails.getUser().getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/postFile/download-url")
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

    @PostMapping("/profileImage/update-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedProfileImageUrlResponse>> generateProfileImageUpdateUrl(
            @RequestBody @Valid PresignedProfileImageUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PresignedProfileImageUrlResponse response = s3Service.generateProfileImageUpdateUrl(request, userDetails.getUser().getId());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @GetMapping("/profileImage/download-url")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<PresignedDownloadUrlResponse>> generateProfileImageDownloadUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PresignedDownloadUrlResponse response = s3Service.generateProfileImageDownloadUrl(userDetails.getUser().getId());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse(response));
    }

    @DeleteMapping("/profileImage")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<ResponseBody<Void>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        s3Service.deleteProfileImage(userDetails.getUser().getId());
        return ResponseEntity.ok(ResponseUtil.createSuccessResponse());
    }
}
