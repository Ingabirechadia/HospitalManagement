package com.example.HMS.repository;

import com.example.HMS.entity.User;
import com.example.HMS.enums.AuthProvider;
import com.example.HMS.enums.UserRole;
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


    boolean existsByEmail(String email);


    @Query("SELECT u FROM User u WHERE u.providerId = :providerId AND u.authProvider = :authProvider")
    Optional<User> findByProviderIdAndAuthProvider(@Param("providerId") String providerId,
                                                   @Param("authProvider") AuthProvider authProvider);


    @Query("SELECT u FROM User u WHERE u.email = :email AND u.authProvider = :authProvider")
    Optional<User> findByEmailAndAuthProvider(@Param("email") String email,
                                              @Param("authProvider") AuthProvider authProvider);


    List<User> findByRole(UserRole role);


    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllEnabled();


    List<User> findByRoleAndEnabled(UserRole role, boolean enabled);


    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE u.providerId = :providerId AND u.authProvider = :authProvider")
    boolean existsByProviderIdAndAuthProvider(@Param("providerId") String providerId,
                                              @Param("authProvider") AuthProvider authProvider);


    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);


    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") UserRole role);


    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
}