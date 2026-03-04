# Reset Database with Seed Data

## Steps to reset your database with proper seed data:

### 1. Stop your Spring Boot application

### 2. Connect to PostgreSQL and drop/recreate database

```sql
-- Connect to postgres as superuser
DROP DATABASE IF EXISTS movie_tracker;
CREATE DATABASE movie_tracker;
```

### 3. Update application.properties temporarily

Change `ddl-auto` to let Hibernate create tables:

```properties
spring.jpa.hibernate.ddl-auto=create
```

### 4. Start Spring Boot application

This will create all tables from your entities.

### 5. Run seed.sql

Execute the seed.sql file to populate data:

```bash
# From project root
psql -U postgres -d movie_tracker -f src/main/resources/db/seed.sql
```

OR use a PostgreSQL client (pgAdmin, DBeaver) to run the seed.sql file.

### 6. Change ddl-auto back to update

```properties
spring.jpa.hibernate.ddl-auto=update
```

### 7. Restart application

## Quick Test

After seeding, test with:
- User #1: johndoe
- User #7: ziyadbz666 (YOU!)
- Password for all: `password123`

Visit: http://localhost:5173/user/7
