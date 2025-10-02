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
            .andExpect(jsonPath("$.message", is("1つ以上の項目で入力内容の検証に失敗しました。")))
            .andExpect(jsonPath("$.details.productId", is("商品IDはNULLにできません。")))
            .andExpect(jsonPath("$.details.quantity", is("数量は1以上である必要があります。")));
    }

    @Test
    void handleIllegalArgumentException_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test-exceptions/illegal-argument"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("無効な引数が提供されました。")));
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/test-exceptions/not-found"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.message", is("テスト用リソースが見つかりません。")));
    }

    @Test
    void handleMethodArgumentTypeMismatch_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/test-exceptions/type-mismatch/{id}", "not-a-uuid"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.message", is("商品IDの形式が無効です。有効なUUIDを指定してください。")));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() throws Exception {
        mockMvc.perform(get("/test-exceptions/generic"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.status", is(500)))
            .andExpect(jsonPath("$.message", is("予期せぬエラーが発生しました。")))
            .andExpect(jsonPath("$.details", is("一般的なエラーが発生しました。")));
    }
}
