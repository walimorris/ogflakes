package com.morris.ogflakes.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.AmazonSNSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AwsSnsConfig {
    private static final Logger logger = LoggerFactory.getLogger(AwsSnsConfig.class);

    @Value("${cloud.aws.credentials.access-key}")
    private String awsAccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String awsSecretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Primary
    @Bean
    public AmazonSNSClient amazonSNSClient() {
        return (AmazonSNSClient) AmazonSNSAsyncClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider((new BasicAWSCredentials(
                        awsAccessKey, awsSecretKey))))
                .build();
    }
}
