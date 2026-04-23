package com.authunidate.repo;

import com.authunidate.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Short> {
    Optional<Role> findByCode(String code);
}
