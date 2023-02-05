package com.aws_demo_app.user_service.AWS;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.amazonaws.services.s3.model.ObjectMetadata;


@AllArgsConstructor
@Service
public class FileStore {
    private final AmazonS3 amazonS3;

    public void upload(String path, String fileName, Map<String, String> metaData, InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();

        /*
            Using lambda's this code can be converted to below line
            for(Map.Entry<String, String> entry : metaData.entrySet()) {
                objectMetadata.addUserMetadata(entry.getKey(), entry.getValue());
            }
        */
        metaData.forEach(objectMetadata::addUserMetadata);

        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public byte[] download(String path, String key) {
        try {
            S3Object object = amazonS3.getObject(path, key);
            S3ObjectInputStream objectContent = object.getObjectContent();
            return IOUtils.toByteArray(objectContent);
        } catch (AmazonServiceException | IOException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public void deleteFile(String bucketName, String fileName) {

        try {
            amazonS3.deleteObject(bucketName, fileName);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to delete the file", e);
        }
    }

}
