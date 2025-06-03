package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.UserDTO;
import com.example.ztpai.controller.UserListController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.UserListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserListController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class UserListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserListService userListService;

    @MockBean
    private JWTFilter jwtFilter;

    private final Long validUserId = 1L;
    private final Long invalidUserId = 999L;

    @Nested
    @DisplayName("GET /api/userlist/get/{user_id}")
    class GetUsers {

        @Test
        @DisplayName("200 - Successfully retrieve available users")
        void shouldReturnAvailableUsers() throws Exception {

            List<UserDTO> userList = Arrays.asList(
                    new UserDTO(2L, "user1"),
                    new UserDTO(3L, "user2"),
                    new UserDTO(4L, "user3")
            );
            when(userListService.getAllUsers(validUserId)).thenReturn(userList);


            mockMvc.perform(get("/api/userlist/get/{user_id}", validUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].username").value("user1"))
                    .andExpect(jsonPath("$[1].id").value(3))
                    .andExpect(jsonPath("$[1].username").value("user2"))
                    .andExpect(jsonPath("$[2].id").value(4))
                    .andExpect(jsonPath("$[2].username").value("user3"));

            verify(userListService).getAllUsers(validUserId);
        }

        @Test
        @DisplayName("200 - Empty list when no users available")
        void shouldReturnEmptyListWhenNoUsersAvailable() throws Exception {

            List<UserDTO> emptyList = List.of();
            when(userListService.getAllUsers(validUserId)).thenReturn(emptyList);


            mockMvc.perform(get("/api/userlist/get/{user_id}", validUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(userListService).getAllUsers(validUserId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            when(userListService.getAllUsers(invalidUserId))
                    .thenThrow(new GlobalExceptions.UserNotFoundException("User not found"));


            mockMvc.perform(get("/api/userlist/get/{user_id}", invalidUserId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(userListService).getAllUsers(invalidUserId);
        }
    }
}
