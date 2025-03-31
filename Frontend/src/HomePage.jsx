import React from 'react';
import { useNavigate } from 'react-router-dom';
import messageIcon from './assets/message_icon.png';
import './HomePage.css';

function HomePage() {
    const navigate = useNavigate();

    return (
        <div className="home-container">

            <img
                src={messageIcon}
                alt="Message Icon"
                className="login-icon"
            />


            <div className="auth-panel">
                <div className="buttons-container">
                    <button
                        className="auth-button"
                        onClick={() => navigate('/login')}
                    >
                        Login
                    </button>
                    <button
                        className="auth-button"
                        onClick={() => navigate('/register')}
                    >
                        Register
                    </button>
                </div>
            </div>
        </div>
    );
}

export default HomePage;