package com.aws_demo_app.user_service.AWS;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.logging.Logger;

// How to use Value annotation - https://www.baeldung.com/spring-value-annotation

@Configuration
@Slf4j
public class AWSConfiguration {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String regionName;

    @Bean
    public AmazonS3 s3() {
        // TODO: Read Access Key and Secret Key from Environment Variables, instead of yml file

        log.info(regionName + " is the region name");
        log.info(accessKey + " is the access key");
        log.info(secretKey + " is the secret key");

        AWSCredentials awsCredentials =
                new BasicAWSCredentials(accessKey, secretKey);

        // Amazon S3 is not Global and its only regional
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(regionName)
                .build();
    }
}
