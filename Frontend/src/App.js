import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './HomePage';
import Login from './LoginPage';
//import Register from './Register';

function App() {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<Login />} />
        </Routes>
      </Router>
  );
}

export default App;