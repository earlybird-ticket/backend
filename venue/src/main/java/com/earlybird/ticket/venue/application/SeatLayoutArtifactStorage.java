package com.earlybird.ticket.venue.application;

public interface SeatLayoutArtifactStorage {
    boolean checkArtifactExists(String objectKey);

    void storeArtifact(String objectKey, byte[] artifact, String hash, String schemaVersion);

    void deleteArtifact(String objectKey);

    String resolveArtifactUrl(String objectKey);
}
