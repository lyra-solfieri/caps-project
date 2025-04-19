package com.lyra.caps.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lyra.caps.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
