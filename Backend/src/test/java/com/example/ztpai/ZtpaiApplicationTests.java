package com.example.ztpai;

import com.example.ztpai.model.User;
import com.example.ztpai.repository.UserRepository;
import com.example.ztpai.security.SecurityConfig;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ZtpaiApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private static String authToken;
    private static String refreshToken;
    private static Long testUserId;
    private static Long friendUserId;
    private static Long requestUserId;


    @Test
    @Order(1)
    void testRegisterUser() {

        String requestBody = """
        {
            "username": "testuser",
            "password": "testpass",
            "email": "test@example.com"
        }
        """;


        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(requestBody, createJsonHeaders()),
                String.class
        );


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Register success", response.getBody());


        User user = userRepository.findByUsername("testuser").orElse(null);
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        testUserId = user.getId();
    }

    @Test
    @Order(2)
    void testLoginUser() {

        String requestBody = """
        {
            "username": "testuser",
            "password": "testpass"
        }
        """;


        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/login",
                new HttpEntity<>(requestBody, createJsonHeaders()),
                Map.class
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));


        authToken = (String) response.getBody().get("token");
        refreshToken = (String) response.getBody().get("refreshToken");
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);
        return headers;
    }

    @Test
    @Order(3)
    @Transactional
    void testAddFriend() {

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                new HttpEntity<>("""
                {
                    "username": "frienduser",
                    "password": "friendpass",
                    "email": "friend@example.com"
                }
                """, createJsonHeaders()),
                String.class
        );

        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        User friendUser = userRepository.findByUsername("frienduser").orElseThrow();
        friendUserId = friendUser.getId();

        HttpHeaders headers = createAuthHeaders();


        ResponseEntity<String> addResponse = restTemplate.exchange(
                "/api/friend/add/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                testUser.getId(),
                friendUser.getId()
        );

        assertEquals(HttpStatus.CREATED, addResponse.getStatusCode());
        assertEquals("Successfully add friend", addResponse.getBody());


        ResponseEntity<String> acceptResponse = restTemplate.exchange(
                "/api/request/accept/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                friendUser.getId(),
                testUser.getId()
        );

        assertEquals(HttpStatus.CREATED, acceptResponse.getStatusCode());


        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(1, updatedUser.getFriends().size());
    }

    @Test
    @Order(4)
    void testGetFriends() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();

        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/friend/get/{user_id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                testUser.getId()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @Order(5)
    void testSendMessage() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        User friendUser = userRepository.findByUsername("frienduser").orElseThrow();

        HttpHeaders headers = createAuthHeaders();

        String requestBody = """
        {
            "content": "Hello friend!"
        }
        """;

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/chat/new/{sender_id}/{receiver_id}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class,
                testUser.getId(),
                friendUser.getId()
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Message added successfully", response.getBody());
    }

    @Test
    @Order(6)
    void testGetMessages() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        User friendUser = userRepository.findByUsername("frienduser").orElseThrow();

        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/chat/get/{sender_id}/{receiver_id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                testUser.getId(),
                friendUser.getId()
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @Order(7)
    void testAddFriendToYourself() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();

        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/friend/add/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                Map.class,
                testUser.getId(),
                testUser.getId()
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("You can't add yourself", response.getBody().get("message"));
    }

    @Test
    @Order(8)
    void testSendEmptyMessage() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();
        User friendUser = userRepository.findByUsername("frienduser").orElseThrow();

        HttpHeaders headers = createAuthHeaders();

        String requestBody = """
        {
            "content": ""
        }
        """;

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/chat/new/{sender_id}/{receiver_id}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class,
                testUser.getId(),
                friendUser.getId()
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Message cannot be empty", response.getBody().get("message"));
    }

    @Test
    @Order(9)
    void testRegisterDuplicateUsername() {
        String requestBody = """
        {
            "username": "testuser",
            "password": "testpass",
            "email": "different@example.com"
        }
        """;

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(requestBody, createJsonHeaders()),
                Map.class
        );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

    }

    @Test
    @Order(10)
    void testRegisterUserForRequest() {
        String requestBody = """
        {
            "username": "requestuser",
            "password": "requestpass",
            "email": "request@example.com"
        }
        """;

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                new HttpEntity<>(requestBody, createJsonHeaders()),
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        User requestUser = userRepository.findByUsername("requestuser").orElse(null);
        assertNotNull(requestUser);
        requestUserId = requestUser.getId();
    }

    @Test
    @Order(11)
    void testSendFriendRequest() {
        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/friend/add/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                requestUserId,
                testUserId
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully add friend", response.getBody());
    }

    @Test
    @Order(12)
    void testGetPendingRequests() {
        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/request/get/{user_id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                testUserId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    @Order(13)
    void testAcceptFriendRequest() {
        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/request/accept/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                testUserId,
                requestUserId
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully accepted request", response.getBody());
    }

    @Test
    @Order(14)
    void testRemoveFriend() {
        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/friend/delete/{user_id}/{friend_id}",
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class,
                testUserId,
                friendUserId
        );


        assertEquals(HttpStatus.CREATED, response.getStatusCode());


        ResponseEntity<List> friendsResponse = restTemplate.exchange(
                "/api/friend/get/{user_id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                testUserId
        );

        List<Map<String, Object>> friendsList = friendsResponse.getBody();
        boolean friendRemoved = true;
        for (Map<String, Object> friend : friendsList) {
            if (friendUserId.equals(Long.valueOf(friend.get("id").toString()))) {
                friendRemoved = false;
                break;
            }
        }
        assertTrue(friendRemoved, "Friend should have been removed from friends list");
    }

    @Test
    @Order(15)
    void testGetUserList() {
        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/userlist/get/{user_id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                testUserId
        );


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(16)
    void testRejectFriendRequest() {

        HttpHeaders headers = createAuthHeaders();


        restTemplate.exchange(
                "/api/friend/add/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                requestUserId,
                testUserId
        );


        ResponseEntity<String> response = restTemplate.exchange(
                "/api/request/reject/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                testUserId,
                requestUserId
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully rejected request", response.getBody());
    }

    @Test
    @Order(17)
    void testTokenRefresh() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", refreshToken);

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/auth/refresh/{userID}",
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                Map.class,
                testUserId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("token"));


    }

    @Test
    @Order(18)
    void testHealthEndpoint() {

        HttpHeaders headers = createAuthHeaders();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/hello",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hello, World!", response.getBody());
    }
}
