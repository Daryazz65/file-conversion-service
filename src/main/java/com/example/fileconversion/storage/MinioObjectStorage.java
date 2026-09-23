package com.example.fileconversion.storage;

import com.example.fileconversion.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.stereotype.Component;

@Component
public class MinioObjectStorage implements ObjectStorage {

    private static final long UNKNOWN_OBJECT_SIZE = -1;

    private final MinioClient client;

    public MinioObjectStorage(MinioClient client) {
        this.client = client;
    }

    @Override
    public byte[] download(String bucket, String key) {
        try (InputStream input = client.getObject(buildGetObjectArgs(bucket, key))) {
            return input.readAllBytes();
        } catch (Exception exception) {
            throw new FileStorageException("Cannot download " + bucket + "/" + key, exception);
        }
    }

    @Override
    public void upload(String bucket, String key, byte[] content, String contentType) {
        try {
            ensureBucket(bucket);

            client.putObject(buildPutObjectArgs(bucket, key, content, contentType));
        } catch (Exception exception) {
            throw new FileStorageException("Cannot upload " + bucket + "/" + key, exception);
        }
    }

    public void ensureBucket(String bucket) {
        try {
            if (!bucketExists(bucket)) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
            }
        } catch (Exception exception) {
            throw new FileStorageException("Cannot prepare bucket " + bucket, exception);
        }
    }

    private boolean bucketExists(String bucket) throws Exception {
        return client.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build());
    }

    private GetObjectArgs buildGetObjectArgs(String bucket, String key) {
        return GetObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .build();
    }

    private PutObjectArgs buildPutObjectArgs(
            String bucket,
            String key,
            byte[] content,
            String contentType
    ) {
        return PutObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .stream(new ByteArrayInputStream(content), content.length, UNKNOWN_OBJECT_SIZE)
                .contentType(contentType)
                .build();
    }
}