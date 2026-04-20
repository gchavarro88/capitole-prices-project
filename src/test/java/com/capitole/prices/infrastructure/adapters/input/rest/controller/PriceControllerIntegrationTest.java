package com.capitole.prices.infrastructure.adapters.input.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class PriceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPriceWhenRangeIsOnThePriorityZeroAndPriceListOneTest() throws Exception {
        // Test 1: request 10:00 day 14 productId 35455 brand 1 (ZARA)
        // Expected: Price 35.50 EUR (PriceList 1, Priority 0)

        mockMvc.perform(get("/price")
                        .param("applicationDate", "2020-06-14T10:00:00+00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void getPriceWhenRangeIsOnThePriorityOneAndPriceListTwoTest() throws Exception {
        // Test 2: request 16:00 day 14 productId 35455 brand 1 (ZARA)
        // Expected: Price 25.45 EUR (PriceList 2, Priority 1) - overlapping period with higher priority

        mockMvc.perform(get("/price")
                        .param("applicationDate", "2020-06-14T16:00:00+00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void getPriceWhenRangeIsOnThePriorityZeroAndPriceListOnePromotionEndsTest() throws Exception {
        // Test 3: request 21:00 day 14 productId 35455 brand 1 (ZARA)
        // Expected: Price 35.50 EUR (PriceList 1, Priority 0) - back to base price after promotion ends

        mockMvc.perform(get("/price")
                        .param("applicationDate", "2020-06-14T21:00:00+00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void getPriceWhenRangeIsOnThePriorityOneAndPriceListTwoMorningPromotionTest() throws Exception {
        // Test 4: request 10:00 del día 15 productId 35455  brand 1 (ZARA)
        // Expected: Price 30.50 EUR (PriceList 3, Priority 1) - morning promotion on 15th

        mockMvc.perform(get("/price")
                        .param("applicationDate", "2020-06-15T10:00:00+00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }

    @Test
    void getPriceWhenRangeIsOnThePriorityOneAndPriceListThreeLastPromotionTest() throws Exception {
        // Test 5: request 21:00 day 16 productId 35455 brand 1 (ZARA)
        // Expected: Price 38.95 EUR (PriceList 4, Priority 1) - new price effective from 16th afternoon

        mockMvc.perform(get("/price")
                        .param("applicationDate", "2020-06-16T21:00:00+00:00")
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95))
                .andExpect(jsonPath("$.currency").value("EUR"));
    }
}
