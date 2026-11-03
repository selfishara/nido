package com.nido.backend.money.infrastructure.web;

import com.nido.backend.money.infrastructure.config.MoneyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentRatioController.class)
@Import(MoneyConfig.class)
class RentRatioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void ac1_returnsTheRatioJson() throws Exception{
        mockMvc.perform(post("/api/v1/money/rent-ratio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "netMonthlyIncome": 1500.00, "rentShare": 450.00 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").value(30.0))
                .andExpect(jsonPath("$.level").value("AMBER"))
                .andExpect(jsonPath("$.healthyMaxRent").value(449))
                .andExpect(jsonPath("$.rentExceedsIncome").value(false));
    }
}
