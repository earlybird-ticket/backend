package com.earlybird.ticket.venue.infrastructure.storage.config;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@EnableConfigurationProperties(NcpStorageProperties.class)
@RequiredArgsConstructor
public class StorageConfig {

    private final NcpStorageProperties ncpStorageProperties;

    @Bean
    public S3Client ncpObjectStorage() {
        return S3Client.builder()
            .endpointOverride(URI.create(ncpStorageProperties.endpoint()))
            .region(Region.of(ncpStorageProperties.region()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        ncpStorageProperties.accessKey(),
                        ncpStorageProperties.secretKey()
                    )
                )
            ).serviceConfiguration(
                S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build()
            ).overrideConfiguration(b ->
                b.apiCallAttemptTimeout(Duration.of(3, ChronoUnit.SECONDS))
                    .apiCallTimeout(Duration.of(10, ChronoUnit.SECONDS))
                    .retryStrategy(RetryMode.STANDARD)
            ).build();
    }
}
