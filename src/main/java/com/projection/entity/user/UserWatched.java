package com.projection.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projection.entity.content.ContentReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_watched", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_content_watched", columnNames = { "user_id", "content_reference_id" })
}, indexes = {
        @Index(name = "idx_user_watched_user_id", columnList = "user_id"),
        @Index(name = "idx_user_watched_content_id", columnList = "content_reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWatched {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_watched_user"))
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_reference_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_watched_content"))
    private ContentReference contentReference;

    @Column(name = "watched_at", nullable = false, updatable = false)
    private LocalDateTime watchedAt;

    @PrePersist
    protected void onCreate() {
        watchedAt = LocalDateTime.now();
    }
}
