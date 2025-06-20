package com.example.ztpai.repository;

import com.example.ztpai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u JOIN u.friends f " +
            "WHERE u.id = :userId AND f.id = :friendId")
    boolean existsFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("SELECT u FROM User u WHERE u.id <> :userID and u.id NOT IN" +
    "(Select fr.receiver.id FROM FriendRequest fr WHERE fr.sender.id = :userID)" +
            "and u.id not in (select f.id from User user join user.friends f where user.id = :userID)")
    List<User> findAllUsersExceptCurrentAndFriends(@Param("userID") Long userID);

}

