package com.andrei.plesoianu.sbecom.repositories;

import com.andrei.plesoianu.sbecom.enums.AppRole;
import com.andrei.plesoianu.sbecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
