import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './HomePage';
import Login from './LoginPage';
import Register from './RegisterPage';
import TestPage from './TestPage';
import MainChatPage from "./MainChatPage";
import AddFriendPage from "./AddFriendPage";
import NotificationPage from "./NotificationPage";
import AdminPanelPage from "./AdminPanelPage";

function App() {
  return (
      <Router>
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/test" element={<TestPage />} />
            <Route path="/chat" element={<MainChatPage />} />
            <Route path="/add-friend" element={<AddFriendPage />} />
            <Route path="/notification" element={<NotificationPage />} />
            <Route path="/admin" element={<AdminPanelPage />} />
        </Routes>
      </Router>
  );
}

export default App;

