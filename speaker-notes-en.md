# Spring Boot E-Commerce Marketplace - Condensed Speaker Script

## Introduction (45 seconds)

"Welcome to this tutorial. Today I'll demonstrate our Spring Boot E-Commerce Marketplace, showcasing modern Java enterprise practices."

## Project Structure Overview (45 seconds)

"Let's examine our Eclipse project structure. Navigate to `src/main/java/com/example/marketplace`. Notice our layered architecture: controller for REST endpoints, service for business logic, entity for domain models, mapper for database operations, dto for data transfer, and exception for error handling. This separation ensures maintainability and scalability."

## Entity Layer - Domain Models (1 minute)

"Opening `src/main/java/com/example/marketplace/entity/Product.java`, observe validation annotations like `@NotBlank` for names and `@Min` for positive prices and stock values. The `src/main/java/com/example/marketplace/entity/Cart.java` entity uses `@JsonManagedReference` for bidirectional relationships. `src/main/java/com/example/marketplace/entity/CartItem.java` completes this pattern with `@JsonBackReference`, preventing circular reference issues during JSON serialization."

## Data Access Layer - MyBatis Integration (45 seconds)

"In `src/main/resources/mappers/ProductMapper.xml`, we implement clean SQL with parameter binding using hash syntax. The `src/main/resources/mappers/CartItemMapper.xml` demonstrates complex JOIN operations combining cart items with product data. MyBatis automatically handles object relationship construction."

## Service Layer - Business Logic (1 minute)

"The `src/main/java/com/example/marketplace/service/ProductServiceImpl.java` `reduceStock` method validates quantity, verifies product existence, and checks stock availability. In `src/main/java/com/example/marketplace/service/CartServiceImpl.java`, `addProductToCart` handles new items and quantity updates with stock validation. The `checkout` method ensures transaction consistency by validating all items before processing."

## Controller Layer - REST API (45 seconds)

"`src/main/java/com/example/marketplace/controller/ProductController.java` provides standard REST endpoints with proper HTTP status codes. `src/main/java/com/example/marketplace/controller/CartController.java` implements session-based cart management using `HttpSession`, automatically creating carts per user session without requiring authentication."

## Exception Handling (30 seconds)

"`src/main/java/com/example/marketplace/exception/GlobalExceptionHandler.java` uses `@RestControllerAdvice` for centralized error handling, returning consistent error responses with appropriate HTTP status codes."

## Frontend Integration (45 seconds)

"The `src/main/resources/static/index.html` provides our user interface, while `src/main/resources/static/app.js` contains the MarketplaceApp class handling API calls and session management. The frontend seamlessly integrates with our session-based backend."

## Application Demonstration (1 minute)

"Right-click `src/main/java/com/example/marketplace/MarketplaceApplication.java`, select 'Run As Spring Boot App'. The server starts on port 8080. Opening `localhost:8080` shows our product catalog. I'll add items to demonstrate real-time cart updates and stock validation.

## Key Features Summary (30 seconds)

"This application demonstrates enterprise practices: Bean Validation for input checking, centralized error handling, inventory control with stock validation, session-based state management, standard REST API design, and MyBatis database integration."

## Conclusion (30 seconds)

"This Spring Boot marketplace showcases development practices with a scalable architecture suitable for extending with authentication, payments, and advanced inventory features. Thank you for your attention."