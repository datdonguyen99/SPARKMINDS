package net.sparkminds.librarymanagement.controller.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sparkminds.librarymanagement.config.TestConfig;
import net.sparkminds.librarymanagement.payload.request.RegisterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(classes = {TestConfig.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterDto registerDto;


    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto("Username Test", "testemail159@gmail.com",
                "Testpass@159", "0785623296");
    }

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/common/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }
}
