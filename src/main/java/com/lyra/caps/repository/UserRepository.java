package com.lyra.caps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.lyra.caps.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    UserDetails findByEmail(String email);

}
