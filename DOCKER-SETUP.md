# 🐳 Docker Setup Guide - Canozbek Academy

## 📋 Prerequisites

- Docker Desktop installed ([Download](https://www.docker.com/products/docker-desktop))
- Docker Compose v2.0+
- 4GB+ RAM available
- 10GB+ disk space

---

## 🚀 Quick Start

### 1. Initialize Project (First Time)

```bash
# Copy environment variables
cp .env.example .env

# Edit .env file with your credentials
nano .env  # or use any text editor
```

### 2. Start All Services

```bash
# Using Docker Compose
docker-compose up -d

# Or using Makefile
make up
```

### 3. Access Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | - |
| Backend API | http://localhost:8080 | - |
| API Docs | http://localhost:8080/swagger-ui.html | - |
| PostgreSQL | localhost:5432 | user: canozbek_user, pass: canozbek_pass_2024 |
| pgAdmin | http://localhost:5050 | email: admin@canozbek.uz, pass: admin123 |
| MinIO Console | http://localhost:9001 | user: minioadmin, pass: minioadmin123 |
| Redis | localhost:6379 | no password |

---

## 📁 Project Structure

```
canozbek-academy/
├── backend/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   ├── package.json
│   ├── Dockerfile.dev
│   └── Dockerfile
├── docker-compose.yml
├── .env.example
├── .env (create from .env.example)
└── Makefile
```

---

## 🛠️ Available Commands

### Using Makefile (Recommended)

```bash
make help              # Show all available commands
make init              # Initialize project (first time)
make up                # Start all services
make down              # Stop all services
make restart           # Restart all services
make logs              # Show all logs
make logs-backend      # Show backend logs only
make logs-frontend     # Show frontend logs only
make clean             # Remove all containers and volumes
make dev               # Start only databases (for local development)
make backup-db         # Backup PostgreSQL database
```

### Using Docker Compose

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild images
docker-compose build --no-cache

# Remove everything
docker-compose down -v --rmi all
```

---

## 🔧 Development Modes

### Mode 1: Full Docker (Recommended for beginners)

Everything runs in Docker:

```bash
make up
```

### Mode 2: Hybrid (Best for development)

Only databases in Docker, backend/frontend run locally:

```bash
# Start only databases
make dev

# In another terminal, run backend
cd backend
./mvnw spring-boot:run

# In another terminal, run frontend
cd frontend
npm run dev
```

### Mode 3: Production

```bash
docker-compose -f docker-compose.prod.yml up -d
```

---

## 📊 Database Management

### Access PostgreSQL

```bash
# Using pgAdmin (GUI)
# Open http://localhost:5050
# Add server: postgres, port 5432, user: canozbek_user

# Using command line
make shell-postgres

# Or directly
docker-compose exec postgres psql -U canozbek_user -d canozbek_academy
```

### Backup Database

```bash
make backup-db
```

### Restore Database

```bash
make restore-db FILE=backup_20240115_120000.sql
```

---

## 📦 MinIO Setup (File Storage)

### Access MinIO Console

1. Open http://localhost:9001
2. Login: `minioadmin` / `minioadmin123`
3. Create bucket: `canozbek-files`
4. Set policy: Public (for public files)

### Configure Backend

Already configured in `docker-compose.yml`:

```yaml
MINIO_ENDPOINT: http://minio:9000
MINIO_ACCESS_KEY: minioadmin
MINIO_SECRET_KEY: minioadmin123
MINIO_BUCKET: canozbek-files
```

---

## 🔍 Troubleshooting

### Problem: Port already in use

```bash
# Check what's using the port
lsof -i :8080  # or 5432, 3000, etc.

# Stop the service using that port
# Or change port in docker-compose.yml
```

### Problem: Database connection failed

```bash
# Check if PostgreSQL is healthy
docker-compose ps

# View PostgreSQL logs
make logs-postgres

# Restart PostgreSQL
docker-compose restart postgres
```

### Problem: Backend not starting

```bash
# View backend logs
make logs-backend

# Check if database is ready
docker-compose exec postgres pg_isready -U canozbek_user

# Rebuild backend
docker-compose build backend
docker-compose up -d backend
```

### Problem: Frontend not loading

```bash
# Check frontend logs
make logs-frontend

# Check if backend is accessible
curl http://localhost:8080/actuator/health

# Rebuild frontend
docker-compose build frontend
docker-compose up -d frontend
```

### Problem: Out of disk space

```bash
# Clean up Docker
docker system prune -a --volumes

# Or just clean this project
make clean
```

---

## 🔐 Security Notes

### For Development:
- Default passwords are in `.env` file
- MinIO and PostgreSQL are accessible from localhost

### For Production:
- Change ALL default passwords
- Use strong JWT secret (64+ characters)
- Enable SSL/TLS
- Use environment variables, not .env file
- Restrict database access
- Use secure MinIO credentials

---

## 📈 Monitoring

### Check Service Health

```bash
# All services status
make status

# Backend health
curl http://localhost:8080/actuator/health

# Database health
docker-compose exec postgres pg_isready
```

### View Logs

```bash
# All logs
make logs

# Specific service
make logs-backend
make logs-frontend
make logs-postgres
```

---

## 🚢 Deployment

### Development

```bash
make up
```

### Production (with Docker)

```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Production (recommended)

Deploy to:
- **Backend**: Heroku, Railway, Render, AWS ECS
- **Frontend**: Vercel, Netlify, AWS S3 + CloudFront
- **Database**: AWS RDS, DigitalOcean Managed Postgres
- **Storage**: AWS S3, DigitalOcean Spaces

---

## 📞 Support

If you encounter issues:

1. Check logs: `make logs`
2. Check service status: `make status`
3. Restart services: `make restart`
4. Clean and rebuild: `make clean && make build && make up`

---

## 🎯 Next Steps

1. ✅ Start services: `make up`
2. ✅ Access frontend: http://localhost:3000
3. ✅ Test API: http://localhost:8080/actuator/health
4. ✅ Create first user via registration
5. ✅ Check pgAdmin: http://localhost:5050
6. ✅ Upload test files to MinIO: http://localhost:9001

Happy coding! 🚀