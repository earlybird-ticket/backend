package com.earlybird.ticket.venue.infrastructure.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ncp.object-storage")
public record NcpStorageProperties(
    String endpoint,
    String region,
    String bucket,
    String baseUrl,
    String accessKey,
    String secretKey
) {

}
