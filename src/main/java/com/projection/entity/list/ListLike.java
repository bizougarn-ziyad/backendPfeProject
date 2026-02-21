package com.projection.entity.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projection.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "list_likes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_list_like", columnNames = { "user_id", "list_id" })
}, indexes = {
        @Index(name = "idx_list_likes_user_id", columnList = "user_id"),
        @Index(name = "idx_list_likes_list_id", columnList = "list_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListLike {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_list_likes_user"))
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false, foreignKey = @ForeignKey(name = "fk_list_likes_list"))
    @JsonIgnore
    private UserList userList;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
