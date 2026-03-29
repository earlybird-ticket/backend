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
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
@EnableConfigurationProperties(NcpStorageProperties.class)
@RequiredArgsConstructor
public class StorageConfig {

    private final NcpStorageProperties ncpStorageProperties;

    /**
     * 최신 AWS SDK의 기본 checksum 처리와 NCP Object Storage가 충돌해 403 발생할 수 있어
     * 요청/응답의 checksum 정책을 WHEN_REQUIRED로 제한한다.
     */
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
            )
            .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
            .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
            .build();
    }
}
