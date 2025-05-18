import React, { useEffect, useRef, useState } from "react";
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
    const [currentUser, setCurrentUser] = useState(null);
    const selectedFriendRef = useRef(null);

    useEffect(() => {
        selectedFriendRef.current = selectedFriend;
    }, [selectedFriend]);


    useEffect(() => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        setCurrentUser({
            id: decoded.userID,
            username: decoded.sub
        });


        axios.get(`http://localhost:8080/api/friend/get/${decoded.userID}`, {
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
        if (selectedFriend && currentUser) {
            const token = localStorage.getItem("token");
            axios.get(`http://localhost:8080/api/chat/get/${currentUser.id}/${selectedFriend.id}`, {
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
    }, [selectedFriend, currentUser]);


    useEffect(() => {
        if (!currentUser) return;

        const token = localStorage.getItem("token");
        const socket = new SockJS("http://localhost:8080/ws");
        const client = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            debug: (str) => {
                console.log('STOMP: ' + str);
            },
        });

        client.onConnect = () => {
            console.log('Connected to WebSocket');

            client.subscribe(`/queue/messages/${currentUser.id}`, (message) => {
                const selected = selectedFriendRef.current;
                const receivedMessage = JSON.parse(message.body);
                if (selected && receivedMessage.sender.id == selected.id) {
                    setMessages(prev => [...prev, receivedMessage]);
                }
            });
        };

        client.onStompError = (frame) => {
            console.error('STOMP error', frame);
        };

        clientRef.current = client;
        client.activate();

        return () => {
            if (client.connected) {
                client.deactivate();
            }
        };
    }, [currentUser]);

    const sendMessage = () => {
        if (!messageText.trim() || !selectedFriend || !currentUser || !clientRef.current?.connected) return;

        const messageDTO = {
            content: messageText,
            sentAt: new Date(),
            sender: {
                id: currentUser.id,
                username: currentUser.username
            },
            receiver: {
                id: selectedFriend.id,
                username: selectedFriend.username
            }
        };

        clientRef.current.publish({
            destination: "/app/chat",
            body: JSON.stringify(messageDTO)
        });


        setMessages(prev => [...prev, messageDTO]);
        setMessageText("");
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
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
                            className={`friend-item ${selectedFriend?.id === friend.id ? 'selected' : ''}`}
                            onClick={() => handleFriendClick(friend)}
                        >
                            <div className="friend-avatar"></div>
                            <div className="friend-name">{friend.username}</div>
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
                                <div
                                    key={idx}
                                    className={`message ${msg.sender.id === currentUser?.id ? 'sent' : 'received'}`}
                                >
                                    <div className="message-content">
                                        <p>{msg.content}</p>
                                        <span className="message-time">
                                            {new Date(msg.sentAt).toLocaleTimeString()}
                                        </span>
                                    </div>
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
                                onKeyPress={handleKeyPress}
                            />
                            <button
                                className="chat-send-button"
                                onClick={sendMessage}
                                disabled={!messageText.trim()}
                            >
                                ➤
                            </button>
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
