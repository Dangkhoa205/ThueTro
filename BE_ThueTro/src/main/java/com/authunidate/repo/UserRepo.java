package com.authunidate.repo;

import com.authunidate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    User findUserByEmail(String email);

    @Query(value = """
            SELECT TOP 1 r.Code
            FROM UserRoles ur
            JOIN Roles r ON ur.RoleId = r.RoleId
            WHERE ur.UserId = :userId
            ORDER BY ur.RoleId
            """, nativeQuery = true)
    String findRoleCodeByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT COUNT(*) FROM UserRoles WHERE UserId = :userId AND RoleId = :roleId", nativeQuery = true)
    int countUserRole(@Param("userId") Long userId, @Param("roleId") Short roleId);

    @Modifying
    @Query(value = "INSERT INTO UserRoles(UserId, RoleId) VALUES (:userId, :roleId)", nativeQuery = true)
    void addRoleToUser(@Param("userId") Long userId, @Param("roleId") Short roleId);
}
