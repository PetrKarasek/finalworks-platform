# Final Works Platform

A web application for teachers to share and comment on student final works. Built with Spring Boot (backend) and React (frontend).

## Features

- View all student final works
- View detailed information about each final work
- Add comments to final works
- Student management
- File upload support (file URLs)

## Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 17
- **Frontend**: React 18.2.0
- **Database**: PostgreSQL
- **Build Tools**: Maven (backend), npm (frontend)

## Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- PostgreSQL 12+
- Maven 3.6+

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Node.js 16+ and npm
- PostgreSQL 12+
- Maven 3.6+

### 1. Database Setup

1. Start PostgreSQL service
2. Create a PostgreSQL database:

```sql
CREATE DATABASE finalworks_db;
```

3. Update the database credentials in `backend/src/main/resources/application.properties` if needed:
   - Default username: `postgres`
   - Default password: `postgres`
   - Default database: `finalworks_db`

### 2. SSL Certificate Setup (Required for HTTPS)

Before running the backend, you need to generate an SSL certificate:

**Windows (PowerShell):**
```powershell
cd backend\src\main\resources
keytool -genkeypair -alias finalworks -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -storepass changeit
```

**Linux/Mac:**
```bash
cd backend/src/main/resources
keytool -genkeypair -alias finalworks -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -storepass changeit
```

**Important:** When prompted for "What is your first and last name?", enter: `localhost`

This will create `keystore.p12` in `backend/src/main/resources/` directory.

For detailed instructions, see [HTTPS_SETUP.md](backend/HTTPS_SETUP.md)

### 3. Backend Setup

1. Navigate to the backend directory:

```bash
cd backend
```

2. Build the project:

```bash
mvn clean install
```

3. Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend will start on:
- **HTTPS**: `https://localhost:8443`
- **HTTP**: `http://localhost:8080` (automatically redirects to HTTPS)

**Note:** Your browser will show a security warning for the self-signed certificate. Click "Advanced" → "Proceed to localhost" to continue.

### 4. Frontend Setup

1. Navigate to the frontend directory:

```bash
cd frontend
```

2. Install dependencies:

```bash
npm install
```

3. Start the development server:

```bash
npm start
```

The frontend will start on `http://localhost:3000` (or `https://localhost:3000` if you configure HTTPS for the frontend)

### 5. Access the Application

1. Open your browser and navigate to: `http://localhost:3000`
2. The frontend will communicate with the backend over HTTPS at `https://localhost:8443`
3. Accept the SSL certificate warning when accessing the backend API

## API Endpoints

### Students
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `POST /api/students` - Create a new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Final Works
- `GET /api/final-works` - Get all final works
- `GET /api/final-works/{id}` - Get final work by ID
- `POST /api/final-works` - Create a new final work
- `PUT /api/final-works/{id}` - Update final work
- `DELETE /api/final-works/{id}` - Delete final work

### Comments
- `GET /api/final-works/{id}/comments` - Get comments for a final work
- `POST /api/final-works/{id}/comments` - Add a comment to a final work
- `DELETE /api/final-works/comments/{commentId}` - Delete a comment

## Project Structure

```
.
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/finalworks/
│   │   │   │   ├── config/          # Configuration classes
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── model/           # Entity models
│   │   │   │   ├── repository/      # JPA repositories
│   │   │   │   └── service/         # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   └── pom.xml
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── components/              # React components
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
└── README.md
```

## Quick Start (Summary)

1. **Database**: Create PostgreSQL database `finalworks_db`
2. **SSL Certificate**: Generate `keystore.p12` in `backend/src/main/resources/` (see step 2 above)
3. **Backend**: Run `mvn spring-boot:run` in the `backend` directory
4. **Frontend**: Run `npm start` in the `frontend` directory
5. **Access**: Open `http://localhost:3000` in your browser

## Usage

1. Start the PostgreSQL database
2. Generate SSL certificate (see step 2 above)
3. Start the Spring Boot backend (runs on HTTPS port 8443)
4. Start the React frontend (runs on port 3000)
5. Open `http://localhost:3000` in your browser
6. Accept the SSL certificate warning when prompted
7. Create students via the API or directly in the database
8. Create final works and add comments

## Development Notes

- The database schema is automatically created/updated by Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
- CORS is configured to allow requests from `https://localhost:3000`
- Security is currently disabled for development (all endpoints are public)
- File uploads are configured to accept files up to 10MB
- **HTTPS is enabled**: Backend runs on HTTPS (port 8443), HTTP (port 8080) redirects to HTTPS
- Passwords are hashed with BCrypt before storage
- All API responses use proper HTTP status codes (200, 201, 400, 404, 409, 500)

## Future Enhancements

- User authentication and authorization
- File upload functionality
- Search and filtering
- Pagination
- Email notifications
- Rich text editor for comments
- File preview functionality

