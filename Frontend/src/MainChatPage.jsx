import React, { useEffect, useRef, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { Client } from '@stomp/stompjs';
import { useNavigate} from "react-router-dom";
import SockJS from 'sockjs-client';
import axios from "axios";
import deleteIcon from "./assets/delete.png";
import "./MainChatPage.css";


export const fetchWithAuthRetry = async (axiosConfig, navigate) => {
    const token = localStorage.getItem("token");

    try {
        const response = await axios({
            ...axiosConfig,
            headers: {
                ...(axiosConfig.headers || {}),
                Authorization: `Bearer ${token}`,
            },
        });
        return response;
    } catch (error) {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
            try {
                const refreshToken = localStorage.getItem("refreshToken");
                const decoded = jwtDecode(refreshToken);
                const userID = decoded.userID;

                const refreshResponse = await axios.post(`http://localhost:8080/api/auth/refresh/${userID}`, {
                    token: refreshToken,
                });

                const newToken = refreshResponse.data.token;
                localStorage.setItem("token", newToken);

                const retryResponse = await axios({
                    ...axiosConfig,
                    headers: {
                        ...(axiosConfig.headers || {}),
                        Authorization: `Bearer ${newToken}`,
                    },
                });

                return retryResponse;

            } catch (refreshError) {
                console.error("Token refresh failed", refreshError);
                localStorage.clear();
                navigate("/login");
            }
        } else {
            throw error;
        }
    }
};

function MainChatPage() {
    const [friends, setFriends] = useState([]);
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [messages, setMessages] = useState([]);
    const [messageText, setMessageText] = useState("");
    const clientRef = useRef(null);
    const [currentUser, setCurrentUser] = useState(null);
    const selectedFriendRef = useRef(null);
    const navigate = useNavigate();
    const containerRef = useRef(null);
    const userToken = localStorage.getItem("token");
    const decodedToken = jwtDecode(userToken);
    const role = decodedToken.role;



    useEffect( () => {
        if(containerRef.current && containerRef){
            const element = containerRef.current;
            element.scroll({
                behavior: "smooth",
                left:0,
                top:element.scrollHeight
            })
        }
    }, [messages]);


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

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/friend/get/${decoded.userID}`,
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
            .then((response) => {
                setFriends(response.data);
            })
            .catch((error) => {
                console.error("Error fetching friends:", error);
            });
    }, []);

    useEffect(() => {
        if (selectedFriend === "GroupChat" && currentUser) {
            fetchWithAuthRetry({
                url: `http://localhost:8080/api/chat/groupchat/get`,
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }, navigate)
                .then((response) => {
                    setMessages(response.data);
                })
                .catch((error) => {
                    console.error("Error fetching group chat:", error);
                });
        } else if (selectedFriend && currentUser) {
            fetchWithAuthRetry({
                url: `http://localhost:8080/api/chat/get/${currentUser.id}/${selectedFriend.id}`,
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            }, navigate)
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
        });

        client.onConnect = () => {
            console.log('Connected to WebSocket');

            client.subscribe(`/queue/messages/${currentUser.id}`, (message) => {
                const selected = selectedFriendRef.current;
                const receivedMessage = JSON.parse(message.body);
                if (selected && (receivedMessage.sender.id === selected.id || receivedMessage.receiver.id === selected.id)) {
                    setMessages(prev => [...prev, receivedMessage]);
                }
            });

            client.subscribe(`/topic/groupchat`, (message) => {
                const receivedMessage = JSON.parse(message.body);
                if (selectedFriendRef.current === "GroupChat") {
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


        if( selectedFriend === "GroupChat" && currentUser) {
            const groupMessageDTO  = {
                content: messageText,
                sender: {
                    id: currentUser.id,
                    username: currentUser.username
                },
                sentAt: new Date(),
            }
            clientRef.current.publish({
                destination: "/app/groupChat",
                body: JSON.stringify(groupMessageDTO)
            });
            setMessageText("");
        }
        else{
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



            setMessageText("");
        }

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

    const handleGroupChatClick = () => {
        setSelectedFriend("GroupChat");
        setMessages([]);
    }

    const handleAddFriendClick = () => {
        navigate('/add-friend');
    }

    const handleRemoveFriendClick = (friendToRemove) => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userID = decoded.userID;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/friend/delete/${userID}/${friendToRemove.id}`,
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
            .then(() => {
                setFriends(prevFriends =>
                    prevFriends.filter(friend => friend.id !== friendToRemove.id)
                );

                if (selectedFriend?.id === friendToRemove.id) {
                    setSelectedFriend(null);
                    setMessages([]);
                }
            })
            .catch((error) => {
                console.error("Error deleting friend", error);
            });
    };

    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Messages</div>
                <div className="friend-list">
                    {friends.map((friend) => {
                        const isSelected = selectedFriend?.id === friend.id;
                        return (
                            <div
                                key={friend.id}
                                className={`friend-item ${isSelected ? 'selected' : ''}`}
                                onClick={!isSelected ? () => handleFriendClick(friend) : undefined}
                                style={{ cursor: isSelected ? 'default' : 'pointer' }}
                            >
                                <div className="friend-name">{friend.username}</div>
                                <button className="delete-friend-button" style={{ background: 'none', border: 'none', padding: 0, marginLeft: '10px' }}
                                onClick={(e) =>{
                                    e.stopPropagation();
                                    handleRemoveFriendClick(friend)
                                }}>
                                    <img src={deleteIcon} alt="Delete" style={{ width: '18px', height: '18px' }} />

                                </button>
                            </div>
                        );
                    })}
                    {role==='SUPERUSER' && (
                        <div
                        className={`friend-item ${selectedFriend === "GroupChat" ? 'selected' : ''}`}
                        onClick={selectedFriend !== "GroupChat" ? handleGroupChatClick : undefined}
                        style={{ cursor: selectedFriend === "GroupChat" ? 'default' : 'pointer' }}
                    >
                        <div className="friend-name">Group chat</div>
                    </div>
                        )}
                </div>
                <button className="add-friend-button" onClick={handleAddFriendClick}>
                    Add Friend
                </button>
                <button className="add-friend-button" onClick={() => navigate('/notification')}>
                    Notifications
                </button>
                {role==='ADMIN' && (
                    <button className="add-friend-button" onClick={()=> navigate('/admin')} >
                        Admin Panel
                    </button>
                )}
            </div>
            <div className="chat-panel">
                {selectedFriend ? (
                    <>
                        <div className="chat-header">
                            <div className="chat-header-title">
                                {selectedFriend === "GroupChat" ? "Group Chat" : selectedFriend.username}
                            </div>
                        </div>
                        <div  ref={containerRef} className="chat-messages">
                            {messages.map((msg, idx) => (
                                <div
                                    key={idx}
                                    className={`message ${msg.sender.id === currentUser?.id ? 'sent' : 'received'}`}
                                >
                                    <div className="message-sender">{msg.sender.username}</div>
                                    <div className="message-date">
                                        {new Date(msg.sentAt).toLocaleString('pl-PL', { dateStyle: 'short', timeStyle: 'short' })}
                                    </div>
                                    <div className="message-bubble">
                                        {msg.content}
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



