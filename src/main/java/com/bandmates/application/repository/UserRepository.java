package com.bandmates.application.repository;

import com.bandmates.application.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser> {
    AppUser findByUsername(String username);
    AppUser findByEmailRegistrationToken(String emailRegistrationToken);
}
