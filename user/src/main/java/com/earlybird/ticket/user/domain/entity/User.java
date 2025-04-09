package com.earlybird.ticket.user.domain.entity;

import com.earlybird.ticket.common.entity.BaseEntity;
import com.earlybird.ticket.common.entity.constant.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@SuperBuilder
@Table(name = "p_user")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
@DiscriminatorValue("USER") // TODO: 추후 USER도 하위 엔티티로 분리
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is null")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    // 상속받기 때문에 데이터가 변경될 수 X
    @Column(name = "role", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    /*
    TODO: 고도화때 등급 부활
    private Grade grade;
     */

    public void updateCustomerWithoutPassword(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.birthDay = user.getBirthDay();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhoneNumber();
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}

