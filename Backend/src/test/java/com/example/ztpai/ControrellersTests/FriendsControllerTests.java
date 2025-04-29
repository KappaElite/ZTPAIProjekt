package com.example.ztpai.ControrellersTests;

import com.example.ztpai.DTO.FriendsDTO;
import com.example.ztpai.controller.FriendsController;
import com.example.ztpai.exception.GlobalExceptions;
import com.example.ztpai.exception.friend.FriendExceptions;
import com.example.ztpai.security.JWTFilter;
import com.example.ztpai.security.JWTUtil;
import com.example.ztpai.security.SecurityConfig;
import com.example.ztpai.service.AuthService;
import com.example.ztpai.service.FriendsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendsController.class)
@Import({SecurityConfig.class, JWTUtil.class, JWTFilter.class})
@AutoConfigureMockMvc(addFilters = false)
class FriendsControllerTest {

    @Autowired
    private MockMvc mvc;

    @Mock
    private FriendsService friendsService;


    @Nested
    @DisplayName("POST /api/friend/add/{user}/{friend}")
    class AddFriend {

        @Test
        @DisplayName("201 – proper friend add")
        void addFriend_created() throws Exception {
            mvc.perform(post("/api/friend/add/1/2"))
                    .andExpect(status().isCreated());
            verify(friendsService).AddFriend(1L, 2L);
        }

        @Test
        @DisplayName("400 - adding yourself")
        void addFriend_yourself() throws Exception {
            doThrow(new FriendExceptions.AddingYourselfException("You cant add yourself"))
                .when(friendsService).AddFriend(1L, 1L);
            mvc.perform(post("/api/friend/add/1/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("You cant add yourself"))
                    .andExpect(jsonPath("$.status").value("400"));
        }

        @Test
        @DisplayName("400 – id is not a number")
        void addFriend_badRequest() throws Exception {
            mvc.perform(post("/api/friend/add/foo/2"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 – user doesnt exist")
        void addFriend_userNotFound() throws Exception {
            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(friendsService).AddFriend(99L, 2L);
            mvc.perform(post("/api/friend/add/99/2"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("409 – relationship exists")
        void addFriend_alreadyExists() throws Exception {
            doThrow(new FriendExceptions.FriendshipAlreadyExistsException("Friendship already exists"))
                    .when(friendsService).AddFriend(1L, 2L);
            mvc.perform(post("/api/friend/add/1/2"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Friendship already exists"))
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    @DisplayName("DELETE /api/friend/delete/{user}/{friend}")
    class RemoveFriend {

        @Test
        @DisplayName("201 – proper deleting")
        void deleteFriend_ok() throws Exception {
            mvc.perform(delete("/api/friend/delete/1/2"))
                    .andExpect(status().isCreated());
            verify(friendsService).RemoveFriend(1L, 2L);
        }

        @Test
        @DisplayName("400 - deleting yourself")
        void deleteFriend_yourself() throws Exception {
            doThrow(new FriendExceptions.DeletingYourselfException("You cant delete yourself"))
                    .when(friendsService).RemoveFriend(1L, 1L);
            mvc.perform(delete("/api/friend/delete/1/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("You cant delete yourself"))
                    .andExpect(jsonPath("$.status").value("400"));
        }


        @Test
        @DisplayName("410 – relationship already deleted")
        void deleteFriend_notFound() throws Exception {
            doThrow(new FriendExceptions.FriendshipAlreadyDeletedException("Friendship already deleted"))
                    .when(friendsService).RemoveFriend(1L, 2L);
            mvc.perform(delete("/api/friend/delete/1/2"))
                    .andExpect(status().isGone())
                    .andExpect(jsonPath("$.message").value("Friendship already deleted"))
                    .andExpect(jsonPath("$.status").value(410));
        }
    }

    @Nested
    @DisplayName("GET /api/friend/get/{user}")
    class GetFriends {

        @Test
        @DisplayName("200 – getting list of friends")
        void getFriends_ok() throws Exception {
            when(friendsService.getFriends(1L))
                    .thenReturn(List.of(new FriendsDTO("Jan", "jan.kowalski@gmail.com")));

            mvc.perform(get("/api/friend/get/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].username").value("Jan"))
                    .andExpect(jsonPath("$[0].email").value("jan.kowalski@gmail.com"));
        }

        @Test
        @DisplayName("400 – id is not a number")
        void getFriends_badRequest() throws Exception {
            mvc.perform(get("/api/friend/get/foo"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("404 – user doesnt exist")
        void getFriends_notFound() throws Exception {
            doThrow(new GlobalExceptions.UserNotFoundException("User not found"))
                    .when(friendsService).getFriends(99L);

            mvc.perform(get("/api/friend/get/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"))
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}