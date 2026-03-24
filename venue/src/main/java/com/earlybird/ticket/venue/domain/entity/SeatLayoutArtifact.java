package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.venue.domain.entity.constant.Section;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Builder
@Entity
@Getter
@Table(name = "p_seat_layout_artifact")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatLayoutArtifact extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "concert_sequence_id", nullable = false)
    private UUID concertSequenceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Section section;

    @Column(name = "cdn_url", columnDefinition = "text", nullable = false)
    private String cdnUrl;

    @Column(name = "storage_key", columnDefinition = "text", nullable = false)
    private String storageKey;

    @Column(name = "hash", columnDefinition = "text", nullable = false)
    private String hash;

    @Column(name = "schema_version", nullable = false, length = 50)
    private String schemaVersion;

    @Column(name = "is_active", nullable = false)
    private boolean isActive; // 현재 공연 회차에서 참조중인지 여부

    @Column(name = "is_ready", nullable = false)
    private boolean isReady; // 현재 사용가능한 상태인지 여부


    public void activate() {
        if (!isReady) {
            throw new IllegalStateException("준비되지 않은 artifact는 활성화할 수 없습니다.");
        }
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void markReady() {
        this.isReady = true;
    }

    public void markNotReady() {
        this.isReady = false;
        this.isActive = false;
    }

}
