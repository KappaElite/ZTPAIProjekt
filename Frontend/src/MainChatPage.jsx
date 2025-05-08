import React from "react";
import "./MainChatPage.css";
import {useEffect, useState} from "react";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

function MainChatPage() {

    const [friends, setFriends] = useState([]);
    useEffect(() => {

        const userId = jwtDecode(localStorage.getItem('token')).userID;
        const token = localStorage.getItem('token');
        axios.get(`http://localhost:8080/api/friend/get/${userId}`, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
            .then((response) => {
                setFriends(response.data);
            })
            .catch((error) => {
                console.error("Error fetching friends:", error);
            });
    }, []);
    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Messages</div>
                <div className="friend-list">
                    {friends.map((friend) => (
                        <div key={friend.id} className="friend-item">
                            <div className="friend-avatar"></div>
                            <div className="friend-name">{friend.username}</div>
                            <div className="delete-icon">🗑️</div>
                        </div>
                    ))}
                </div>
            </div>
            <div className="chat-panel">
                <div className="chat-header">
                    <div className="chat-header-title">Jan Kowalski</div>
                </div>
                <div className="chat-messages">
                    <div className="message received">
                        <div className="message-bubble">Hej, chcesz się spotkać?</div>
                    </div>
                    <div className="message received">
                        <div className="message-bubble">Pasuje ci czwartek o godzinie 14?</div>
                    </div>
                    <div className="message sent">
                        <div className="message-bubble">Z wielką chęcią</div>
                    </div>
                </div>
                <div className="chat-input-container">
                    <input
                        type="text"
                        className="chat-input"
                        placeholder="Type a message..."
                    />
                    <button className="chat-send-button">➤</button>
                </div>
            </div>
        </div>
    );
}

export default MainChatPage;