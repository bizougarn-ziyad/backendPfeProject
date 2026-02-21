package com.projection.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projection.entity.content.ContentReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_favorites", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_content_favorite", columnNames = { "user_id", "content_reference_id" })
}, indexes = {
        @Index(name = "idx_user_favorites_user_id", columnList = "user_id"),
        @Index(name = "idx_user_favorites_content_id", columnList = "content_reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFavorite {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_favorites_user"))
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_reference_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_favorites_content"))
    private ContentReference contentReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
