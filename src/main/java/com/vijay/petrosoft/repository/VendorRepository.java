package com.vijay.petrosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vijay.petrosoft.domain.Vendor;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    boolean existsByName(String name);
    Optional<Vendor> findByName(String name);
}
