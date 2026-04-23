package com.authunidate.repo;

import com.authunidate.entity.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserOtpRepo extends JpaRepository<UserOtp, Long> {
    @Modifying
    @Query("DELETE FROM UserOtp o WHERE o.user.id = :userId")
    void deleteAllByUserID(@Param("userId") long userId);

    @Query("""
            select o from UserOtp o
            where o.user.id = :userId
              and o.otpCode = :code
              and o.used = false
              and o.expiredAt > :now
            """)
    Optional<UserOtp> findValidOtp(@Param("userId") long userId,
                                   @Param("code") String otpCode,
                                   @Param("now") LocalDateTime now);
}
