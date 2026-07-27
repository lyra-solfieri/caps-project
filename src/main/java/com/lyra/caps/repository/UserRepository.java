package com.lyra.caps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.lyra.caps.model.User;
import com.lyra.caps.model.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);

    Optional<User> findByRole(Role role);

}
