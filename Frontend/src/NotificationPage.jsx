import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import "./NotificationPage.css";
import React, {useEffect,useState} from "react";
import axios from "axios";

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

function NotificationPage() {
    const navigate = useNavigate();
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userID = decoded.userID;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/request/get/${userID}`,
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
            .then((response) => {
                setNotifications(response.data);
            })
            .catch((error) => {
                console.error("Error fetching notifications:", error);
            });

    }, [navigate]);

    const handleBackClick = () => {
        navigate("/chat");
    };

    const handleAcceptRequest = (notification) => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userID = decoded.userID;
        const friendID = notification.id;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/request/accept/${userID}/${friendID}`,
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
        .then(() => {
            setNotifications((prevState)=>
                prevState.filter((notification) => notification.id !== friendID)
            );
        })
        .catch((error) => {
            console.error("Error accepting request:", error);
        })
    }

    const handleRejectRequest = (notification) => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userID = decoded.userID;
        const friendID = notification.id;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/request/reject/${userID}/${friendID}`,
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
        .then(() => {
            setNotifications((prevState)=>
            prevState.filter((notification) => notification.id !== friendID)
            );
        })
        .catch((error) => {
            console.error("Error rejecting request:", error);
        })
    }

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
                                    <button className="accept-button" onClick={()=>handleAcceptRequest(notification)}>Accept</button>
                                    <button className="decline-button" onClick={()=>handleRejectRequest(notification)}>Decline</button>
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

