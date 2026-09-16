package com.novabank.novabank_registration.customer.entity;

import jakarta.persistence.*;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_customers_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_customers_mobile_number",
                        columnNames = "mobile_number"
                )
        }
)

@Getter
@Setter
public class Customer extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(
            name = "first_name",
            nullable = false,
            length = 50
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false,
            length = 50
    )
    private String lastName;

    @Column(
            name = "email",
            nullable = false
    )
    private String email;

    @Column(
            name = "mobile_number",
            nullable = false,
            length = 10
    )
    private String mobileNumber;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false
    )
    private CustomerStatus status =
            CustomerStatus.PENDING_VERIFICATION;


    public Customer() {
    }
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "customer_roles",
            joinColumns = @JoinColumn(
                    name = "customer_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"
            )
    )
    private Set<Role> roles = new HashSet<>();
}