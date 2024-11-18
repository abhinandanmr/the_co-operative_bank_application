package com.abhi.the_bank_app.repository;

import com.abhi.the_bank_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email); // Note: `existsByEmail`, not `existByEmail`

    Boolean existsByAccountNumber(String accountNumber); // Corrected method name

    User findByAccountNumber(String accountNumber);
}