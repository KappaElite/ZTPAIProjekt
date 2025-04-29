import { useState } from "react";
import axios from "axios";
import "./TestPage.css"

function TestPage() {
    const [senderId, setSenderId] = useState("");
    const [receiverId, setReceiverId] = useState("");
    const [messageContent, setMessageContent] = useState("");
    const [messages, setMessages] = useState([]);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const sendMessage = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        if (!senderId || !receiverId || !messageContent) {
            setError("All fields are necessary");
            return;
        }
        const token = localStorage.getItem('token');
        try {
            const response = await axios.post(
                `http://localhost:8080/api/chat/new/${senderId}/${receiverId}`,
                { content: messageContent },
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    }
                }
            );
            setSuccess("Message sent");
            setMessageContent("");
        } catch (err) {
            setError(err.response?.data || err.message || "Error while sending message");
        }
    };

    const getMessages = async (e) => {
        e?.preventDefault();
        setError("");
        setSuccess("");

        if (!senderId || !receiverId) {
            setError("Provide ID for sender and receiver");
            return;
        }
        const token = localStorage.getItem('token');
        try {
            const response = await axios.get(
                `http://localhost:8080/api/chat/get/${senderId}/${receiverId}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );
            setMessages(response.data);
        } catch (err) {
            setError(err.response?.data || err.message || "Error while getting messages");
        }
    };

    return (
        <div className="test-container">
            <div className="test-panel">
                <h2 className="test-title">Testing chat</h2>

                {error && <div className="test-error">{error}</div>}
                {success && <div className="test-success">{success}</div>}

                <form onSubmit={sendMessage} className="test-form">
                    <div className="test-input-group">
                        <label className="test-label">Sender ID:</label>
                        <input
                            type="number"
                            value={senderId}
                            onChange={(e) => setSenderId(e.target.value)}
                            className="test-input"
                            required
                        />
                    </div>

                    <div className="test-input-group">
                        <label className="test-label">Receiver ID:</label>
                        <input
                            type="number"
                            value={receiverId}
                            onChange={(e) => setReceiverId(e.target.value)}
                            className="test-input"
                            required
                        />
                    </div>

                    <div className="test-input-group">
                        <label className="test-label">Message Content:</label>
                        <textarea
                            value={messageContent}
                            onChange={(e) => setMessageContent(e.target.value)}
                            className="test-input test-textarea"
                            rows="3"
                            required
                        />
                    </div>

                    <div className="test-button-group">
                        <button
                            type="submit"
                            className="test-button test-button-send"
                        >
                            Send Message
                        </button>
                        <button
                            type="button"
                            onClick={getMessages}
                            className="test-button test-button-fetch"
                        >
                            Get Messages
                        </button>
                    </div>
                </form>

                <div className="test-messages">
                    <h3 className="test-messages-title">History</h3>
                    {messages.length === 0 ? (
                        <p className="test-message-empty">No messages to show</p>
                    ) : (
                        <ul className="test-message-list">
                            {messages.map((msg, index) => (
                                <li key={index} className="test-message-item">
                                    <div className="test-message-header">
                                        <span>
                                            <strong>Od:</strong> {msg.sender.id} → <strong>Do:</strong> {msg.receiver.id}
                                        </span>
                                        <span>
                                            {new Date(msg.sentAt).toLocaleString()}
                                        </span>
                                    </div>
                                    <p className="test-message-content">{msg.content}</p>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
}

export default TestPage;