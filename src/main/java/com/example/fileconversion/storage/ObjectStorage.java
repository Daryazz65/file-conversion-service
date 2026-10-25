package com.example.fileconversion.storage;

public interface ObjectStorage {

    byte[] download(String bucket, String key);

    void upload(String bucket, String key, byte[] content, String contentType);

    void ensureBucket(String bucket);
}