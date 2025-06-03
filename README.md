# ZTPAIProjekt

A web-based chat application featuring a real-time chat system built with a modern Java backend and a React frontend. The project demonstrates integration of various technologies such as PostgreSQL, RabbitMQ, WebSockets, and Docker for a robust, scalable, and reactive chat experience.

## Technologies Used

### Backend
- **Spring Boot** (Java) — REST API and WebSocket server for real-time chat logic.
- **WebSockets** — Enables efficient real-time, bidirectional communication between chat clients and the backend.
- **PostgreSQL** — Persistent data storage for users and chat messages.
- **RabbitMQ** — Message broker for asynchronous profanity filtering, ensuring that forbidden words are filtered out without blocking the chat flow.

### Frontend
- **React** — Modern SPA frontend for a responsive, interactive user experience.
- Multiple client instances (for simulating chat between users in real time).

### Infrastructure
- **Docker Compose** — Used to orchestrate PostgreSQL and RabbitMQ services for easy local development and deployment.

## Why These Technologies?

- **Spring Boot** was chosen for its rapid development capabilities, rich ecosystem, and seamless integration with WebSockets, allowing for easy implementation of real-time features and RESTful APIs.
- **WebSockets** are crucial for a chat application, providing low-latency, full-duplex communication essential for instant messaging.
- **PostgreSQL** offers reliability, scalability, and robust support for relational data, making it ideal for storing chat histories and user information.
- **RabbitMQ** was integrated to handle profanity filtering asynchronously. This ensures that the main chat functionality remains fast and responsive, while message content moderation happens in the background.
- **React** enables building a highly interactive and dynamic frontend, with component-based architecture that fits modern web development best practices.
- **Docker Compose** simplifies setup and ensures consistent environments for development and testing, especially when integrating multiple services like databases and message brokers.

## Architecture

- The backend (Spring Boot) exposes REST endpoints and WebSocket channels, managing chat logic, user management, message history, and profanity filtering.
- The React frontend communicates with the backend via HTTP and WebSocket APIs.
- RabbitMQ is used for asynchronous message processing (profanity filtering).
- PostgreSQL stores user and message data.
- Docker Compose simplifies launching PostgreSQL and RabbitMQ containers.
- Two frontend instances can be run simultaneously (e.g., on ports 3000 and 3001) to simulate multiple chat clients and demonstrate real-time communication.

## Getting Started

### Prerequisites

- Java 17+ (for Spring Boot backend)
- Node.js + npm (for React frontend)
- Docker + Docker Compose

### Running the Application

#### 1. Start Infrastructure (PostgreSQL & RabbitMQ)

From the root directory, run:

```bash
docker-compose up --build
```

This will launch PostgreSQL and RabbitMQ containers.

#### 2. Start the Backend

In a new terminal, navigate to the backend directory and run:

```bash
./mvnw spring-boot:run
```
or
```bash
mvn spring-boot:run
```

#### 3. Start Two Frontend Instances

Open two terminals.

- In the first terminal, run:

  ```bash
  cd frontend
  npm install
  npm start
  ```

- In the second terminal, run:

  ```bash
  cd frontend
  PORT=3001 npm start
  ```

This will launch two chat clients on different ports (3000 and 3001).

### Usage

- Open both frontend URLs in your browser (e.g., http://localhost:3000 and http://localhost:3001).
- Chat between users in real time via WebSockets.
- Messages containing profanities are filtered asynchronously via RabbitMQ, ensuring a clean chat experience without interrupting user interactions.
