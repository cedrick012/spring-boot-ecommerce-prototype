# Spring Boot E-Commerce Marketplace - Presentation Speaker Notes

## Introduction (1 minute)
• Welcome to our Spring Boot E-Commerce Marketplace demonstration
• This is a lightweight EC API designed for small to medium-scale online businesses
• Features session-based shopping carts, product catalog, and checkout functionality
• Built with Spring Boot, MyBatis, and H2 database for educational and PoC purposes

## Project Structure Overview (1 minute)
• Open the project structure in Eclipse
• Navigate to `src/main/java/com/example/marketplace`
• Show the layered architecture: controller, service, entity, mapper, dto, exception packages
• Point out the clean separation of concerns following Spring Boot best practices

## Entity Layer - Domain Models (1 minute)
• Open `src/main/java/com/example/marketplace/entity/Product.java`
• Here's our Product entity with validation annotations
• Notice `@NotBlank` for name validation, `@Min` for price and stock constraints
• Open `src/main/java/com/example/marketplace/entity/Cart.java`
• Cart uses `@JsonManagedReference` to handle circular reference with CartItem
• Open `src/main/java/com/example/marketplace/entity/CartItem.java`
• CartItem uses `@JsonBackReference` to complete the bidirectional relationship

## Data Access Layer - MyBatis Integration (1 minute)
• Navigate to `src/main/resources/mappers/ProductMapper.xml`
• This shows our MyBatis XML mapping for product operations
• Notice the clean SQL queries with parameter binding using `#{}`
• Open `src/main/resources/mappers/CartItemMapper.xml`
• Complex JOIN query mapping CartItem with Product data
• The `resultMap` handles the object relationship mapping

## Service Layer - Business Logic (1.5 minutes)
• Open `src/main/java/com/example/marketplace/service/ProductServiceImpl.java`
• The `reduceStock` method includes validation and stock checking logic
• Open `src/main/java/com/example/marketplace/service/CartServiceImpl.java`
• `addProductToCart` method handles both new items and quantity updates
• The `checkout` method demonstrates transaction handling with comprehensive error checking
• Notice how we validate stock availability before processing the order

## Controller Layer - REST API (1 minute)
• Open `src/main/java/com/example/marketplace/controller/ProductController.java`
• Simple REST endpoints for product listing and details
• Open `src/main/java/com/example/marketplace/controller/CartController.java`
• Session-based cart management using `HttpSession`
• Notice the `/session` endpoints that automatically create carts per user session

## Exception Handling (0.5 minutes)
• Open `src/main/java/com/example/marketplace/exception/GlobalExceptionHandler.java`
• Centralized error handling using `@RestControllerAdvice`
• Consistent error response format across the application
• Different exception types return appropriate HTTP status codes

## Frontend Integration (1 minute)
• Navigate to `src/main/resources/static/index.html`
• Simple HTML interface consuming our REST APIs
• Open `src/main/resources/static/app.js`
• JavaScript class handling API calls and UI updates
• Session-based cart functionality working seamlessly with backend

## Running the Application (1 minute)
• Right-click on `MarketplaceApplication.java` in Eclipse
• Select "Run As" → "Spring boot app"
• Application starts on port 8080 with embedded H2 database
• Open browser to `http://localhost:8080`
• Demonstrate adding products to cart and checkout functionality

## Key Features Demonstrated
• **Product Management**: Validation, stock tracking
• **Session-based Carts**: Automatic cart creation per browser session  
• **Inventory Control**: Stock validation during add-to-cart and checkout
• **Error Handling**: Graceful failure with user-friendly messages
• **MyBatis Integration**: Clean SQL mapping with complex joins
• **RESTful Design**: Standard HTTP methods and status codes

## Conclusion (0.5 minutes)
• This marketplace demonstrates enterprise-grade Spring Boot practices
• Scalable architecture ready for authentication and payment integration
• Perfect foundation for learning modern Java web development