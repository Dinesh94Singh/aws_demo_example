package com.aws_demo_app.user_service.AWS;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// How to use Value annotation - https://www.baeldung.com/spring-value-annotation

@Configuration
@Slf4j
public class AWSConfiguration {

    @Value("${cloud.aws.assumeRoleARN}")
    private String assumeRoleARN;

    @Value("${cloud.aws.roleSessionName}")
    private String roleSessionName;

    @Value("${cloud.aws.region.static}")
    private String regionName;

    /*
     * Since accessKey and secretKey are not stored in github we will use ARN's
     * 
     * @Value("${cloud.aws.credentials.access-key}")
     * private String accessKey;
     * 
     * @Value("${cloud.aws.credentials.secret-key}")
     * private String secretKey;
     */

    @Bean
    public AmazonS3 s3() {
        if (StringUtils.isNotEmpty(assumeRoleARN)) {
            AWSCredentials awsCredentials = getAWSCredentials();

            // Provide temporary security credentials so that the Amazon S3 client
            // can send authenticated requests to Amazon S3. You create the client
            // using the sessionCredentials object.
            final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(regionName)
                    .build();

            return s3Client;
        }

        return null;
    }

    @Bean
    public AmazonSQSAsync amazonSqsAsync() {
        if (StringUtils.isNotEmpty(assumeRoleARN)) {
            AWSCredentials awsCredentials = getAWSCredentials();
            try {
                AmazonSQSAsync client = AmazonSQSAsyncClientBuilder.standard().withRegion(regionName)
                        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                        .build();
                log.info("Successfully built sqs client !!");
                return client;
            } catch (Exception ex) {
                log.info("Unable to create sqs client due to " + ex);
            }
        }

        return null;
    }

    private AWSCredentials getAWSCredentials() {
        // Creating the STS client is part of your trusted code. It has
        // the security credentials you use to obtain temporary security credentials.
        AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion(regionName)
                .build();

        // Obtain credentials for the IAM role. Note that you cannot assume the role of
        // an AWS root account;
        // Amazon S3 will deny access. You must use credentials for an IAM user or an
        // IAM role.
        AssumeRoleRequest roleRequest = new AssumeRoleRequest()
                .withRoleArn(assumeRoleARN)
                .withRoleSessionName(roleSessionName);

        AssumeRoleResult roleResponse = stsClient.assumeRole(roleRequest);
        Credentials sessionCredentials = roleResponse.getCredentials();

        log.info("Access Key is " + sessionCredentials.getAccessKeyId());
        log.info("Secret Key is " + sessionCredentials.getSecretAccessKey());
        log.info("Session token is " + sessionCredentials.getSessionToken());

        // Create a BasicSessionCredentials object that contains the credentials you
        // just retrieved.
        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(
                sessionCredentials.getAccessKeyId(),
                sessionCredentials.getSecretAccessKey(),
                sessionCredentials.getSessionToken());

        return awsCredentials;
    }
}
