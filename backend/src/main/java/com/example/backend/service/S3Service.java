package com.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${BUCKET_NAME}")
    private String bucketName;

    @Value("${REGION}")
    private String region;

    // 파일 저장 경로(폴더)
    private final String FILE_PATH_PREFIX = "articles/";

    // S3 파일 업로드 처리 메서드
    // 파일을 articleService에서 받은 후
    // s3 업로드 후 imageUrl과 s3객체 키를 반환하는 메서드
    public Map<String, String> uploadFile(MultipartFile file) {
        // s3Key 생성
        String s3Key = FILE_PATH_PREFIX + UUID.randomUUID() + "_" + file.getOriginalFilename();

        // s3 버킷에 파일 업로드
        // 업로드할 file과 s3 객체 키(s3Key) 를 전달
        uploadFileToS3(s3Key, file);

        String IMAGE_URL_FORMAT = "https://%s.s3.%s.amazonaws.com/%s";
        // https://버킷명.s3.리전.amazonaws.com/객체키
        String imageUrl = String.format(IMAGE_URL_FORMAT, bucketName, region, s3Key);
//        String imageUrl = "http://" + bucketName + ".s3." + region + ".amazonaws.com/" + s3Key;
        return Map.of(
                "imageUrl", imageUrl,
                "s3Key", s3Key
        );
    }

    private void uploadFileToS3(String s3Key, MultipartFile file) {
        try {

        // S3에 요청할 객체
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void deleteFile(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}