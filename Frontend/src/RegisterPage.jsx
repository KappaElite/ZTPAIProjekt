import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './RegisterPage.css';

function RegisterPage() {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        email: '',
        username: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', {
                username: formData.username,
                password: formData.password,
                email: formData.email
            });

            if (response.status === 201) {
                setSuccess('Registration successful! Redirecting...');
                setTimeout(() => navigate('/login'), 2000);
            }
        } catch (err) {
            if (err.response) {
                if (err.response.status === 406) {
                    setError('Registration failed: ' + (err.response.data || 'Invalid data'));
                } else {
                    setError('Error: ' + (err.response.data || err.message));
                }
            } else {
                setError('Network error: ' + err.message);
            }
        }
    };

    return (
        <div className="register-container">

            <div className="register-panel">
                <h2 className="register-title">REGISTER</h2>
                {error && <div className="error-message">{error}</div>}
                {success && <div className="success-message">{success}</div>}
                <form className="register-form" onSubmit={handleSubmit}>
                    <input
                        type="email"
                        name="email"
                        className="register-input"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />
                    <input
                        type="text"
                        name="username"
                        className="register-input"
                        placeholder="Username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />
                    <input
                        type="password"
                        name="password"
                        className="register-input"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />
                </form>
                <div className="register-footer">
                    <div className="login-link" onClick={() => navigate('/login')}>
                        Already have an account? Login
                    </div>
                </div>
            </div>


            <button type="button" className="register-button" onClick={handleSubmit}>
                REGISTER
            </button>
        </div>
    );
}

export default RegisterPage;