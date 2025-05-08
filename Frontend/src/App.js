import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './HomePage';
import Login from './LoginPage';
import Register from './RegisterPage';
import TestPage from './TestPage';
import MainChatPage from "./MainChatPage";

function App() {
  return (
      <Router>
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/test" element={<TestPage />} />
            <Route path="/chat" element={<MainChatPage />} />

        </Routes>
      </Router>
  );
}

export default App;