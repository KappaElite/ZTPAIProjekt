import React from "react";
import { useNavigate } from "react-router-dom";
import "./AdminPanelPage.css";
import ERDImage from "./assets/ERD.png";

function AdminPanelPage() {
    const navigate = useNavigate();

    const handleBackClick = () => {
        navigate("/chat");
    };

    return (
        <div className="admin-container">
            <div className="admin-sidebar">
                <div className="admin-sidebar-header">Admin Panel</div>
                <button className="back-button" onClick={handleBackClick}>
                    Back to Chat
                </button>
            </div>
            <div className="admin-panel">
                <div className="admin-header">
                    <div className="admin-header-title">Database Schema (ERD)</div>
                </div>
                <div className="admin-content">
                    <div className="erd-container">
                        <img src={ERDImage} alt="Database ERD" className="erd-image" />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AdminPanelPage;
