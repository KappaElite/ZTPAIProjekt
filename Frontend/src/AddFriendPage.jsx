import React, {useEffect,useState} from "react";
import { useNavigate } from "react-router-dom";
import "./AddFriendPage.css";
import { jwtDecode } from "jwt-decode";
import axios from "axios";

function AddFriendPage() {
    const navigate = useNavigate();

    const [availableFriends, setAvailableFriends] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userId = decoded.userID;

        axios.get(`http://localhost:8080/api/userlist/get/${userId}`, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
        })
            .then((response) => {
                setAvailableFriends(response.data);
            })
            .catch((error) => {
                console.error("Error fetching available friends:", error);
            });

    }, []);

    const handleBackClick = () => {
        navigate("/chat");
    };

    const handleSendRequest = (friendId) => {
        const token = localStorage.getItem("token");
        const decoded = jwtDecode(token);
        const userId = decoded.userID;
        axios.post(`http://localhost:8080/api/friend/add/${userId}/${friendId}`, {},{
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            }
        })
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