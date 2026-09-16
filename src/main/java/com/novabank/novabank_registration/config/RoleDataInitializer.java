package com.novabank.novabank_registration.config;


import com.novabank.novabank_registration.customer.entity.Role;
import com.novabank.novabank_registration.customer.entity.RoleName;
import com.novabank.novabank_registration.customer.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleDataInitializer {

    @Bean
    CommandLineRunner initializeRoles(
            RoleRepository roleRepository) {

        return args -> {

            for (RoleName roleName
                    : RoleName.values()) {

                if (roleRepository
                        .findByName(roleName)
                        .isEmpty()) {

                    roleRepository.save(
                            new Role(roleName)
                    );
                }
            }
        };
    }
}
