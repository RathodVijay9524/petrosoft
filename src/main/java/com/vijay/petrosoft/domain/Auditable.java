package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class Auditable {
    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "created_at")
    protected LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;
}
