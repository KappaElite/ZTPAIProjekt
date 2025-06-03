package com.example.ztpai.ControrellersTests;

import com.example.ztpai.controller.HelloController;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTFilter jwtFilter;

    @Test
    @DisplayName("GET /api/hello - Health check returns 200 OK")
    void healthCheckEndpointReturnsExpectedMessage() throws Exception {

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }
}
