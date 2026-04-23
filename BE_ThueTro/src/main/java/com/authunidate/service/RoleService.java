package com.authunidate.service;

import com.authunidate.entity.Role;
import com.authunidate.repo.RoleRepo;
import com.authunidate.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;

    public String getRoleCodeByUserId(Long userId) {
        String roleCode = userRepo.findRoleCodeByUserId(userId);
        return (roleCode == null || roleCode.isBlank()) ? ROLE_USER : roleCode;
    }

    @Transactional
    public void ensureUserHasRole(Long userId, String roleCode) {
        Role role = ensureRole(roleCode);
        if (userRepo.countUserRole(userId, role.getRoleId()) == 0) {
            userRepo.addRoleToUser(userId, role.getRoleId());
        }
    }

    @Transactional
    public Role ensureRole(String roleCode) {
        return roleRepo.findByCode(roleCode).orElseGet(() -> {
            return roleRepo.save(Role.builder()
                    .code(roleCode)
                    .name(roleCode)
                    .build());
        });
    }
}
