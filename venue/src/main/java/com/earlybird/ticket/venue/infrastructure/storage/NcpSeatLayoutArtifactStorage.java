package com.earlybird.ticket.venue.infrastructure.storage;


import com.earlybird.ticket.venue.application.SeatLayoutArtifactStorage;
import com.earlybird.ticket.venue.infrastructure.storage.config.NcpStorageProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class NcpSeatLayoutArtifactStorage implements SeatLayoutArtifactStorage {

    private final S3Client ncpObjectStorage;
    private final NcpStorageProperties ncpStorageProperties;

    /**
        TODO: 존재 여부뿐 아니라 metadata의 artifact-hash와 schema-version 일치 여부까지 검증해
            재사용 가능한 artifact인지 확인하도록 확장
     */
    @Override
    public boolean checkArtifactExists(String objectKey) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(ncpStorageProperties.bucket())
                .key(objectKey)
                .build();

            ncpObjectStorage.headObject(headRequest);
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw new IllegalStateException("아티팩트 존재 여부 확인에 실패했습니다. key =" + objectKey, e);
        }
    }

    @Override
    public void storeArtifact(
        String objectKey, byte[] artifact, String hash, String schemaVersion
    ) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(ncpStorageProperties.bucket())
                .key(objectKey)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .metadata(Map.of(
                    "artifact-hash", hash,
                    "schema-version", schemaVersion
                ))
                .build();

            ncpObjectStorage.putObject(putObjectRequest, RequestBody.fromBytes(artifact));
        } catch (S3Exception e) {
            throw new IllegalStateException("좌석 레이아웃 아티팩트 업로드에 실패했습니다. key=" + objectKey, e);
        }
    }

    @Override
    public void deleteArtifact(String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(ncpStorageProperties.bucket())
                .key(objectKey)
                .build();

            ncpObjectStorage.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new IllegalStateException("좌석 레이아웃 아티팩트 삭제에 실패했습니다. key=" + objectKey, e);
        }
    }

    @Override
    public String resolveArtifactUrl(String objectKey) {
        return ncpStorageProperties.endpoint() + "/" +
            ncpStorageProperties.bucket() + "/"
            + objectKey;
    }
}
