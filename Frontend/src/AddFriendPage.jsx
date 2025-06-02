import React, {useEffect,useState} from "react";
import { useNavigate } from "react-router-dom";
import "./AddFriendPage.css";
import { jwtDecode } from "jwt-decode";
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

function AddFriendPage() {
    const navigate = useNavigate();

    const [availableFriends, setAvailableFriends] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userId = decoded.userID;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/userlist/get/${userId}`,
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
            .then((response) => {
                setAvailableFriends(response.data);
            })
            .catch((error) => {
                console.error("Error fetching available friends:", error);
            });

    }, [navigate]);

    const handleBackClick = () => {
        navigate("/chat");
    };

    const handleSendRequest = (friendId) => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userId = decoded.userID;

        fetchWithAuthRetry({
            url: `http://localhost:8080/api/friend/add/${userId}/${friendId}`,
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
        }, navigate)
            .then(() => {
                setAvailableFriends((prevFriends) =>
                    prevFriends.filter((friend) => friend.id !== friendId)
                );
            })
            .catch((error) => {
                console.error("Error sending friend request:", error);
            });
    };

    return (
        <div className="chat-container">
            <div className="sidebar">
                <div className="sidebar-header">Add Friends</div>
                <button className="add-friend-button" onClick={handleBackClick}>
                    Back
                </button>
            </div>
            <div className="chat-panel">
                <div className="chat-header">
                    <div className="chat-header-title">Available Friends</div>
                </div>
                <div className="friend-list">
                    {availableFriends.map((friend) => (
                        <div key={friend.id} className="friend-item">
                            <div className="friend-name">{friend.username}</div>
                            <button
                                className="add-friend-button"
                                onClick={() => handleSendRequest(friend.id)}
                            >
                                Send Request
                            </button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default AddFriendPage;

