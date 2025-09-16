package com.vijay.petrosoft.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType;

    private String description;

    public enum RoleType {
        OWNER("Owner", "Full system access and control"),
        MANAGER("Manager", "Management level access with reporting"),
        OPERATOR("Operator", "Operational access for daily tasks"),
        ACCOUNTANT("Accountant", "Financial and accounting access"),
        SUPPORT("Support", "Customer support and maintenance access");

        private final String displayName;
        private final String description;

        RoleType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }
}
