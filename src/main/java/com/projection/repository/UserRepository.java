package com.projection.repository;

import com.projection.entity.user.User;
import com.projection.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.bio) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "ORDER BY u.username ASC")
    List<User> searchUsers(@Param("query") String query);

    // Admin queries
    long countByRole(Role role);

    long countByIsActiveTrue();

    long countByIsSuspendedTrue();

    long countByIsActiveFalse();

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    long countByCreatedAtAfter(@Param("since") LocalDateTime since);

    List<User> findAllByOrderByCreatedAtDesc();

    /** Returns [country, count] ordered by count desc. Nulls/blanks are excluded. */
    @Query("SELECT u.country, COUNT(u) FROM User u WHERE u.country IS NOT NULL AND u.country <> '' GROUP BY u.country ORDER BY COUNT(u) DESC")
    List<Object[]> countByCountry();

    /** Returns users created on or after the given date, for timeline grouping in Java. */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt ASC")
    List<User> findCreatedSince(@Param("since") LocalDateTime since);
}

