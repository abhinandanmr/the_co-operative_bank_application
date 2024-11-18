package com.abhi.the_bank_app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private  String firstName;
    private  String lastName;
    private  String otherName;
    private  String gender;
    private  String address;
    private  String stateOfOrigin;
    private  String accountNumber;
    private BigDecimal accountBalance;
    private  String email;
    private  String phoneNumber;
    private  String alternativePhoneNumber;
    private  String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


}