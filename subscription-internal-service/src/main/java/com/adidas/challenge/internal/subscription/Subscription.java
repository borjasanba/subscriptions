package com.adidas.challenge.internal.subscription;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
public class Subscription {

    @Id
    private String id;

    @Column(nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    private String gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private Boolean consent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
