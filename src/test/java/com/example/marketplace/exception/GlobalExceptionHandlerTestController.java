package com.example.marketplace.exception;

import com.example.marketplace.dto.AddToCartRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A dummy controller for testing the GlobalExceptionHandler.
 * This controller is only active within the test scope.
 */
@RestController
@RequestMapping("/test-exceptions")
public class GlobalExceptionHandlerTestController {

    @PostMapping("/validation")
    public void triggerValidationException(@Valid @RequestBody AddToCartRequest request) {
        // This method will trigger MethodArgumentNotValidException if the request is invalid
    }

    @GetMapping("/illegal-argument")
    public void triggerIllegalArgumentException() {
        throw new IllegalArgumentException("Illegal argument provided");
    }

    @GetMapping("/not-found")
    public void triggerNotFoundException() {
        throw new NotFoundException("Test resource not found");
    }

    @GetMapping("/type-mismatch/{id}")
    public void triggerTypeMismatchException(@PathVariable("id") UUID id) {
        // This will trigger MethodArgumentTypeMismatchException if a non-UUID is passed
    }

    @GetMapping("/generic")
    public void triggerGenericException() throws Exception {
        throw new Exception("A generic error occurred");
    }
}
