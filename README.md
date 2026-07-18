# Scala 3 FinTech Trading Platform

A modern, high-performance, and purely functional microservice simulation of a financial trading platform. This project acts as a showcase of modern **Scala 3** development practices, reactive stream processing, and interactive data visualization for top-tier European FinTech and Hedge Fund standards (e.g., EPAM Spain).

## 🚀 Architecture Overview

The system is split into two main independent modules running inside a unified monorepo:
1. **Backend (`/backend`)**: A lightweight, non-blocking, purely functional REST & WebSocket API built with the Typelevel ecosystem on Scala 3. It emulates a continuous stream of market tickers and handles atomic transaction management without lock blockages.
2. **Frontend (`/frontend`)**: An interactive Single Page Application (SPA) that opens a real-time reactive communication channel with the backend to render moving data points and accept trade orders.

---

## 🛠 Tech Stack

### Backend (Scala 3)
* **Language**: Scala 3.3+ (LTS) — utilizing modern features like Optional Braces (indentation-based syntax), `given`/`using` context abstractions, and `extension` methods.
* **Core & Effects**: `Cats Effect 3` — used as the asynchronous, concurrent, and resource-safe effect monad engine.
* **Streaming**: `FS2 (Functional Streams for Scala)` — handles the backpressured ingestion, transformation, and emission of real-time market data flows.
* **HTTP Server & Routing**: `Http4s` — a minimal, type-safe, and pure functional HTTP server implementation.
* **JSON Serialization**: `Circe` — automatic and semi-automatic compile-time JSON encoding/decoding for entity models.
* **Database Access**: `Skunk` / `Doobie` — non-blocking, pure functional routing to the persistence layer.
* **Logging**: `Log4cats` with `Logback`.

### Frontend (React & TypeScript)
* **Framework**: `React 18+` (Functional Components & Hooks).
* **Build Tool**: `Vite` — for ultra-fast compilation and HMR (Hot Module Replacement).
* **Language**: `TypeScript` — enforcing strict type-safety across API payloads.
* **Linter**: `ESLint` — tracking code quality and keeping to industry standards.
* **Styling & Components**: TailwindCSS / standard CSS grids for high-performance dashboard charts and blink-on-update quote tables.

---

## 🏗 Key Technical Highlights (For Interviewers)

* **Pure Functional Programming**: Zero side-effects or mutations (`var`). Every action is suspended inside the `cats.effect.IO` monad.
* **Lock-free Concurrency**: Thread-safe mutable state management (user balances, open order books) using atomic primitives like `Ref` and `Deferred` from Cats Effect instead of standard JVM synchronized blocks.
* **WebSocket Streaming**: Live-streaming financial tickers using an FS2 pipeline over a persistent WebSocket connection directly into the React context.
* **Resource Safety**: Precise allocation and automated releasing of HTTP clients and database connection pools via the `Resource` data type.
* **CORS Policy integration**: Fully configured cross-origin middleware on the Http4s layer to guarantee clean API request flows from the React frontend port (`5173`) to the Scala container (`8080`).

---

## 🏃‍♂️ Getting Started

### Prerequisites
* **Java Development Kit (JDK)**: Version 17 or 21
* **sbt**: Scala Build Tool 1.9+
* **Node.js**: Version 18+ & npm

### Running the Backend Locally
```bash
cd backend
sbt run
```
*The server will boot up natively on `http://localhost:8080`*

### Running the Frontend Locally
```bash
cd frontend
npm install
npm run dev
```
*The React SPA dashboard will open automatically on `http://localhost:5173`*

---

## 🐳 Docker Deployment (Production Emulation)

The whole ecosystem (Database, Scala core, and React view) can be spin-up in an orchestrated Docker container environment:

```bash
docker-compose up --build
```

