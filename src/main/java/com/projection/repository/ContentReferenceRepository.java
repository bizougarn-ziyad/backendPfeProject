package com.projection.repository;

import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContentReferenceRepository extends JpaRepository<ContentReference, UUID> {

    Optional<ContentReference> findByTmdbIdAndContentType(Long tmdbId, ContentType contentType);

    boolean existsByTmdbIdAndContentType(Long tmdbId, ContentType contentType);
}
