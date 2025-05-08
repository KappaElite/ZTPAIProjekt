import React from "react";
import "./MainChatPage.css";

function MainChatPage() {
    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Messages</div>
                <div className="friend-list">
                    <div className="friend-item">
                        <div className="friend-avatar"></div>
                        <div className="friend-name">Krzysztof Nowak</div>
                        <div className="delete-icon">🗑️</div>
                    </div>
                    <div className="friend-item">
                        <div className="friend-avatar"></div>
                        <div className="friend-name">Jan Kowalski</div>
                        <div className="delete-icon">🗑️</div>
                    </div>
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