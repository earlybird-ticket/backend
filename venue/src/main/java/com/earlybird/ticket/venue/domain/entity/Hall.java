package com.earlybird.ticket.venue.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_hall")
@SQLRestriction(("deleted_at is null"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hall extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
}
