# Docker Deployment Guide

## Quick Start

### Prerequisites
- Install [Docker](https://docs.docker.com/get-docker/)
- Install [Docker Compose](https://docs.docker.com/compose/install/)

### Start the Application
```bash
# After cloning the project, run in the project root directory
docker-compose up -d
```

### Access the Application
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:3306

### Stop the Application
```bash
docker-compose down
```

### View Logs
```bash
# View all service logs
docker-compose logs

# View specific service logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql
```

## Development Environment

### Local Development (without Docker)
If you want to develop locally, you can:

1. **Start the database**:
```bash
docker-compose up mysql -d
```

2. **Run backend locally**:
```bash
cd backend
./mvnw spring-boot:run
```

3. **Run frontend locally**:
```bash
cd frontend
npm install
npm run dev
```

### Reset Data
```bash
# Delete all data (including database)
docker-compose down -v
docker-compose up -d
```

## Environment Variables

### Database Configuration
You can modify in `docker-compose.yml`:
- `MYSQL_ROOT_PASSWORD`: MySQL root password
- `MYSQL_DATABASE`: Database name
- `MYSQL_USER`: Application username
- `MYSQL_PASSWORD`: Application password

### Backend Configuration
- `SERVER_PORT`: Backend service port
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Test Accounts
The application will automatically create the following test accounts:
- Username: `admin`, Password: `admin123`
- Username: `test`, Password: `test123`

## Troubleshooting

### Port Conflicts
If ports are occupied, you can modify the port mapping in `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Change to 8081
```

### Database Connection Failure
1. Ensure MySQL container is running: `docker-compose ps`
2. Check database logs: `docker-compose logs mysql`
3. Wait for database to fully start (may take 30 seconds)

### Frontend Cannot Access Backend
1. Check if backend is running: `docker-compose logs backend`
2. Verify nginx configuration is correct
3. Check network connection: `docker network ls` 