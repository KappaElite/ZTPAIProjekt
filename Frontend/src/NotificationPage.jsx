import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./NotificationPage.css";
import React, {useEffect,useState} from "react";
import axios from "axios";

function NotificationPage() {
    const navigate = useNavigate();
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userID = decoded.userID;

        axios.get(`http://localhost:8080/api/request/get/${userID}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        }
        )
            .then((response) => {
                setNotifications(response.data);
            })
            .catch((error) => {
                console.error("Error fetching notifications:", error);
            });

    }, []);

    const handleBackClick = () => {
        navigate("/chat");
    };

    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Notifications</div>
                <button className="add-friend-button" onClick={handleBackClick}>
                    Back
                </button>
            </div>
            <div className="chat-panel">
                <div className="chat-header">
                    <div className="chat-header-title">Friend Requests</div>
                </div>
                <div className="notification-list">
                    {notifications.map((notification) => {
                        return (
                            <div key={notification.id} className="notification-item">
                                <div className="notification-text">{notification.username} sent you a friend request</div>
                                <div className="notification-actions">
                                    <button className="accept-button">Accept</button>
                                    <button className="decline-button">Decline</button>
                                </div>
                            </div>
                        );
                    })}
                 </div>
            </div>
        </div>
    );
}

export default NotificationPage;