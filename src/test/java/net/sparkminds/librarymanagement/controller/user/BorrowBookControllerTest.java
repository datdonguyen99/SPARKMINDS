package net.sparkminds.librarymanagement.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sparkminds.librarymanagement.config.TestConfig;
import net.sparkminds.librarymanagement.payload.request.BorrowBookDto;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.service.AuthService;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {TestConfig.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
class BorrowBookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Order(1)
    void testSearchBorrowBooks() throws Exception {
        final int numberOfBookExpected = 2;
        LoginDto loginDto = new LoginDto("datdonguyen99@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/borrow-books")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParams(new LinkedMultiValueMap<>() {{
                            add("title.contains", "the");
                            add("category.equals", "FICTION");
                        }}))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JSONArray books = new JSONArray(content);

        Assertions.assertEquals(numberOfBookExpected, books.length());
    }

    @Test
    @Order(2)
    void testBorrowBooks() throws Exception {
        LoginDto loginDto = new LoginDto("datdonguyen99@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        List<BorrowBookDto> listBookDtoRequest = new ArrayList<>();
        listBookDtoRequest.add(new BorrowBookDto(5L));
        listBookDtoRequest.add(new BorrowBookDto(14L));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/borrow-books")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(listBookDtoRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
