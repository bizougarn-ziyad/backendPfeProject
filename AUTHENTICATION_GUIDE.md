# Authentication Integration Guide

This document explains how to run the backend and frontend with the integrated authentication system.

## Backend Setup (Spring Boot)

### Prerequisites
- Java 17 or higher
- PostgreSQL database
- Maven

### Configuration

1. **Database Setup**
   - Create a PostgreSQL database named `movie_tracker`
   - Update `src/main/resources/application.properties` with your database credentials:
     ```properties
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

2. **Run the Backend**
   ```bash
   cd c:\Users\ziyad\backendProjetPfe\projection
   ./mvnw spring-boot:run
   ```
   
   The backend will start on `http://localhost:8080`

### Backend API Endpoints

#### Sign Up
- **URL:** `POST http://localhost:8080/api/auth/signup`
- **Request Body:**
  ```json
  {
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Success Response (201 Created):**
  ```json
  {
    "id": "uuid",
    "username": "johndoe",
    "email": "john@example.com",
    "bio": null,
    "profilePictureUrl": null,
    "role": "USER",
    "message": "User registered successfully"
  }
  ```

#### Login
- **URL:** `POST http://localhost:8080/api/auth/login`
- **Request Body:**
  ```json
  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "id": "uuid",
    "username": "johndoe",
    "email": "john@example.com",
    "bio": null,
    "profilePictureUrl": null,
    "role": "USER",
    "message": "Login successful"
  }
  ```

### Validation Rules

#### Sign Up
- **username:** 
  - Required
  - Length: 3-50 characters
  - Must be unique
- **email:** 
  - Required
  - Valid email format
  - Max length: 255 characters
  - Must be unique
- **password:** 
  - Required
  - Min length: 6 characters

#### Login
- **email:** 
  - Required
  - Valid email format
- **password:** 
  - Required

### Error Responses

- **400 Bad Request** - Validation errors
- **401 Unauthorized** - Invalid credentials
- **409 Conflict** - Email or username already exists
- **500 Internal Server Error** - Server error

## Frontend Setup (React + Vite)

### Prerequisites
- Node.js 16 or higher
- npm or yarn

### Installation

1. **Install Dependencies**
   ```bash
   cd c:\Users\ziyad\projetPfe\Movie-TV-Tracker\frontend
   npm install
   ```

2. **Run the Frontend**
   ```bash
   npm run dev
   ```
   
   The frontend will start on `http://localhost:5173`

### Frontend Features

#### Authentication Service
Located at `src/services/authService.js`

- **authService.signup(signupData)** - Create new account
- **authService.login(loginData)** - Login to existing account
- **userStorage.setUser(user)** - Store user in localStorage
- **userStorage.getUser()** - Retrieve user from localStorage
- **userStorage.removeUser()** - Clear user from localStorage
- **userStorage.isAuthenticated()** - Check if user is logged in

#### Updated Components

1. **Login Component** (`src/components/Login.jsx`)
   - Connected to backend API
   - Shows loading state during API call
   - Displays validation errors
   - Stores user data on successful login
   - Redirects to dashboard

2. **SignUp Component** (`src/components/SignUp.jsx`)
   - Connected to backend API
   - Shows loading state during API call
   - Displays validation errors
   - Stores user data on successful signup
   - Redirects to dashboard after success

## Testing the Integration

### Using the UI

1. Start both backend and frontend servers
2. Navigate to `http://localhost:5173`
3. Click "Sign up" to create a new account
4. Fill in the form and submit
5. Try logging in with the created credentials

### Using API Testing Tools (Postman/Thunder Client)

#### Test Sign Up
```bash
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "test123"
}
```

#### Test Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "test123"
}
```

## Security Features

- **Password Encryption**: Passwords are encrypted using BCrypt
- **CORS Configuration**: Configured to allow requests from `http://localhost:5173`
- **Input Validation**: Both frontend and backend validation
- **Session Management**: Stateless session management
- **Error Handling**: Comprehensive error handling and validation messages

## Technologies Used

### Backend
- Spring Boot 4.0.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- Lombok
- Bean Validation

### Frontend
- React 19
- React Router v6
- Axios
- Vite

## Troubleshooting

### Backend Issues

1. **Database Connection Failed**
   - Check PostgreSQL is running
   - Verify database credentials in `application.properties`
   - Ensure database `movie_tracker` exists

2. **Port Already in Use**
   - Change port in `application.properties`: `server.port=8081`

### Frontend Issues

1. **CORS Errors**
   - Verify backend is running on port 8080
   - Check CORS configuration in `SecurityConfig.java`

2. **Network Errors**
   - Ensure backend is running
   - Check API base URL in `authService.js`

## Next Steps

- Implement JWT token-based authentication
- Add logout functionality
- Protect routes with authentication guards
- Add password reset functionality
- Implement remember me feature
