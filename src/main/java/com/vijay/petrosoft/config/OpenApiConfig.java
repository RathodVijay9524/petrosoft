package com.vijay.petrosoft.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for comprehensive API documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.petrosoft.com")
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
                )
                .tags(List.of(
                        new Tag().name("Authentication").description("User authentication and authorization endpoints"),
                        new Tag().name("User Management").description("User CRUD operations and management"),
                        new Tag().name("Role Management").description("Role-based access control"),
                        new Tag().name("Sales Management").description("Sales transactions and operations"),
                        new Tag().name("Cash Management").description("Cash flow and movement tracking"),
                        new Tag().name("Shift Management").description("Shift operations and tracking"),
                        new Tag().name("Customer Management").description("Customer information and operations"),
                        new Tag().name("Supplier Management").description("Supplier information and operations"),
                        new Tag().name("Inventory Management").description("Inventory tracking and management"),
                        new Tag().name("Fuel Management").description("Fuel types and pricing"),
                        new Tag().name("Voucher Management").description("Financial vouchers and entries"),
                        new Tag().name("Financial Reports").description("Financial reporting and analytics"),
                        new Tag().name("Dashboard").description("Real-time dashboard and metrics"),
                        new Tag().name("Master Setup").description("System configuration and setup"),
                        new Tag().name("Notifications").description("Email and SMS notifications"),
                        new Tag().name("Payment Gateway").description("Payment processing and integration"),
                        new Tag().name("Subscription Management").description("Subscription plans and billing"),
                        new Tag().name("Purchase Management").description("Purchase orders and bills")
                ));
    }
    
    private Info apiInfo() {
        return new Info()
                .title("Petrosoft Management System API")
                .description("""
                        ## Comprehensive Petrol Pump Management System API
                        
                        This API provides complete functionality for managing a petrol pump business including:
                        
                        ### Core Features:
                        - **User Management**: Complete user lifecycle with role-based access control
                        - **Sales Operations**: Fuel sales, transactions, and reporting
                        - **Cash Management**: Cash flow tracking and reconciliation
                        - **Inventory Management**: Fuel stock and supplier management
                        - **Financial Management**: Vouchers, reports, and accounting
                        - **Shift Management**: Employee shifts and operations
                        - **Customer Management**: Customer information and credit management
                        - **Notification System**: Email and SMS alerts
                        - **Payment Integration**: Razorpay payment gateway
                        - **Subscription Management**: Plan management and billing
                        
                        ### Security:
                        - JWT-based authentication
                        - Role-based authorization (OWNER, MANAGER, OPERATOR, ACCOUNTANT, SUPPORT)
                        - Comprehensive audit logging
                        - Rate limiting and security monitoring
                        
                        ### Documentation:
                        - All endpoints are documented with examples
                        - Request/response schemas are provided
                        - Error codes and messages are documented
                        - Authentication requirements are specified
                        
                        ### Support:
                        For technical support or questions, please contact the development team.
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("Petrosoft Development Team")
                        .email("support@petrosoft.com")
                        .url("https://petrosoft.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
    
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("""
                        JWT Authentication
                        
                        To authenticate API requests, include the JWT token in the Authorization header:
                        
                        ```
                        Authorization: Bearer <your-jwt-token>
                        ```
                        
                        You can obtain a JWT token by calling the `/api/auth/login` endpoint with valid credentials.
                        
                        **Token Expiration**: Tokens expire after 24 hours. Use the refresh token endpoint to get a new token.
                        
                        **Role-based Access**: Different endpoints require different roles. Check the endpoint documentation for required permissions.
                        """);
    }
}
