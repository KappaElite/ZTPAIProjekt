package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.controller.FriendsController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.FriendsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendsController.class)
@Import({SecurityConfig.class, JWTUtil.class})
@AutoConfigureMockMvc(addFilters = false)
public class FriendsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FriendsService friendsService;

    @MockBean
    private JWTFilter jwtFilter;

    private final Long validUserId = 1L;
    private final Long validFriendId = 2L;
    private final Long invalidUserId = 999L;

    @Nested
    @DisplayName("GET /api/friend/get/{user_id}")
    class GetFriends {

        @Test
        @DisplayName("200 - Successfully retrieve friends list")
        void shouldReturnFriendsList() throws Exception {

            List<FriendsDTO> friendsList = Arrays.asList(
                    new FriendsDTO("friendUser1", "friend1@example.com", 2L),
                    new FriendsDTO("friendUser2", "friend2@example.com", 3L)
            );
            when(friendsService.getFriends(validUserId)).thenReturn(friendsList);


            mockMvc.perform(get("/api/friend/get/{user_id}", validUserId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(2))
                    .andExpect(jsonPath("$[0].username").value("friendUser1"))
                    .andExpect(jsonPath("$[0].email").value("friend1@example.com"))
                    .andExpect(jsonPath("$[1].id").value(3))
                    .andExpect(jsonPath("$[1].username").value("friendUser2"))
                    .andExpect(jsonPath("$[1].email").value("friend2@example.com"));

            verify(friendsService).getFriends(validUserId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            when(friendsService.getFriends(invalidUserId)).thenThrow(
                    new GlobalExceptions.UserNotFoundException("User not found"));


            mockMvc.perform(get("/api/friend/get/{user_id}", invalidUserId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(friendsService).getFriends(invalidUserId);
        }
    }

    @Nested
    @DisplayName("POST /api/friend/add/{user_id}/{friend_id}")
    class AddFriend {

        @Test
        @DisplayName("201 - Successfully add friend")
        void shouldAddFriend() throws Exception {

            doNothing().when(friendsService).AddFriend(validUserId, validFriendId);


            mockMvc.perform(post("/api/friend/add/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully add friend"));

            verify(friendsService).AddFriend(validUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(friendsService).AddFriend(invalidUserId, validFriendId);


            mockMvc.perform(post("/api/friend/add/{user_id}/{friend_id}", invalidUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(friendsService).AddFriend(invalidUserId, validFriendId);
        }

        @Test
        @DisplayName("409 - Friendship already exists")
        void shouldReturn409WhenFriendshipExists() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("Friendship already exists"))
                    .when(friendsService).AddFriend(validUserId, validFriendId);


            mockMvc.perform(post("/api/friend/add/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Friendship already exists"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(friendsService).AddFriend(validUserId, validFriendId);
        }
    }

    @Nested
    @DisplayName("DELETE /api/friend/delete/{user_id}/{friend_id}")
    class RemoveFriend {

        @Test
        @DisplayName("201 - Successfully remove friend")
        void shouldRemoveFriend() throws Exception {

            doNothing().when(friendsService).RemoveFriend(validUserId, validFriendId);


            mockMvc.perform(delete("/api/friend/delete/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isCreated())
                    .andExpect(content().string("Successfully remove friend"));

            verify(friendsService).RemoveFriend(validUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - User not found")
        void shouldReturn404WhenUserNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(friendsService).RemoveFriend(invalidUserId, validFriendId);


            mockMvc.perform(delete("/api/friend/delete/{user_id}/{friend_id}", invalidUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(friendsService).RemoveFriend(invalidUserId, validFriendId);
        }

        @Test
        @DisplayName("404 - Friendship not found")
        void shouldReturn404WhenFriendshipNotFound() throws Exception {

            doThrow(new GlobalExceptions.UserNotFoundException("Friendship not found"))
                    .when(friendsService).RemoveFriend(validUserId, validFriendId);


            mockMvc.perform(delete("/api/friend/delete/{user_id}/{friend_id}", validUserId, validFriendId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Friendship not found"))
                    .andExpect(jsonPath("$.status").value(404));

            verify(friendsService).RemoveFriend(validUserId, validFriendId);
        }
    }
}
