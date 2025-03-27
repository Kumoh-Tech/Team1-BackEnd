package com.club_board.club_board_server.service.file;

import com.club_board.club_board_server.domain.book.BookImage;
import com.club_board.club_board_server.domain.file.File;
import com.club_board.club_board_server.dto.file.request.PresignedUploadUrlRequest;
import com.club_board.club_board_server.dto.file.response.PresignedDownloadUrlResponse;
import com.club_board.club_board_server.dto.file.response.PresignedUploadUrlResponse;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import com.club_board.club_board_server.service.book.BookImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final FileService fileService;
    private final BookImageService bookImageService;

    @Value("${aws.s3.credentials.accessKey}")
    private String accessKey;

    @Value("${aws.s3.credentials.secretKey}")
    private String secretKey;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private final static int PUT_REQUEST_DURATION_OF_MINUTES = 60;
    private final static int GET_REQUEST_DURATION_OF_MINUTES = 60;

    public PresignedUploadUrlResponse generatePostFileUploadUrl(PresignedUploadUrlRequest request, Long userId) {
        String objectName = this.generatePostFileName(request.getFileName(), userId);

        String fileUploadUrl = this.generatePutObjectRequestUrl(objectName, request.getContentType());

        File savedFile = fileService.saveFileName(objectName);

        return PresignedUploadUrlResponse.builder()
                .url(fileUploadUrl)
                .fileId(savedFile.getId())
                .build();
    }

    private String generatePostFileName(String originFileName, Long userId) {
        return String.join(
                "/", "postFiles", userId.toString(), UUID.randomUUID().toString(), originFileName
        );
    }

    public PresignedUploadUrlResponse generateBookImageUploadUrl(PresignedUploadUrlRequest request) {
        this.checkImageContentType(request.getContentType());

        String objectName = this.generateBookImageName(request.getFileName());

        String fileUploadUrl = this.generatePutObjectRequestUrl(objectName, request.getContentType());

        BookImage savedBookImage = bookImageService.saveBookImage(objectName);

        return PresignedUploadUrlResponse.builder()
                .url(fileUploadUrl)
                .fileId(savedBookImage.getId())
                .build();
    }

    private String generateBookImageName(String originFileName) {
        return String.join(
                "/", "bookImages", UUID.randomUUID().toString(), originFileName
        );
    }

    private String generatePutObjectRequestUrl(String objectName, String contentType) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (
                S3Presigner s3Presigner = S3Presigner.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .region(Region.AP_NORTHEAST_2)
                        .build()
        ) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(PUT_REQUEST_DURATION_OF_MINUTES))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

            return presignedPutObjectRequest.url().toString();
        }
    }

    public PresignedDownloadUrlResponse generatePostFileDownloadUrl(Long fileId) {
        String objectName = fileService.getFileName(fileId);

        String fileDownloadUrl = this.generateGetObjectRequestUrl(objectName);

        return PresignedDownloadUrlResponse.builder()
                .url(fileDownloadUrl)
                .build();
    }

    public PresignedDownloadUrlResponse generateBookImageDownloadUrl(Long bookImageId) {
        String objectName = bookImageService.getFileName(bookImageId);

        String fileDownloadUrl = this.generateGetObjectRequestUrl(objectName);

        return PresignedDownloadUrlResponse.builder()
                .url(fileDownloadUrl)
                .build();
    }

    private String generateGetObjectRequestUrl(String objectName) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (
                S3Presigner s3Presigner = S3Presigner.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .region(Region.AP_NORTHEAST_2)
                        .build()
        ) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(GET_REQUEST_DURATION_OF_MINUTES))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

            return presignedGetObjectRequest.url().toString();
        }
    }

    public PresignedUploadUrlResponse generateBookImageUpdateUrl(PresignedUploadUrlRequest request, Long bookImageId) {
        this.checkImageContentType(request.getContentType());

        String objectName = bookImageService.getFileName(bookImageId);

        String fileUpdateUrl = this.generatePutObjectRequestUrl(objectName, request.getContentType());

        return PresignedUploadUrlResponse.builder()
                .url(fileUpdateUrl)
                .fileId(bookImageId)
                .build();
    }

    private void checkImageContentType(String contentType) {
        if (!contentType.startsWith("image/")) {
            throw new BusinessException(ExceptionType.INVALID_FILE_TYPE);
        }
    }

    public void deleteBookImage(BookImage bookImage) {
        this.deleteObjectRequest(bookImage.getUrl());
    }

    private void deleteObjectRequest(String url) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (
                S3Client s3 = S3Client.builder()
                        .region(Region.AP_NORTHEAST_2)
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .build();
        ) {
            DeleteObjectRequest deleteObjectsRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(url)
                    .build();

            s3.deleteObject(deleteObjectsRequest);
        }
    }
}
