import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './HomePage';
import Login from './LoginPage';
import Register from './RegisterPage';
import TestPage from './TestPage';

function App() {
  return (
      <Router>
        <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/test" element={<TestPage />} />

        </Routes>
      </Router>
  );
}

export default App;