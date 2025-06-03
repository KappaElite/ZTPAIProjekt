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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

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
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @Order(3)
    @Transactional
    void testAddFriend() {

        restTemplate.postForEntity(
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


        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/friend/add/{user_id}/{friend_id}",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class,
                testUser.getId(),
                friendUser.getId()
        );


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Successfully add friend", response.getBody());


        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertEquals(1, updatedUser.getFriends().size());
    }

    @Test
    @Order(4)
    void testGetFriends() {
        User testUser = userRepository.findByUsername("testuser").orElseThrow();

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

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

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

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

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

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

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

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

        HttpHeaders headers = createJsonHeaders();
        headers.setBearerAuth(authToken);

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
}

