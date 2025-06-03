package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.RequestDTO;
import com.example.ztpai.controller.RequestController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.RequestService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @MockBean
    private JWTFilter jwtFilter;

    private final Long validUserId = 1L;
    private final Long validFriendId = 2L;
    private final Long invalidUserId = 999L;

    @Nested
    @DisplayName("GET /api/request/get/{user_id}")
    class GetRequests {

        @Test
        @DisplayName("200 - Successfully retrieve friend requests")
        void shouldReturnFriendRequests() throws Exception {

            List<RequestDTO> requestList = Arrays.asList(
                    new RequestDTO(2L, "requestUser1"),
                    new RequestDTO(3L, "requestUser2")
            );
            when(requestService.getRequest(validUserId)).thenReturn(requestList);


            mockMvc.perform(get("/api/request/get/{user_id}", validUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].username").value("requestUser1"))
                    .andExpect(jsonPath("$[1].id").value(3))
                    .andExpect(jsonPath("$[1].username").value("requestUser2"));

            verify(requestService).getRequest(validUserId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            when(requestService.getRequest(invalidUserId)).thenThrow(
                    new GlobalExceptions.UserNotFoundException("User not found"));


            mockMvc.perform(get("/api/request/get/{user_id}", invalidUserId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(requestService).getRequest(invalidUserId);
        }
    }

    @Nested
    @DisplayName("POST /api/request/accept/{user_id}/{friend_id}")
    class AcceptRequest {

        @Test
        @DisplayName("201 - Successfully accept friend request")
        void shouldAcceptFriendRequest() throws Exception {

            doNothing().when(requestService).acceptRequest(validUserId, validFriendId);


            mockMvc.perform(post("/api/request/accept/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully accepted request"));

            verify(requestService).acceptRequest(validUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(requestService).acceptRequest(invalidUserId, validFriendId);


            mockMvc.perform(post("/api/request/accept/{user_id}/{friend_id}", invalidUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(requestService).acceptRequest(invalidUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - Request not found")
        void shouldReturn404WhenRequestNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("Request not found"))
                    .when(requestService).acceptRequest(validUserId, validFriendId);


            mockMvc.perform(post("/api/request/accept/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Request not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(requestService).acceptRequest(validUserId, validFriendId);
        }
    }

    @Nested
    @DisplayName("POST /api/request/reject/{user_id}/{friend_id}")
    class RejectRequest {

        @Test
        @DisplayName("201 - Successfully reject friend request")
        void shouldRejectFriendRequest() throws Exception {

            doNothing().when(requestService).rejectRequest(validUserId, validFriendId);


            mockMvc.perform(post("/api/request/reject/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully rejected request"));

            verify(requestService).rejectRequest(validUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(requestService).rejectRequest(invalidUserId, validFriendId);


            mockMvc.perform(post("/api/request/reject/{user_id}/{friend_id}", invalidUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(requestService).rejectRequest(invalidUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - Request not found")
        void shouldReturn404WhenRequestNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("Request not found"))
                    .when(requestService).rejectRequest(validUserId, validFriendId);


            mockMvc.perform(post("/api/request/reject/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Request not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(requestService).rejectRequest(validUserId, validFriendId);
        }
    }
}
