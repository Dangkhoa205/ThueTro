package com.authunidate.config;

import com.authunidate.entity.User;
import com.authunidate.repo.UserRepo;
import com.authunidate.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Bean
    public ApplicationRunner init(UserRepo userRepo) {
        return args -> {
            roleService.ensureRole(RoleService.ROLE_ADMIN);
            roleService.ensureRole(RoleService.ROLE_USER);

            if (userRepo.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .fullName("ADMIN")
                        .passwordHash(passwordEncoder.encode("admin123"))
                        .active(true)
                        .build();
                admin = userRepo.save(admin);
                roleService.ensureUserHasRole(admin.getId(), RoleService.ROLE_ADMIN);
                log.info("Default admin account has been initialized.");
            } else {
                User admin = userRepo.findByEmail("admin@gmail.com").orElseThrow();
                roleService.ensureUserHasRole(admin.getId(), RoleService.ROLE_ADMIN);
            }
        };
    }
}
