# Docker Database Setup

This guide explains how to run PostgreSQL using Docker, which is the easiest way to set up the database without installing PostgreSQL directly on your machine.

## Prerequisites

- Docker Desktop installed and running
  - Windows/Mac: Download from https://www.docker.com/products/docker-desktop
  - Linux: Install Docker Engine and Docker Compose

## Quick Start

### 1. Start PostgreSQL Container

From the project root directory, run:

```bash
docker-compose up -d
```

This will:
- Download the PostgreSQL 15 image (if not already present)
- Create and start a PostgreSQL container
- Create the `finalworks_db` database automatically
- Expose PostgreSQL on port 5432

### 2. Verify Container is Running

```bash
docker-compose ps
```

You should see the `finalworks-postgres` container running.

### 3. Check Logs (Optional)

```bash
docker-compose logs postgres
```

### 4. Start Your Backend Application

The backend is already configured to connect to PostgreSQL at `localhost:5432`. Just start your Spring Boot application and it will:
- Connect to the database
- Create all tables automatically
- Initialize sample data (2 final works) on first run

## Useful Commands

### Stop the Database
```bash
docker-compose down
```

### Stop and Remove Data (Clean Slate)
```bash
docker-compose down -v
```
⚠️ **Warning**: This will delete all database data!

### Restart the Database
```bash
docker-compose restart
```

### View Database Logs
```bash
docker-compose logs -f postgres
```

### Access PostgreSQL CLI
```bash
docker-compose exec postgres psql -U postgres -d finalworks_db
```

Or from outside Docker:
```bash
psql -h localhost -p 5432 -U postgres -d finalworks_db
```
Password: `postgres`

## Database Configuration

The Docker setup uses these default values (already configured in `application.properties`):
- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `finalworks_db`
- **Username**: `postgres`
- **Password**: `postgres`

If you need to change these, edit `docker-compose.yml` and update `application.properties` accordingly.

## Data Persistence

Data is stored in a Docker volume named `postgres_data`, so your data persists even if you stop the container. To completely remove all data, use:

```bash
docker-compose down -v
```

## Troubleshooting

### Port Already in Use
If port 5432 is already in use, you can change it in `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Use 5433 on host instead
```
Then update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/finalworks_db
```

### Container Won't Start
Check if Docker Desktop is running and try:
```bash
docker-compose down
docker-compose up -d
```

### Reset Everything
```bash
docker-compose down -v
docker-compose up -d
```

## Production Notes

For production, you should:
1. Change the default password in `docker-compose.yml`
2. Use environment variables for sensitive data
3. Configure proper backup strategies
4. Use a managed database service (AWS RDS, Azure Database, etc.) instead of Docker
