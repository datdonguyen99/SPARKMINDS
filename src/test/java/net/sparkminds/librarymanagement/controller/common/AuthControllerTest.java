package net.sparkminds.librarymanagement.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sparkminds.librarymanagement.config.TestConfig;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.request.TokenRefreshDto;
import net.sparkminds.librarymanagement.repository.SessionRepository;
import net.sparkminds.librarymanagement.repository.UserRepository;
import net.sparkminds.librarymanagement.security.JwtTokenProvider;
import net.sparkminds.librarymanagement.util.ExcelToDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.UUID;

@SpringBootTest(classes = {TestConfig.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ExcelToDatabase excelToDatabase;

    private static final String filePath = "src/test/resources/data/data_test_junit5.xlsx";

    @BeforeEach
    void setUp() throws IOException {
        excelToDatabase.readExcelFile(filePath, 0, "accounts");
        excelToDatabase.readExcelFile(filePath, 1, "users");
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteById(77L);
        userRepository.deleteById(88L);
        userRepository.deleteById(99L);
    }

    @Test
    @Order(1)
    void testLogin() throws Exception {
        LoginDto loginDto1 = new LoginDto("datdonguyen33@gmail.com", "Donguyendat@99", "");
        LoginDto loginDto2 = new LoginDto("datdonguyen22@yopmail.com", "Donguyendat@99", "");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/common/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto1)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isString())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/common/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto2)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isString())
                .andReturn();
    }

    @Test
    @Order(2)
    void testRefreshToken() throws Exception {
        String jti = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = jwtTokenProvider.generateJwtRefreshTokenFromEmail("datdonguyen33@gmail.com", jti);
        TokenRefreshDto tokenRefreshDto = new TokenRefreshDto();
        tokenRefreshDto.setRefreshToken(refreshToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/common/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRefreshDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isString())
                .andReturn();
    }
}
