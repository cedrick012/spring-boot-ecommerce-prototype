package com.example.marketplace.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.marketplace.service.CartService;
import com.example.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Mock beans from other controllers to satisfy context creation
    @MockBean
    private ProductService productService;

    @MockBean
    private CartService cartService;

    @Test
    void home_shouldRedirectToIndexHtml() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/index.html"));
    }

    @Test
    void favicon_shouldReturnNoContent() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
            .andExpect(status().isNoContent());
    }
}
