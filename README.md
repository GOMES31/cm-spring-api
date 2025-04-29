# TaskTrackr API

This is an API for a project management application. It uses **Spring Boot**, **JWT Token** authentication, and **PostgreSQL** for the database.

---

## Prerequisites

- JDK 17+  
- Maven  
- PostgreSQL (access via PgAdmin)  

---

## 1. Database Setup (PgAdmin)

1. Open **PgAdmin** and connect to your PostgreSQL server.  
2. Right-click on **Databases** → **Create** → **Database…**  
   - **Name**: `tasktrackr_api`  
   - **Owner**: your superuser (e.g. `postgres`)  
3. Expand **Login/Group Roles** → **Create** → **Login/Group Role…**  
   - **Role Name**: `tasktrackr_admin`  
   - **Definition** → **Password**: `1234`  
4. Right-click your new role → **Properties** → **Privileges** → **Grant ALL privileges** on the `tasktrackr_api` database.  

> Hibernate will auto-generate tables on first run (ddl-auto=create).

---

## 2. Generate a JWT Secret

1. Visit [https://jwtsecret.com/generate](https://jwtsecret.com/generate) and click **Generate**.  
2. Copy the generated secret value.  

---

## 3. IntelliJ Run Configuration

1. Click the dropdown next to the **Run ▶️** button and choose **Edit…**  
2. In **Run/Debug Configurations**, click **Modify options** → **Environment variables**  
3. In the **Environment variables** field, add:
  ```text
   JWT_SECRET=<your-generated-jwt-secret>
  ```

## `application.yml` Settings

```yaml
spring:
  application:
    name: tasktrackr-api

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/tasktrackr_api
    username: tasktrackr_admin
    password: 1234

  jpa:
    hibernate:
      # Options: None, Validate, Update, Create, Create-Drop
      ddl-auto: create
    show-sql: false
    properties:
      hibernate:
        format_sql: true
    database: postgres
    database-platform: org.hibernate.dialect.PostgreSQLDialect

logging:
  level:
    org.hibernate.SQL: OFF
    org.springframework.security: DEBUG

server:
  servlet:
    context-path: /api

security:
  jwt:
    secret-key: ${JWT_SECRET}
    expiration: 86400000       # 1 day
    refresh-token:
      expiration: 604800000    # 7 days



