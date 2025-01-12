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

    public PresignedUploadUrlResponse generateUploadUrl(PresignedUploadUrlRequest request, Long userId) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (
                S3Presigner s3Presigner = S3Presigner.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .region(Region.AP_NORTHEAST_2)
                        .build()
        ) {
            String objectName = this.generateFileName(request.getFileName(), userId);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .contentType(request.getContentType())
                    .build();

            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

            File savedFile = fileService.saveFileName(objectName);

            return PresignedUploadUrlResponse.builder()
                    .url(presignedPutObjectRequest.url().toString())
                    .fileId(savedFile.getId())
                    .build();
        }
    }

    private String generateFileName(String originFileName, Long userId) {
        return String.join(
                "/", "postFiles", userId.toString(), UUID.randomUUID().toString(), originFileName
        );
    }

    public PresignedDownloadUrlResponse generateDownloadUrl(Long fileId) {
        String objectName = fileService.getFileName(fileId);

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
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

            return PresignedDownloadUrlResponse.builder()
                    .url(presignedGetObjectRequest.url().toString())
                    .build();
        }
    }

    public PresignedUploadUrlResponse generateBookImageUploadUrl(PresignedUploadUrlRequest request) {
        if (!request.getContentType().startsWith("image/")) {
            throw new BusinessException(ExceptionType.INVALID_FILE_TYPE);
        }

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        try (
                S3Presigner s3Presigner = S3Presigner.builder()
                        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                        .region(Region.AP_NORTHEAST_2)
                        .build()
        ) {
            String objectName = this.generateBookImageName(request.getFileName());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectName)
                    .contentType(request.getContentType())
                    .build();

            PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(60))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(putObjectPresignRequest);

            BookImage savedBookImage = bookImageService.saveBookImage(objectName);

            return PresignedUploadUrlResponse.builder()
                    .url(presignedPutObjectRequest.url().toString())
                    .fileId(savedBookImage.getId())
                    .build();
        }
    }

    private String generateBookImageName(String originFileName) {
        return String.join(
                "/", "bookImages", UUID.randomUUID().toString(), originFileName
        );
    }

    public PresignedDownloadUrlResponse generateBookImageDownloadUrl(Long bookImageId) {
        String objectName = bookImageService.getFileName(bookImageId);

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
                    .signatureDuration(Duration.ofMinutes(60))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

            return PresignedDownloadUrlResponse.builder()
                    .url(presignedGetObjectRequest.url().toString())
                    .build();
        }
    }

    public void deleteBookImage(BookImage bookImage) {
        this.deleteObject(bookImage.getUrl());

        bookImageService.deleteBookImage(bookImage);
    }

    private void deleteObject(String url) {
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
