import React, { useState } from 'react';
import axios from 'axios';
import './App.css'; // Import the CSS file

const App = () => {
  const [statusList, setStatusList] = useState([]);
  const [isStarted, setIsStarted] = useState(false);
  const [isRunning, setIsRunning] = useState(false); // Track if the system is running
  const [config, setConfig] = useState(null);
  const [status, setStatus] = useState('');
  // Configure the system
  const configureSystem = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/configure');
      setStatusList((prevStatus) => [...prevStatus, response.data]);
      if (response.data === "System configured successfully using configuration from JSON.") {
        const configResponse = await axios.get('http://localhost:8080/api/status');
        setConfig(configResponse.data);
      }
    } catch (error) {
      setStatusList((prevStatus) => [...prevStatus, 'Error configuring system: ' + error.message]);
    }
  };

  // Start the system
  const startSystem = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/start');
      setStatusList((prevStatus) => [...prevStatus, response.data]);
      if (response.data === "System started successfully.") {
        setIsStarted(true);
        setIsRunning(true); // System is running now
      }
    } catch (error) {
      setStatusList((prevStatus) => [...prevStatus, 'Error starting system: ' + error.message]);
    }
  };

  // Fetch status list
  const fetchStatusList = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/status');
      setStatusList(response.data);
    } catch (error) {
      setStatusList((prevStatus) => [...prevStatus, 'Error fetching status: ' + error.message]);
    }
  };
//Stop system
  const stopSystem = async () => {
    try {
        const response = await fetch("http://localhost:8080/api/stop", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (response.ok) {
            const result = await response.json();
            setStatus(result); // Set the result message to state
        } else {
            setStatus("Error stopping system.");
        }
    } catch (error) {
        setStatus("Failed to stop the system.");
    }
  };

   // Reset system
   const resetSystem = async () => {
    try {
      const response = await axios.post('http://localhost:8080/api/reset');
      setStatusList([response.data]); // Clear logs and display reset message
      setConfig(null); // Clear configuration
      setIsStarted(false);
      setIsRunning(false);
    } catch (error) {
      setStatusList((prevStatus) => [...prevStatus, 'Error resetting system: ' + error.message]);
    }
  };

  return (
    <div className="app-container">
      <header>
        <h1>Ticketing System</h1>
      </header>
      <div className="content">
        <div className="left-panel">
          <button onClick={configureSystem}>
            Configure System
          </button>
          <button onClick={startSystem} disabled={isStarted}>
            {isStarted ? 'System Started' : 'Start System'}
          </button>
          <button onClick={stopSystem}>Stop System</button>
          <button onClick={fetchStatusList}>Fetch Status</button>
          <button onClick={resetSystem} style={{ backgroundColor: '#ff6666' }}>
            Reset System
          </button>
        </div>
        <div className="right-panel">
          <h2>Log Display:</h2>
          <ul>
            {statusList.map((status, index) => (
              <li key={index}>{status}</li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default App;
