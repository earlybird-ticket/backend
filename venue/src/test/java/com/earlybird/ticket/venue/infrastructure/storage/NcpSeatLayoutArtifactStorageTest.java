package com.earlybird.ticket.venue.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.earlybird.ticket.venue.infrastructure.storage.config.NcpStorageProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

@ExtendWith(MockitoExtension.class)
class NcpSeatLayoutArtifactStorageTest {

    @InjectMocks
    private NcpSeatLayoutArtifactStorage ncpSeatLayoutArtifactStorage;

    @Mock
    private S3Client ncpObjectStorage;

    @Mock
    private NcpStorageProperties ncpStorageProperties;

    @Test
    void 객체가_존재하면_true를_반환한다() {
        // given
        String bucket = "test-bucket";
        String objectKey = "test-object-key";
        BDDMockito.given(ncpStorageProperties.bucket()).willReturn(bucket);
        BDDMockito.given(ncpObjectStorage.headObject(BDDMockito.any(HeadObjectRequest.class)))
            .willReturn(HeadObjectResponse.builder().build());

        // when
        boolean isExist = ncpSeatLayoutArtifactStorage.checkArtifactExists(objectKey);

        // then
        assertThat(isExist).isTrue();
        BDDMockito.then(ncpObjectStorage).should().headObject(
            BDDMockito.argThat((HeadObjectRequest request) ->
                request.bucket().equals(bucket)
                    && request.key().equals(objectKey)
            )
        );
    }

    @Test
    void 객체가_없으면_false를_반환한다() {
        // given
        String bucket = "test-bucket";
        String objectKey = "test-object-key";
        BDDMockito.given(ncpStorageProperties.bucket()).willReturn(bucket);
        BDDMockito.given(ncpObjectStorage.headObject(BDDMockito.any(HeadObjectRequest.class)))
            .willThrow(S3Exception.builder().statusCode(404).build());

        // when
        boolean isExist = ncpSeatLayoutArtifactStorage.checkArtifactExists(objectKey);

        // then
        assertThat(isExist).isFalse();
        BDDMockito.then(ncpObjectStorage).should().headObject(
            BDDMockito.argThat((HeadObjectRequest request) ->
                request.bucket().equals(bucket)
                    && request.key().equals(objectKey)
            )
        );
    }

    @Test
    void 좌석_레이아웃_아티팩트를_업로드한다() {
        // given
        String bucket = "test-bucket";
        String objectKey = "test-object-key";
        byte[] artifact = "test-artifact".getBytes();
        String hash = "test-hash";
        String schemaVersion = "test-schema-version";
        BDDMockito.given(ncpStorageProperties.bucket()).willReturn(bucket);
        BDDMockito.given(ncpObjectStorage.putObject(
            BDDMockito.any(PutObjectRequest.class), BDDMockito.any(RequestBody.class))
        ).willReturn(PutObjectResponse.builder().build());

        // when & then
        assertThatCode(() ->
            ncpSeatLayoutArtifactStorage.storeArtifact(objectKey, artifact, hash, schemaVersion)
        ).doesNotThrowAnyException();

        BDDMockito.then(ncpObjectStorage).should().putObject(
            BDDMockito.argThat((PutObjectRequest putRequest) ->
                putRequest.bucket().equals(bucket)
                    && putRequest.key().equals(objectKey)
                    && putRequest.contentType().equals(MediaType.APPLICATION_JSON_VALUE)
                    && putRequest.metadata().equals(
                    Map.of(
                        "artifact-hash", hash,
                        "schema-version", schemaVersion
                    )
                )
            ),
            BDDMockito.any(RequestBody.class)
        );

    }

    @Test
    void 좌석_레이아웃_아티팩트를_삭제한다() {
        // given
        String bucket = "test-bucket";
        String objectKey = "test-object-key";
        BDDMockito.given(ncpStorageProperties.bucket()).willReturn(bucket);
        BDDMockito.given(ncpObjectStorage.deleteObject(BDDMockito.any(DeleteObjectRequest.class)))
            .willReturn(DeleteObjectResponse.builder().build());

        // when & then
        assertThatCode(() -> ncpSeatLayoutArtifactStorage.deleteArtifact(objectKey))
            .doesNotThrowAnyException();

        BDDMockito.then(ncpObjectStorage).should().deleteObject(
            BDDMockito.argThat((DeleteObjectRequest request) ->
                request.bucket().equals(bucket)
                    && request.key().equals(objectKey)
            )
        );
    }

    @Test
    void 스토리지_키를_기반으로_아티팩트_URL을_생성한다() {
        // given
        String bucket = "test-bucket";
        String objectKey = "test-object-key";
        String endpoint = "http://localhost:8000";

        BDDMockito.given(ncpStorageProperties.bucket()).willReturn(bucket);
        BDDMockito.given(ncpStorageProperties.endpoint()).willReturn(endpoint);

        // when
        String artifactUrl = ncpSeatLayoutArtifactStorage.resolveArtifactUrl(objectKey);

        // then
        assertThat(artifactUrl).isEqualTo(endpoint + "/" + bucket + "/" + objectKey);

    }

}