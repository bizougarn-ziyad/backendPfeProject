# Movie & TV Show Tracker - JPA Entity Documentation

## 📦 Project Structure

```
com.projection.entity
├── enums/
│   ├── Role.java
│   ├── ContentType.java
│   └── NotificationType.java
├── user/
│   ├── User.java
│   └── UserFollow.java
├── content/
│   └── ContentReference.java
├── list/
│   ├── UserList.java
│   ├── ListItem.java
│   └── ListLike.java
├── review/
│   ├── Review.java
│   └── ReviewLike.java
├── messaging/
│   ├── Conversation.java
│   └── Message.java
└── notification/
    └── Notification.java
```

## 📊 Entity Overview

### 1️⃣ **User** (`users` table)
Core user entity with authentication and profile information.

**Fields:**
- `id` (UUID) - Primary key
- `username` (String, unique) - User's display name
- `email` (String, unique) - Login email
- `password` (String) - Hashed password (BCrypt recommended)
- `bio` (Text) - User bio
- `profilePictureUrl` (String) - Profile image URL
- `role` (Enum: USER, ADMIN) - User role
- `isActive` (Boolean) - Account status
- `createdAt` (LocalDateTime) - Registration date
- `updatedAt` (LocalDateTime) - Last profile update
- `lastLogin` (LocalDateTime) - Last login timestamp

**Indexes:**
- `idx_users_email` on `email`
- `idx_users_username` on `username`

---

### 2️⃣ **ContentReference** (`content_references` table)
Lightweight reference to TMDB content (Movie or TV Show). **Does NOT store full metadata.**

**Fields:**
- `id` (UUID) - Primary key
- `tmdbId` (Long) - TMDB API ID
- `contentType` (Enum: MOVIE, TV) - Content type
- `createdAt` (LocalDateTime) - First reference date

**Constraints:**
- Unique constraint on `(tmdbId, contentType)`

**Indexes:**
- `idx_content_tmdb_id` on `tmdb_id`
- `idx_content_type` on `content_type`

---

### 3️⃣ **UserList** (`user_lists` table)
User-created collections of movies/TV shows.

**Fields:**
- `id` (UUID) - Primary key
- `user` (ManyToOne User) - List owner
- `name` (String) - List name
- `description` (Text) - List description
- `isDefault` (Boolean) - System-generated list (e.g., "Favorites", "Watch Later")
- `isPublic` (Boolean) - Public visibility
- `createdAt` (LocalDateTime) - Creation date
- `updatedAt` (LocalDateTime) - Last modification

**Relationships:**
- `OneToMany` with `ListItem`

**Indexes:**
- `idx_user_lists_user_id` on `user_id`
- `idx_user_lists_is_public` on `is_public`

---

### 4️⃣ **ListItem** (`list_items` table)
Individual content items within a user list.

**Fields:**
- `id` (UUID) - Primary key
- `userList` (ManyToOne UserList) - Parent list
- `contentReference` (ManyToOne ContentReference) - Referenced content
- `notes` (Text) - User notes for this item
- `addedAt` (LocalDateTime) - Date added to list

**Constraints:**
- Unique constraint on `(userList, contentReference)` - prevents duplicates

**Indexes:**
- `idx_list_items_list_id` on `list_id`
- `idx_list_items_content_id` on `content_reference_id`

---

### 5️⃣ **Review** (`reviews` table)
User reviews for movies/TV shows.

**Fields:**
- `id` (UUID) - Primary key
- `user` (ManyToOne User) - Review author
- `contentReference` (ManyToOne ContentReference) - Reviewed content
- `rating` (Integer, 1-10) - User rating (validated)
- `reviewText` (Text) - Review content
- `likesCount` (Integer) - Denormalized like count
- `createdAt` (LocalDateTime) - Review creation date
- `updatedAt` (LocalDateTime) - Last edit date

**Constraints:**
- Unique constraint on `(user, contentReference)` - one review per user per content
- `@Min(1)` and `@Max(10)` validation on rating

**Indexes:**
- `idx_reviews_user_id` on `user_id`
- `idx_reviews_content_id` on `content_reference_id`
- `idx_reviews_rating` on `rating`

---

### 6️⃣ **Conversation** (`conversations` table)
Chat conversations between users (supports 1-on-1 and group chats).

**Fields:**
- `id` (UUID) - Primary key
- `name` (String) - Optional conversation name (for group chats)
- `isGroup` (Boolean) - Group chat flag
- `participants` (ManyToMany User) - Conversation members
- `createdAt` (LocalDateTime) - Conversation creation date

**Relationships:**
- `ManyToMany` with `User` via `conversation_participants` join table
- `OneToMany` with `Message`

**Indexes:**
- `idx_conversations_created_at` on `created_at`
- `idx_conv_participants_conv_id` on `conversation_id` (join table)
- `idx_conv_participants_user_id` on `user_id` (join table)

---

### 7️⃣ **Message** (`messages` table)
Individual messages within conversations (WebSocket ready).

**Fields:**
- `id` (UUID) - Primary key
- `conversation` (ManyToOne Conversation) - Parent conversation
- `sender` (ManyToOne User) - Message sender
- `content` (Text) - Message content
- `isRead` (Boolean) - Read status
- `isDeleted` (Boolean) - Soft delete flag
- `sentAt` (LocalDateTime) - Message timestamp

