package com.projection.entity.list;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projection.entity.content.ContentReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "list_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_list_content", columnNames = { "list_id", "content_reference_id" })
}, indexes = {
        @Index(name = "idx_list_items_list_id", columnList = "list_id"),
        @Index(name = "idx_list_items_content_id", columnList = "content_reference_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false, foreignKey = @ForeignKey(name = "fk_list_items_list"))
    @JsonIgnore
    private UserList userList;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_reference_id", nullable = false, foreignKey = @ForeignKey(name = "fk_list_items_content"))
    private ContentReference contentReference;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
