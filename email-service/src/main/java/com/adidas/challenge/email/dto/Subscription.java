package com.adidas.challenge.email.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Subscription {

    private String id;
    private String email;
    private String firstName;
    private String gender;
    private LocalDate dateOfBirth;
    private Boolean consent;
    private LocalDateTime createdAt;

}
