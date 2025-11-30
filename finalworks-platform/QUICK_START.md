# Quick Start Guide

Follow these steps to run the application locally:

## Step 1: Database Setup

1. Start PostgreSQL
2. Create database:
```sql
CREATE DATABASE finalworks_db;
```

## Step 2: Generate SSL Certificate

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

**Important:** When asked "What is your first and last name?", enter: `localhost`

## Step 3: Start Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will be available at: **https://localhost:8443**

## Step 4: Start Frontend

Open a new terminal:

```bash
cd frontend
npm install
npm start
```

Frontend will be available at: **http://localhost:3000**

## Step 5: Access Application

1. Open browser: `http://localhost:3000`
2. When you see SSL certificate warning, click "Advanced" → "Proceed to localhost"
3. Application is ready to use!

## Troubleshooting

### SSL Certificate Error
- Make sure `keystore.p12` exists in `backend/src/main/resources/`
- Verify the certificate was created with alias `finalworks`
- Check that you entered `localhost` when prompted for name

### Port Already in Use
- Backend: Change `server.port` in `application.properties`
- Frontend: Change port in `package.json` or use `PORT=3001 npm start`

### Database Connection Error
- Verify PostgreSQL is running
- Check credentials in `backend/src/main/resources/application.properties`
- Ensure database `finalworks_db` exists

### CORS Errors
- Verify backend is running on HTTPS (port 8443)
- Check CORS configuration in `CorsConfig.java` and controllers

