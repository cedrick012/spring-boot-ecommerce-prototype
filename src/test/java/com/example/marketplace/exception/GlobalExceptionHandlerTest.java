package com.example.marketplace.exception;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = GlobalExceptionHandlerTestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleValidationExceptions_shouldReturnBadRequest() throws Exception {
        String invalidRequest = "{\"quantity\": 0}"; // Missing productId, invalid quantity

        mockMvc.perform(post("/test-exceptions/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Validation failed for one or more fields.")))
            .andExpect(jsonPath("$.details.productId", is("Product id can not be null")))
            .andExpect(jsonPath("$.details.quantity", is("Quantity must be at least 1")));
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test-exceptions/illegal-argument"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Illegal argument provided")));
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test-exceptions/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.message", is("Test resource not found")));
    }

    @Test
    void handleMethodArgumentTypeMismatch_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test-exceptions/type-mismatch/{id}", "not-a-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("Invalid product ID format. Please provide a valid UUID.")));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test-exceptions/generic"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status", is(500)))
            .andExpect(jsonPath("$.message", is("An unexpected error occurred.")))
            .andExpect(jsonPath("$.details", is("A generic error occurred")));
    }
}
