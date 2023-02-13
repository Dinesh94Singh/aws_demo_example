package com.aws_demo_app.user_service.AWS;

import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageResult;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class SQSController {
    private final AmazonSQSAsync sqsClient;

    public void sendMessage(String message, String endPoint) {
        SendMessageResult result = sqsClient.sendMessage(endPoint, message);
        log.info("Message Result: " + result);
    }
}
