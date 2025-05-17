import React, {useEffect, useRef, useState} from "react";
import { jwtDecode } from "jwt-decode";
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from "axios";
import "./MainChatPage.css";

function MainChatPage() {
    const [friends, setFriends] = useState([]);
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [messages, setMessages] = useState([]);
    const [messageText, setMessageText] = useState("");
    const clientRef = useRef(null);

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

    useEffect(() => {
        if(selectedFriend) {
            const token = localStorage.getItem("token");
            const userId = jwtDecode(token).userID;
            const friendID = selectedFriend ? selectedFriend.id : null;
            axios.get(`http://localhost:8080/api/chat/get/${userId}/${friendID}`, {
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            })
                .then((response) => {
                    setMessages(response.data);
                })
                .catch((error) => {
                    console.error("Error fetching messages:", error);
                });
        }
        }, [selectedFriend]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const username = jwtDecode(token).sub;
        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            onConnect: () => {
                client.subscribe("/user/queue/messages", (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    if (selectedFriend && receivedMessage.sender.id === selectedFriend.id) {
                        setMessages(prev => [...prev, receivedMessage]);
                    }
                });
            },
        });
        client.activate();
        clientRef.current = client;

        return () => {
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, [selectedFriend]);

    const sendMessage = () => {
        const token = localStorage.getItem("token");
        const username = jwtDecode(token).sub;

        const message = {
            content: messageText,
            receiver: {
                id: selectedFriend.id,
                username: selectedFriend.username
            }
        };

        clientRef.current.publish({
            destination: "/api/chat/send",
            body: JSON.stringify(message),
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });

        setMessages(prev => [...prev, {
            content: messageText,
            sender: { username }
        }]);

        setMessageText("");
    };



    const handleFriendClick = (friend) => {
        setSelectedFriend(friend);
        setMessages([]);
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
                            {messages.map((msg, idx) => (
                                <div key={idx} className="chat-message">
                                    <strong>{msg.sender.username}:</strong> {msg.content}
                                </div>
                            ))}
                        </div>
                        <div className="chat-input-container">
                            <input
                                type="text"
                                className="chat-input"
                                placeholder="Type a message..."
                                value={messageText}
                                onChange={(e) => setMessageText(e.target.value)}
                            />
                            <button className="chat-send-button" onClick={sendMessage}>➤</button>
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