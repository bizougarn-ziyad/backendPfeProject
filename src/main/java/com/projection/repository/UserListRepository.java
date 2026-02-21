package com.projection.repository;

import com.projection.entity.list.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserListRepository extends JpaRepository<UserList, UUID> {

    @Query("SELECT COUNT(ul) FROM UserList ul WHERE ul.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT ul FROM UserList ul WHERE ul.user.id = :userId")
    List<UserList> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ul FROM UserList ul WHERE ul.user.id = :userId AND ul.name = :name")
    Optional<UserList> findByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);

    @Query("SELECT ul FROM UserList ul WHERE ul.user.id = :userId AND ul.isDefault = true AND ul.name = :name")
    Optional<UserList> findByUserIdAndDefaultName(@Param("userId") Long userId, @Param("name") String name);
}
