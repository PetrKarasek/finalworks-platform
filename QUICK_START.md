# Quick Start Guide

Follow these steps to run the application locally:

## Prerequisites

- Docker Desktop installed and running
- Java 17+ installed
- Node.js and npm installed

## Step 1: Start the Database (PostgreSQL with Docker)

From the project root directory, run:

```bash
docker-compose up -d
```

This will:
- Download PostgreSQL 15 image (if not already present)
- Create and start a PostgreSQL container
- Create the `finalworks_db` database automatically
- Expose PostgreSQL on port 5432

**Verify it's running:**
```bash
docker-compose ps
```

You should see `finalworks-postgres` container running.

## Step 2: Generate SSL Certificate (if not already done)

The keystore should already exist, but if you need to regenerate it:

**Windows (PowerShell):**
```powershell
cd backend\src\main\resources
keytool -genkeypair -alias finalworks -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -storepass changeit -dname "CN=localhost, OU=Development, O=FinalWorks, L=City, ST=State, C=US" -noprompt
```

**Linux/Mac:**
```bash
cd backend/src/main/resources
keytool -genkeypair -alias finalworks -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -storepass changeit -dname "CN=localhost, OU=Development, O=FinalWorks, L=City, ST=State, C=US" -noprompt
```

### Step 2: Start the Backend

1. Open a terminal in the `backend` directory:
   ```bash
   cd backend
   ```

2. Start the Spring Boot application:
   - **If using IDE (IntelliJ/Eclipse)**: Run `FinalWorksApplication.java`
   - **If using Maven from command line**:
     ```bash
     mvn spring-boot:run
     ```

3. Wait for the application to start. You should see:
   - "Started FinalWorksApplication" message
   - Database connection successful
   - Sample data initialized (2 final works created)

4. The backend will be available at:
   - **HTTPS**: `https://localhost:8443`
   - **HTTP** (redirects to HTTPS): `http://localhost:8080`

**Note**: Your browser will show a security warning for the self-signed certificate. Click "Advanced" → "Proceed to localhost" to continue.

### Step 3: Start the Frontend

1. Open a **new terminal** in the `frontend` directory:
   ```bash
   cd frontend
   ```

2. Install dependencies (if not already installed):
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

4. The frontend will:
   - Start on `http://localhost:3000`
   - Automatically open in your browser
   - Connect to the backend API

## Complete Startup Sequence

```bash
# Terminal 1: Start Database
docker-compose up -d

# Terminal 2: Start Backend
cd backend
mvn spring-boot:run
# OR run FinalWorksApplication.java from your IDE

# Terminal 3: Start Frontend
cd frontend
npm start
```

## Verify Everything is Running

1. **Database**: 
   ```bash
   docker-compose ps
   ```
   Should show `finalworks-postgres` as running

2. **Backend**: 
   - Open `https://localhost:8443/api/final-works` in browser
   - Should return JSON with final works (accept SSL warning first)

3. **Frontend**: 
   - Open `http://localhost:3000`
   - Should show the homepage with sample works

## Troubleshooting

### Database Connection Error

If backend can't connect to database:
1. Check if Docker container is running:
   ```bash
   docker-compose ps
   ```
2. Check database logs:
   ```bash
   docker-compose logs postgres
   ```
3. Restart the database:
   ```bash
   docker-compose restart
   ```

### Port Already in Use

If port 5432 is already in use:
1. Change port in `docker-compose.yml`:
   ```yaml
   ports:
     - "5433:5432"  # Use 5433 instead
   ```
2. Update `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5433/finalworks_db
   ```

### Backend Won't Start

- Check if Java 17+ is installed: `java -version`
- Check if Maven is installed: `mvn -version`
- Check backend logs for errors

### Frontend Can't Connect to Backend

- Make sure backend is running on `https://localhost:8443`
- Accept the SSL certificate warning in browser first
- Check browser console for errors

## Stopping the Application

### Stop Frontend
Press `Ctrl+C` in the frontend terminal

### Stop Backend
Press `Ctrl+C` in the backend terminal (or stop from IDE)

### Stop Database
```bash
docker-compose down
```

### Stop and Remove All Data
```bash
docker-compose down -v
```
⚠️ **Warning**: This will delete all database data!

## Default Credentials

The application creates 2 sample students on first run:
- **Student 1**: jan.novak@example.com (password: Password123)
- **Student 2**: marie.svobodova@example.com (password: SecurePass456)

## Next Steps

1. Register a new account (password must be 8+ chars with 1 uppercase)
2. Upload your final work
3. Browse and comment on works
4. Bookmark your favorites

Enjoy using the Final Works Platform! 🎓
