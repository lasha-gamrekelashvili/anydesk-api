# 🖥️ 

- 🧠 **Backend**: Java (Spring Boot)
- 🎨 **Frontend**: React + Vite
- 🐘 **Database**: PostgreSQL
- 🐳 **Dockerized** with `docker-compose` for local or deployment-ready usage

---

## 📁 Project Structure

```
anydesk-app/
├── anydesk-api/           # Java Spring Boot backend
│   ├── Dockerfile
│   ├── src/
│   ├── pom.xml
│   └── application.properties (uses env vars or fallbacks)
│
├── anydesk-client/        # React + Vite frontend
│   ├── Dockerfile
│   ├── .env
│   ├── .env.docker
│   ├── nginx.conf
│   └── src/
│
├── docker-compose.yml     # Docker Compose entry point
└── README.md              # This file
```

---

## 🚀 Run Everything Using Docker

### ✅ Prerequisites

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### ▶️ Run all services:

From the root directory:

```bash
docker compose up --build
```

### 🧩 Services Started:

| Service    | Description             | URL / Port                     |
|------------|-------------------------|--------------------------------|
| PostgreSQL | Database                | `localhost:5434`               |
| Backend    | Spring Boot app         | `http://localhost:8080`        |
| Frontend   | React + NGINX app       | `http://localhost:3000`        |

---

## 🔧 Backend Configuration

The project already comes with a pre-filled `application.properties` file that supports both Docker and local development via environment variable fallback:

```properties
spring.application.name=anydesk-api
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5434/anydeskdb}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:123123}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🌐 Frontend Configuration

Uses `VITE_API_URL` to dynamically target backend.

### `.env` (for local dev):

```
VITE_API_URL=http://localhost:8080
```

### `.env.docker` (for Docker build):

```
VITE_API_URL= (url intentionally left empty, this way nginx will re-direct any calls which start with /api to correct server)
```

## 📦 NGINX Proxy Configuration

Docker serves the React app via NGINX. The `nginx.conf` file proxies API calls:

```nginx
server {
  listen 80;

  location / {
    root   /usr/share/nginx/html;
    index  index.html;
    try_files $uri $uri/ /index.html;
  }

  location /api/ {
    proxy_pass http://backend:8080;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host $host;
    proxy_cache_bypass $http_upgrade;
  }
}
```

---

## 💻 Running Locally (Without Docker)

You can run the frontend and backend independently with only Docker for the DB.

---

### 🐘 Start Only PostgreSQL with Docker

```bash
docker compose up postgres
```

PostgreSQL will run on port `5434`. Default credentials:

- User: `postgres`
- Password: `123123`
- DB: `anydeskdb`

---

### ⚙️ Run Backend (Spring Boot) Locally

```bash
cd anydesk-api
./mvnw spring-boot:run
```

 `application.properties` already comes with fallback values which match the ones written above.

---

### 🖥 Run Frontend (React) Locally

```bash
cd anydesk-client
npm install
npm run dev
```

This will start Vite on `http://localhost:5173` (default), and send requests to `VITE_API_URL=http://localhost:8080`.

---

## ✅ Quick Test URLs

| Target      | Example                      |
|-------------|------------------------------|
| Frontend UI | `http://localhost:3000`(When running from docker) or `:5173`|
| Backend API | `http://localhost:8080/api/...`   |
| Swagger UI    | `http://localhost:8080/swagger-ui.html` |


