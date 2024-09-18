# Assignment
This is a sample project to demonstrate how to implement a simple authentication system using Spring Boot and JWT.

Your task is to improve the security of this system by implementing the following feature:
- **Brute Force Protection**: Block a user from making further login attempts if they fail to login 3 times.
    Release this block after 5 seconds (for simplicity).

## Hints
There is a `LoginAttemptAspect` class that you can use to implement the brute force protection feature.
If is an "around" Spring AOP advice that intercepts the login endpoint. Follow the hints in the `LoginAttemptAspect` class to implement the logic.

There is a `LoginAttemptService` class that you can use to keep track of login attempts. 
You can use an in-memory data structure to keep track of the login attempts.

Run the integration tests in `AuthenticationControllerTest` to verify that the feature works as expected. 

## Endpoints

Here are the description of the endpoints in this project:

| API route | Access status | Description |
| --- | --- | --- |
| [POST] /auth/signup | Unprotected | Register a new user |
| [POST] /auth/login | Unprotected | Authenticate a user |
| [GET] /users/me | Protected | Retrieve the current authenticated user |
| [GET] /users | Protected | Retrieve all the users |

## JWT Token Decoder Tool

Use an online tool https://jwt.io/ to decode the JWT token.