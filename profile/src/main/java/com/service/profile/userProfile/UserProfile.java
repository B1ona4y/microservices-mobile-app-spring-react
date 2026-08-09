package com.service.profile.userProfile;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "user_profile")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    // Совпадает с JWT subject, который oauth2 выдаёт как строковый id пользователя.
    // Тот же тип, что owner в notebook (SyncableEntity), — единый идентификатор владельца.
    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must be at most 255 characters")
    @Pattern(regexp = "^[\\p{L}\\p{N} _\\-.,!?()]+$", message = "name contains invalid characters")
    @Column(nullable = false)
    private String displayName;

    private String bio;

    private String avatarUrl;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        this.updatedAt = Instant.now();
    }
}
