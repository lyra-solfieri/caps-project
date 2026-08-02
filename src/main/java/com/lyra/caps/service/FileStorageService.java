package com.lyra.caps.service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

@Service
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public FileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String salvar(MultipartFile file, String correlationId) {
        try {
            boolean found = minioClient.bucketExists(
                io.minio.BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder().bucket(bucket).build());
            }

            String objectName = correlationId + "/" + file.getOriginalFilename();

            try (InputStream is = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(is, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }

            return objectName;
        } catch (MinioException e) {
            throw new RuntimeException("Erro ao salvar arquivo no MinIO", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar arquivo", e);
        }
    }

    public String getUrl(String objectName) {
        return objectName;
    }
}
