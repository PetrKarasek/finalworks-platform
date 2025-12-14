# Database Setup Guide

## Quick Start (H2 - Recommended for Development)

The application is currently configured to use **H2 in-memory database** by default. This works immediately without any setup:

- ✅ No installation required
- ✅ Starts automatically
- ✅ Perfect for development and testing
- ⚠️ Data is lost when the application restarts

The H2 console is available at: `https://localhost:8443/h2-console`

**JDBC URL for H2 Console:** `jdbc:h2:mem:finalworks_db`  
**Username:** `sa`  
**Password:** (leave empty)

## PostgreSQL Setup (For Production)

If you want to use PostgreSQL instead:

### 1. Install PostgreSQL

- Download from: https://www.postgresql.org/download/windows/
- Or use Chocolatey: `choco install postgresql`

### 2. Create Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE finalworks_db;

-- Create user (optional)
CREATE USER finalworks_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE finalworks_db TO finalworks_user;

-- Exit
\q
```

### 3. Update Configuration

Edit `backend/src/main/resources/application.properties`:

1. Comment out the H2 configuration:
```properties
# spring.datasource.url=jdbc:h2:mem:finalworks_db
# spring.datasource.driver-class-name=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
```

2. Uncomment the PostgreSQL configuration:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finalworks_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

3. Update the username and password if different from defaults.

### 4. Restart Application

The application will automatically create tables on first startup (due to `spring.jpa.hibernate.ddl-auto=update`).

## Using Development Profile

Alternatively, you can use the `dev` profile which uses H2:

```bash
java -jar target/finalworks-backend-1.0.0.jar --spring.profiles.active=dev
```

Or in your IDE, add VM options: `-Dspring.profiles.active=dev`

## Database Schema

The application automatically creates these tables:
- `students` - Student information
- `final_works` - Final work submissions
- `comments` - Comments on final works
- `fatal_logs` - Application error logs
