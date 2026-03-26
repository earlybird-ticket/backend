package com.earlybird.ticket.venue.application.service;


import static org.assertj.core.api.Assertions.assertThat;

import com.earlybird.ticket.venue.domain.entity.SeatLayoutArtifact;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import com.earlybird.ticket.venue.domain.repository.SeatLayoutArtifactRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatLayoutArtifactServiceTest {

    @InjectMocks
    private SeatLayoutArtifactService seatLayoutArtifactService;

    @Mock
    private SeatLayoutArtifactRepository seatLayoutArtifactRepository;

    private static final String DEFAULT_CDN_URL = "cdnUrl";
    private static final String DEFAULT_STORAGE_KEY = "storageKey";


    @Test
    void 동일한_scope_hash_schemaVersion의_아티팩트가_있으면_기존_메타데이터를_반환한다() {
        // given
        UUID artifactId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String hash = "hash";
        String schemaVersion = "schemaVersion";

        SeatLayoutArtifact existing = seatLayoutArtifact(
            artifactId, concertId, concertSequenceId, section, hash, schemaVersion, false
        );

        BDDMockito.given(
                seatLayoutArtifactRepository.findByScopeAndHashAndSchemaVersion(
                    concertId, concertSequenceId, section, hash, schemaVersion
                )
            )
            .willReturn(Optional.of(existing));

        // when
        SeatLayoutArtifact artifact = seatLayoutArtifactService.prepareArtifactMetadata(
            concertId, concertSequenceId, section, DEFAULT_CDN_URL, DEFAULT_STORAGE_KEY, hash,
            schemaVersion
        );

        // then
        assertThat(existing.isActive()).isFalse();
        assertThat(existing.isReady()).isFalse();
        assertThat(artifact).isSameAs(existing);

        BDDMockito.then(seatLayoutArtifactRepository).should()
            .findByScopeAndHashAndSchemaVersion(
                concertId, concertSequenceId, section, hash, schemaVersion
            );
        BDDMockito.then(seatLayoutArtifactRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void 동일_아티팩트가_없으면_비활성_상태로_새로_저장한다() {
        // given
        UUID artifactId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String hash = "hash";
        String schemaVersion = "schemaVersion";

        SeatLayoutArtifact saved = seatLayoutArtifact(
            artifactId, concertId, concertSequenceId, section, hash, schemaVersion, false
        );

        BDDMockito.given(seatLayoutArtifactRepository
                .findByScopeAndHashAndSchemaVersion(
                    concertId, concertSequenceId, section, hash, schemaVersion
                )
            )
            .willReturn(Optional.empty());

        BDDMockito.given(
                seatLayoutArtifactRepository.save(BDDMockito.any(SeatLayoutArtifact.class))
            )
            .willReturn(saved);

        // when
        SeatLayoutArtifact artifact = seatLayoutArtifactService.prepareArtifactMetadata(
            concertId, concertSequenceId, section, DEFAULT_CDN_URL, DEFAULT_STORAGE_KEY, hash,
            schemaVersion
        );

        // then
        assertThat(artifact.isReady()).isFalse();
        assertThat(artifact.isActive()).isFalse();
        assertThat(artifact).isSameAs(saved);

        BDDMockito.then(seatLayoutArtifactRepository).should().findByScopeAndHashAndSchemaVersion(
            concertId, concertSequenceId, section, hash, schemaVersion
        );
        BDDMockito.then(seatLayoutArtifactRepository).should().save(
            BDDMockito.argThat(toSave ->
                toSave.getConcertId().equals(concertId)
                    && toSave.getConcertSequenceId().equals(concertSequenceId)
                    && toSave.getSection().equals(section)
                    && toSave.getCdnUrl().equals(DEFAULT_CDN_URL)
                    && toSave.getStorageKey().equals(DEFAULT_STORAGE_KEY)
                    && toSave.getHash().equals(hash)
                    && toSave.getSchemaVersion().equals(schemaVersion)
                    && !toSave.isActive()
                    && !toSave.isReady()
            )
        );
        BDDMockito.then(seatLayoutArtifactRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 현재_active_아티팩트가_없으면_대상을_ready_active_상태로_전환한다() {
        // given
        UUID artifactId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String hash = "hash";
        String schemaVersion = "schemaVersion";

        SeatLayoutArtifact artifact = seatLayoutArtifact(
            artifactId, concertId, concertSequenceId, section, hash, schemaVersion, false
        );

        BDDMockito.given(seatLayoutArtifactRepository.findActiveArtifact(
                    concertId, concertSequenceId, section
                )
            )
            .willReturn(Optional.empty());

        // when
        SeatLayoutArtifact activated = seatLayoutArtifactService.markReadyAndActivate(artifact);

        // then
        assertThat(activated.isReady()).isTrue();
        assertThat(activated.isActive()).isTrue();
        assertThat(activated).isSameAs(artifact);

        BDDMockito.then(seatLayoutArtifactRepository).should().findActiveArtifact(
            concertId, concertSequenceId, section
        );
        BDDMockito.then(seatLayoutArtifactRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void 다른_active_아티팩트가_있으면_기존_active를_비활성화하고_대상을_ready_active로_전환한다() {
        // given
        UUID existingArtifactId = UUID.randomUUID();
        UUID artifactId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String hash = "hash";
        String schemaVersion = "schemaVersion";

        SeatLayoutArtifact existing = seatLayoutArtifact(
            existingArtifactId, concertId, concertSequenceId, section, "hash-1", schemaVersion, true
        );

        SeatLayoutArtifact artifact = seatLayoutArtifact(
            artifactId, concertId, concertSequenceId, section, hash, schemaVersion, false
        );

        BDDMockito.given(
                seatLayoutArtifactRepository.findActiveArtifact(concertId, concertSequenceId, section)
            )
            .willReturn(Optional.of(existing));

        // when
        SeatLayoutArtifact current = seatLayoutArtifactService.markReadyAndActivate(artifact);

        // then
        assertThat(existing.isActive()).isFalse();
        assertThat(current.isActive()).isTrue();
        assertThat(current.isReady()).isTrue();
        assertThat(current).isSameAs(artifact);

        BDDMockito.then(seatLayoutArtifactRepository).should()
            .findActiveArtifact(concertId, concertSequenceId, section);
        BDDMockito.then(seatLayoutArtifactRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void 현재_active가_자기_자신이면_비활성화없이_ready_active_상태를_유지한다() {
        // given
        UUID artifactId = UUID.randomUUID();
        UUID concertId = UUID.randomUUID();
        UUID concertSequenceId = UUID.randomUUID();
        Section section = Section.A;
        String hash = "hash";
        String schemaVersion = "schemaVersion";

        SeatLayoutArtifact existing = seatLayoutArtifact(
            artifactId, concertId, concertSequenceId, section, hash, schemaVersion, true
        );

        BDDMockito.given(seatLayoutArtifactRepository.findActiveArtifact(
                    concertId, concertSequenceId, section
                )
            )
            .willReturn(Optional.of(existing));

        // when
        SeatLayoutArtifact current = seatLayoutArtifactService.markReadyAndActivate(existing);

        // then
        assertThat(existing.isReady()).isTrue();
        assertThat(existing.isActive()).isTrue();
        assertThat(current).isSameAs(existing);

        BDDMockito.then(seatLayoutArtifactRepository).should()
            .findActiveArtifact(concertId, concertSequenceId, section);
        BDDMockito.then(seatLayoutArtifactRepository).shouldHaveNoMoreInteractions();
    }

    private SeatLayoutArtifact seatLayoutArtifact(
        UUID artifactId,
        UUID concertId,
        UUID concertSequenceId,
        Section section,
        String hash,
        String schemaVersion,
        boolean activeAndReady
    ) {
        return SeatLayoutArtifact.builder()
            .id(artifactId)
            .concertId(concertId)
            .concertSequenceId(concertSequenceId)
            .section(section)
            .hash(hash)
            .schemaVersion(schemaVersion)
            .cdnUrl(DEFAULT_CDN_URL)
            .storageKey(DEFAULT_STORAGE_KEY)
            .isActive(activeAndReady)
            .isReady(activeAndReady)
            .build();
    }


}