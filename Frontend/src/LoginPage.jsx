import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginPage.css';

function LoginPage() {
    const navigate = useNavigate();
    const [credentials, setCredentials] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        setCredentials({
            ...credentials,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                username: credentials.username,
                password: credentials.password
            });

            if(response.data.refreshToken){
                localStorage.setItem('refreshToken', response.data.refreshToken);
            }
            else{
                setError('Missing refresh token');
            }

            if (response.data.token) {
                localStorage.setItem('token', response.data.token);
                navigate('/chat');
            } else {
                setError(response.data.error || 'Login failed');
            }
        } catch (err) {
            if (err.response && err.response.data && err.response.data.error) {
                setError(err.response.data.error);
            } else {
                setError('Login failed. Please try again.');
            }
        }
    };

    return (
        <div className="login-container">
            <div className="login-panel">
                {error && <div className="error-message">{error}</div>}
                <form className="login-form" onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="username"
                        className="login-input"
                        placeholder="Login"
                        value={credentials.username}
                        onChange={handleChange}
                        required
                    />
                    <input
                        type="password"
                        name="password"
                        className="login-input"
                        placeholder="Password"
                        value={credentials.password}
                        onChange={handleChange}
                        required
                    />
                    <div className="forgot-password" onClick={() => navigate('/forgot-password')}>
                        Forgot password?
                    </div>
                </form>
                <button type="button" className="login-button" onClick={handleSubmit}>
                    Login
                </button>
            </div>
        </div>
    );
}

export default LoginPage;