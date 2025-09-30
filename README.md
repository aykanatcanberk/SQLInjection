# Vulnerable / Secure Login Demo

This repository contains two authentication flows used for educational purposes: a deliberately **vulnerable** login endpoint (to demonstrate SQL injection) and a **secure** login endpoint (token-based authentication). Use this project *only* in a controlled lab environment. Unauthorized testing against systems you do not own is illegal.

---

## Project Overview

* **Vulnerable service** (`/api/vulnerable/login`)

  * Demonstrates insecure coding: user input is concatenated directly into a SQL query and executed via `EntityManager.createNativeQuery(...)`.
  * Intended to teach how SQL injection works and how to detect and exploit (in lab).
  * Example payloads can bypass authentication if the service is not protected by additional layers.

* **Secure service** (`/api/auth/*`)

  * A proper authentication flow that returns a token (e.g. JWT) on successful login and uses hashed passwords.
  * Frontend expects a token to be stored and used in `Authorization` headers for protected routes.

---

## Important Endpoints

### Vulnerable

* `POST /api/vulnerable/login`

  * Request JSON: `{ "email": "...", "password": "..." }`
  * Purpose: intentionally constructs SQL from input and executes it.
  * Use for learning how SQL injection payloads can modify queries.

Example working request that bypassed password check in the lab:

```json
POST http://localhost:8081/api/vulnerable/login
Content-Type: application/json

{
  "email": "test@example.com' -- ",
  "password": "irrelevant"
}
```

### Secure

* `POST /api/auth/register` — create a new user

  * Example body:

    ```json
    {
      "firstName": "testt",
      "lastName": "testt",
      "email": "testt3@example.com",
      "phoneNumber": "+905551234567",
      "password": "SecurePass123!"
    }
    ```

* `POST /api/auth/login` — normal (secure) login

  * Example body:

    ```json
    {
      "email": "test3@example.com",
      "password": "SecurePass123!"
    }
    ```
  * On success, this endpoint should return an authentication token which the frontend stores (encrypted) and uses in subsequent requests.

---

## Reproducing the Vulnerability (lab only)

1. Start the application and ensure the DB contains at least one user (for example `test@example.com`).
2. Use Postman or curl to send a POST to `/api/vulnerable/login` with a payload that injects SQL. Examples:

   * Bypass with comment:

     ```json
     { "email": "test@example.com' -- ", "password": "irrelevant" }
     ```

Notes:

* If the secure authentication returns tokens and the vulnerable endpoint does not produce tokens, bypassing the vulnerable endpoint alone will **not** automatically authenticate the frontend (client) against the secure routes. Tokens must be issued or client state updated for route guards to allow navigation.

---

## How to Extract Data (Educational)

* Simple `OR`/comment injections can make the `WHERE` clause true and return rows.
* `UNION SELECT` payloads can attempt to combine other query results, but you must match column counts and types; this often requires trial-and-error and knowledge of the DB.
* In this demo, the service returns only `Login successful` or `Invalid credentials`. To reveal user rows as JSON you can:

  * Modify the vulnerable service (lab-only) to include the `users` result in the response body, or
  * Tail the application logs where `getResultList()` output or generated SQL is visible.

---

## Fixes and Mitigations

To remediate the vulnerability shown here, implement one or more of the following:

1. **Parameterized queries / PreparedStatements** — never concatenate user input into SQL.

   * Use JPA repository methods or `entityManager.createNativeQuery` with bound parameters.
2. **Use ORM mapping methods** — `JpaRepository` and method-based queries (`findByEmailAndPassword`) with proper hashing.
3. **Hash passwords** — store only salted password hashes (e.g., bcrypt, Argon2). Never store plaintext passwords.
4. **Least privilege DB user** — give the DB account only necessary rights.
5. **Input validation & output encoding** — validate input lengths/types and escape output when needed.
6. **Unit / integration tests** — add tests that attempt injection payloads to assert queries are parameterized.

Example of safe repository query (still avoid native string concat):

```java
@Query(value = "SELECT * FROM users WHERE email = :email AND password = :password", nativeQuery = true)
Optional<User> vulnerableLogin(@Param("email") String email, @Param("password") String password);
```

But note: use hashed passwords and avoid comparing raw password columns.

---

## Postman / Testing Notes

* Use the included Postman collection (if provided) for convenience.
* Open the Postman Console to inspect raw request and response bodies.
* If the vulnerable endpoint returns only a status, consider modifying the service in a clone of the project to return the `users` list for demonstration purposes.

---

