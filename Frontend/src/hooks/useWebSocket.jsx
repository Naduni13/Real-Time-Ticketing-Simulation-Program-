// src/hooks/useWebSocket.js
import { useState, useEffect } from 'react';

const useWebSocket = (url, onMessage) => {
  const [connected, setConnected] = useState(false);
  
  useEffect(() => {
    const socket = new WebSocket(url);
    
    socket.onopen = () => {
      setConnected(true);
      console.log('Connected to WebSocket');
    };

    socket.onmessage = (event) => {
      onMessage(event.data);  // Call the callback passed as argument
    };

    socket.onclose = () => {
      setConnected(false);
      console.log('Disconnected from WebSocket');
    };

    return () => {
      socket.close();
    };
  }, [url, onMessage]);

  return { connected };
};

export default useWebSocket;
