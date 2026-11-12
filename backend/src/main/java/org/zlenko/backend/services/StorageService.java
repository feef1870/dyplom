package org.zlenko.backend.services;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final MinioClient minioClient;

    @Value("${app.minio.bucket}")
    private String bucket;

    @PostConstruct
    public void ensureBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize MinIO bucket '" + bucket + "'", e);
        }
    }

    public void upload(String key, InputStream stream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not upload object '" + key + "'", e);
        }
    }

    public InputStream download(String key) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucket).object(key).build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not download object '" + key + "'", e);
        }
    }

    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucket).object(key).build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Could not delete object '" + key + "'", e);
        }
    }
}
