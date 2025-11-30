# HTTPS Setup Instructions

This application is configured to use HTTPS. You need to generate a self-signed SSL certificate for development.

## Generate SSL Certificate

### Using Java Keytool (Recommended)

Run the following command in the `backend/src/main/resources` directory:

```bash
keytool -genkeypair -alias finalworks -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 365 -storepass changeit
```

When prompted, enter:
- **First and last name**: `localhost`
- **Organizational unit**: (can be left blank or enter your organization)
- **Organization**: (can be left blank or enter your organization)
- **City**: (your city)
- **State**: (your state)
- **Country code**: (your 2-letter country code, e.g., US)

**Important**: When asked "What is your first and last name?", enter `localhost` (this is critical for the certificate to work with localhost).

### Using OpenSSL (Alternative)

If you prefer OpenSSL:

```bash
# Generate private key
openssl genrsa -out key.pem 2048

# Generate certificate signing request
openssl req -new -key key.pem -out csr.pem

# Generate self-signed certificate
openssl x509 -req -days 365 -in csr.pem -signkey key.pem -out cert.pem

# Convert to PKCS12 format
openssl pkcs12 -export -in cert.pem -inkey key.pem -out keystore.p12 -name finalworks -password pass:changeit
```

Then place `keystore.p12` in `backend/src/main/resources/`.

## Configuration

The application is configured with:
- **HTTPS Port**: 8443
- **HTTP Port**: 8080 (redirects to HTTPS)
- **Keystore**: `classpath:keystore.p12`
- **Password**: `changeit` (change this in production!)

## Accessing the Application

- **Backend HTTPS**: https://localhost:8443
- **Frontend**: https://localhost:3000 (when configured with HTTPS)

## Browser Warnings

Since this uses a self-signed certificate, browsers will show a security warning. For development:
- **Chrome/Edge**: Click "Advanced" → "Proceed to localhost (unsafe)"
- **Firefox**: Click "Advanced" → "Accept the Risk and Continue"

## Production

For production, use a certificate from a trusted Certificate Authority (CA) like Let's Encrypt, and update:
- `server.ssl.key-store` to point to your production certificate
- `server.ssl.key-store-password` to a secure password
- Update CORS origins to your production domain