**Indexes:**
- `idx_messages_conversation_id` on `conversation_id`
- `idx_messages_sender_id` on `sender_id`
- `idx_messages_sent_at` on `sent_at`

---

### 8️⃣ **Notification** (`notifications` table)
User notifications for various events.

**Fields:**
- `id` (UUID) - Primary key
- `user` (ManyToOne User) - Notification recipient
- `type` (Enum: MESSAGE, FOLLOW, LIKE, REVIEW, LIST_PUBLISHED)
- `title` (String) - Notification title
- `message` (Text) - Notification message
- `referenceId` (UUID) - Reference to related entity
- `isRead` (Boolean) - Read status
- `createdAt` (LocalDateTime) - Notification timestamp

**Indexes:**
- `idx_notifications_user_id` on `user_id`
- `idx_notifications_is_read` on `is_read`
- `idx_notifications_created_at` on `created_at`

---

### 9️⃣ **UserFollow** (`user_follows` table)
Social following relationships between users.

**Fields:**
- `id` (UUID) - Primary key
- `follower` (ManyToOne User) - User who follows
- `following` (ManyToOne User) - User being followed
- `createdAt` (LocalDateTime) - Follow date

**Constraints:**
- Unique constraint on `(follower, following)` - prevents duplicate follows

**Indexes:**
- `idx_user_follows_follower_id` on `follower_id`
- `idx_user_follows_following_id` on `following_id`

---

### 🔟 **ReviewLike** (`review_likes` table)
Like interactions on reviews.

**Fields:**
- `id` (UUID) - Primary key
- `user` (ManyToOne User) - User who liked
- `review` (ManyToOne Review) - Liked review
- `createdAt` (LocalDateTime) - Like timestamp

**Constraints:**
- Unique constraint on `(user, review)` - one like per user per review

**Indexes:**
- `idx_review_likes_user_id` on `user_id`
- `idx_review_likes_review_id` on `review_id`

---

### 1️⃣1️⃣ **ListLike** (`list_likes` table)
Like interactions on user lists.

**Fields:**
- `id` (UUID) - Primary key
- `user` (ManyToOne User) - User who liked
- `userList` (ManyToOne UserList) - Liked list
- `createdAt` (LocalDateTime) - Like timestamp

**Constraints:**
- Unique constraint on `(user, userList)` - one like per user per list

**Indexes:**
- `idx_list_likes_user_id` on `user_id`
- `idx_list_likes_list_id` on `list_id`

---

## 🔗 Entity Relationships Diagram

```
User
├── OneToMany → UserList
├── OneToMany → Review
├── OneToMany → Notification
├── ManyToMany → Conversation (participants)
├── OneToMany → Message (as sender)
├── OneToMany → UserFollow (as follower)
├── OneToMany → UserFollow (as following)
├── OneToMany → ReviewLike
└── OneToMany → ListLike

ContentReference
├── OneToMany → ListItem
└── OneToMany → Review

UserList
├── ManyToOne → User
├── OneToMany → ListItem
└── OneToMany → ListLike

Review
├── ManyToOne → User
├── ManyToOne → ContentReference
└── OneToMany → ReviewLike

Conversation
├── ManyToMany → User (participants)
└── OneToMany → Message

Message
├── ManyToOne → Conversation
└── ManyToOne → User (sender)
```

---

## ⚙️ Configuration

### application.properties
```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/movie_tracker
spring.datasource.username=postgres
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Database Setup
1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE movie_tracker;
   ```

2. Run the schema from `src/main/resources/db/schema.sql`

3. (Optional) Run seed data from `src/main/resources/db/seed.sql`

---

## 📝 Key Design Decisions

### ✅ What This Schema DOES Store:
- User accounts & profiles
- Social interactions (follows, likes)
- User-created lists
- Reviews & ratings
- Direct messages & conversations
- Notifications
- References to TMDB content (ID only)

### ❌ What This Schema DOES NOT Store:
- Full movie/TV metadata (title, plot, cast, etc.)
- Movie posters, trailers, images
- Genre definitions
- Streaming platform availability

**Rationale:** All movie/TV data comes from TMDB API on-demand. Only store TMDB IDs for reference.

---

## 🚀 Next Steps

1. **Create Repositories** - JPA repository interfaces for each entity
2. **Create Services** - Business logic layer
3. **Create Controllers** - REST API endpoints
4. **TMDB Integration** - Service to fetch movie/TV data
5. **WebSocket Configuration** - Real-time messaging
6. **Security Configuration** - JWT authentication
7. **DTOs** - Data transfer objects for API responses

---

## 🛡️ Best Practices Implemented

✅ UUID primary keys for security  
✅ Lazy loading by default (avoid N+1 queries)  
✅ `@JsonIgnore` to prevent circular serialization  
✅ Unique constraints on natural keys  
✅ Proper indexes on foreign keys & query columns  
✅ Bean Validation annotations (`@Min`, `@Max`, `@NotNull`)  
✅ Audit timestamps (`@PrePersist`, `@PreUpdate`)  
✅ Lombok for boilerplate reduction  
✅ Named foreign key constraints  
✅ Cascade rules configured appropriately  

---

## 📚 Dependencies

```xml
<!-- Core -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<!-- Utilities -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- WebSocket -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

## 📄 License
This project structure follows Spring Boot & JPA best practices.
