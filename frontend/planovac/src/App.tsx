import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './mainPage/MainPage';
import './App.css';
import Schedule from './schedule/Schedule';
import WorkplaceView from './work_operation_service/WorkplaceView';
import WorkerView from './work_operation_service/WorkerView';
import HomePage from './homepage/Homepage';
import Header from './header/Header';

function App() {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/schedule/:workOperationId" element={<Schedule />} />
        <Route path="/workplaceview/:workOperationId" element={<WorkplaceView />} />
        <Route path="/workerview/:workOperationId" element={<WorkerView />} />
      </Routes>
    </Router>
  );
}

export default App;