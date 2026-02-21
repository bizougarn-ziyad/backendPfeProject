package com.projection.repository;

import com.projection.entity.list.ListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListItemRepository extends JpaRepository<ListItem, UUID> {

    @Query("SELECT li FROM ListItem li WHERE li.userList.id = :listId")
    List<ListItem> findByListId(@Param("listId") UUID listId);

    @Query("SELECT li FROM ListItem li WHERE li.userList.id = :listId AND li.contentReference.tmdbId = :tmdbId AND li.contentReference.contentType = :contentType")
    Optional<ListItem> findByListIdAndContent(@Param("listId") UUID listId, @Param("tmdbId") Long tmdbId,
            @Param("contentType") com.projection.entity.enums.ContentType contentType);

    @Query("SELECT CASE WHEN COUNT(li) > 0 THEN true ELSE false END FROM ListItem li WHERE li.userList.id = :listId AND li.contentReference.tmdbId = :tmdbId AND li.contentReference.contentType = :contentType")
    boolean existsByListIdAndContent(@Param("listId") UUID listId, @Param("tmdbId") Long tmdbId,
            @Param("contentType") com.projection.entity.enums.ContentType contentType);
}
