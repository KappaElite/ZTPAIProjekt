import React, { useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import "./MainChatPage.css";

function MainChatPage() {
    const [friends, setFriends] = useState([]);
    const [selectedFriend, setSelectedFriend] = useState(null); // State for selected friend

    useEffect(() => {
        const token = localStorage.getItem("token");
        const userId = jwtDecode(token).userID;

        axios.get(`http://localhost:8080/api/friend/get/${userId}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((response) => {
                setFriends(response.data);
            })
            .catch((error) => {
                console.error("Error fetching friends:", error);
            });
    }, []);

    const handleFriendClick = (friend) => {
        setSelectedFriend(friend);
    };

    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Messages</div>
                <div className="friend-list">
                    {friends.map((friend) => (
                        <div
                            key={friend.id}
                            className="friend-item"
                            onClick={() => handleFriendClick(friend)}
                        >
                            <div className="friend-avatar"></div>
                            <div className="friend-name">{friend.username}</div>
                            <div className="delete-icon">🗑️</div>
                        </div>
                    ))}
                </div>
            </div>
            <div className="chat-panel">
                {selectedFriend ? (
                    <>
                        <div className="chat-header">
                            <div className="chat-header-title">
                                {selectedFriend.username}
                            </div>
                        </div>
                        <div className="chat-messages">
                            {}
                        </div>
                        <div className="chat-input-container">
                            <input
                                type="text"
                                className="chat-input"
                                placeholder="Type a message..."
                            />
                            <button className="chat-send-button">➤</button>
                        </div>
                    </>
                ) : (
                    <div className="chat-placeholder">
                        Select a friend to start chatting
                    </div>
                )}
            </div>
        </div>
    );
}

export default MainChatPage;