package com.example.ztpai.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String role;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<Message> receivedMessages;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<User> friends;

    public User() {}

    public User(String username, String passwordHash, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.email = email;
    }

    public String getUsername() {return username;}
    public String getPassword() {return passwordHash;}
    public String getRole() {return role;}
    public String getEmail() {return email;}
    public List<User> getFriends() {return friends;}
    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.passwordHash = password;}
    public void setId(Long id) {this.id = id;}
    public long getId() {return id;}
}
