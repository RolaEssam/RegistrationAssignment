package com.novabank.novabank_registration.customer.repository;


import com.novabank.novabank_registration.customer.entity.Role;
import com.novabank.novabank_registration.customer.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
