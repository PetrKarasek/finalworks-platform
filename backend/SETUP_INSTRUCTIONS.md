# Setup Instructions

## Database Setup (PostgreSQL)

1. **Install PostgreSQL** (if not already installed):
   - Windows: Download from https://www.postgresql.org/download/windows/
   - macOS: `brew install postgresql && brew services start postgresql`
   - Linux: `sudo apt-get install postgresql` (Ubuntu/Debian)

2. **Create Database**:
   ```sql
   -- Connect to PostgreSQL
   psql -U postgres

   -- Create database
   CREATE DATABASE finalworks_db;

   -- Exit
   \q
   ```

3. **Update Configuration** (if needed):
   - Edit `src/main/resources/application.properties`
   - Update `spring.datasource.username` and `spring.datasource.password` if different from defaults

4. **Run Application**:
   - The application will automatically create tables on first startup
   - Sample data (2 final works) will be created automatically if database is empty

## Email Configuration (Optional)

The application will work without email configuration, but emails won't be sent.

### For Development (Console Logging):
No configuration needed - the app will log email attempts.

### For Production (SMTP):
Add to `src/main/resources/application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Note**: For Gmail, you need to use an "App Password" instead of your regular password.

## Features Implemented

✅ **CORS Fixed**: Frontend can now connect from `http://localhost:3000`
✅ **PostgreSQL Database**: Configured to use PostgreSQL
✅ **Sample Data**: 2 sample final works are created automatically on first startup
✅ **Password Validation**: 
   - Minimum 8 characters
   - At least 1 uppercase letter
✅ **Email Confirmation**: Sends confirmation email on registration (if email is configured)

## Sample Data

On first startup, the application creates:
- 2 sample students (Jan Novák, Marie Svobodová)
- 2 sample final works with descriptions

## Password Requirements

When registering, passwords must:
- Be at least 8 characters long
- Contain at least one uppercase letter

Example valid passwords:
- `Password123`
- `MySecurePass`
- `Test1234`

Invalid passwords:
- `password` (no uppercase)
- `PASS` (too short)
- `12345678` (no uppercase)
