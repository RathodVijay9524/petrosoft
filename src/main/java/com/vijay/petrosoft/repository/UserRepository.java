package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByPumpId(Long pumpId);
}
