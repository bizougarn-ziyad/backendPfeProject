-- ============================================================================
-- Movie & TV Show Tracker - PostgreSQL Database Schema
-- Production-Ready Relational Database Design (3NF)
-- ============================================================================
-- Author: Generated for Full-Stack Application
-- Database: PostgreSQL 12+
-- Features: UUID support, WebSockets ready, Real-time messaging
-- ============================================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- 1. USERS & AUTHENTICATION
-- ============================================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- BCrypt hashed
    bio TEXT,
    profile_picture_url TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    is_active BOOLEAN DEFAULT TRUE, -- Soft delete support
    email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- OAuth provider support (Google, GitHub, etc.)
CREATE TABLE oauth_providers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL CHECK (provider IN ('GOOGLE', 'GITHUB', 'FACEBOOK')),
    provider_user_id VARCHAR(255) NOT NULL, -- External user ID
    access_token TEXT, -- Encrypted in application layer
    refresh_token TEXT,
    token_expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(provider, provider_user_id),
    UNIQUE(user_id, provider) -- One provider per user
);

-- User follows (social feature)
CREATE TABLE user_follows (
    follower_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    following_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id),
    CHECK (follower_id != following_id) -- Users cannot follow themselves
);

-- Indexes for users
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_oauth_providers_user_id ON oauth_providers(user_id);
CREATE INDEX idx_oauth_providers_provider ON oauth_providers(provider, provider_user_id);
CREATE INDEX idx_user_follows_follower ON user_follows(follower_id);
CREATE INDEX idx_user_follows_following ON user_follows(following_id);

-- ============================================================================
-- 2. MOVIES (Minimal Cache)
-- ============================================================================
-- Design: Only cache essential data for list display & filtering
-- Full movie details (overview, cast, ratings) fetched from TMDB API on-demand
-- This keeps data fresh and reduces database size

CREATE TABLE movies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tmdb_id INTEGER NOT NULL UNIQUE, -- The Movie Database ID (required for API calls)
    title VARCHAR(500) NOT NULL,
    poster_url VARCHAR(500), -- For thumbnail display in lists
    release_date DATE, -- For sorting and filtering
    genre_ids INTEGER[], -- TMDB genre IDs (e.g., [28, 12, 878] for Action, Adventure, Sci-Fi)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for movies
CREATE INDEX idx_movies_tmdb_id ON movies(tmdb_id);
CREATE INDEX idx_movies_title ON movies(title);
CREATE INDEX idx_movies_release_date ON movies(release_date DESC);
CREATE INDEX idx_movies_genre_ids ON movies USING GIN (genre_ids); -- Fast array searching

-- ============================================================================
-- 3. TV SHOWS / SERIES (Minimal Cache)
-- ============================================================================
-- Design: Only cache essential data for list display & filtering
-- Full show details fetched from TMDB API on-demand

CREATE TABLE tv_shows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tmdb_id INTEGER NOT NULL UNIQUE, -- The Movie Database ID
    name VARCHAR(500) NOT NULL,
    poster_url VARCHAR(500), -- For thumbnail display in lists
    first_air_date DATE, -- For sorting and filtering
    genre_ids INTEGER[], -- TMDB genre IDs
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for TV shows
CREATE INDEX idx_tv_shows_tmdb_id ON tv_shows(tmdb_id);
CREATE INDEX idx_tv_shows_name ON tv_shows(name);
CREATE INDEX idx_tv_shows_first_air_date ON tv_shows(first_air_date DESC);
CREATE INDEX idx_tv_shows_genre_ids ON tv_shows USING GIN (genre_ids); -- Fast array searching

-- ============================================================================
-- 4. USER LISTS (Default & Custom)
-- ============================================================================
-- Design: Flexible list system supporting both default and custom lists
-- Default lists: WATCH_LATER, FAVORITES, LIKED
-- Custom lists: User-created collections

CREATE TABLE user_lists (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN DEFAULT FALSE, -- True for WATCH_LATER, FAVORITES, LIKED
    default_list_type VARCHAR(50), -- WATCH_LATER, FAVORITES, LIKED (only for default lists)
    is_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, default_list_type), -- One default list per type per user
    CHECK (
        (is_default = TRUE AND default_list_type IS NOT NULL) OR
        (is_default = FALSE AND default_list_type IS NULL)
    )
);

-- List items - Polymorphic design using content_type discriminator
-- Clean approach: Separate nullable FKs with CHECK constraint
CREATE TABLE list_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    list_id UUID NOT NULL REFERENCES user_lists(id) ON DELETE CASCADE,
    content_type VARCHAR(20) NOT NULL CHECK (content_type IN ('MOVIE', 'TV_SHOW')),
    movie_id UUID REFERENCES movies(id) ON DELETE CASCADE,
    tv_show_id UUID REFERENCES tv_shows(id) ON DELETE CASCADE,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notes TEXT, -- User's personal notes about this item
    -- Constraint: Exactly one of movie_id or tv_show_id must be set
    CHECK (
        (content_type = 'MOVIE' AND movie_id IS NOT NULL AND tv_show_id IS NULL) OR
        (content_type = 'TV_SHOW' AND tv_show_id IS NOT NULL AND movie_id IS NULL)
    ),
    -- Prevent duplicates in the same list
    UNIQUE(list_id, movie_id),
    UNIQUE(list_id, tv_show_id)
);

-- List likes (social feature - users can like public lists)
CREATE TABLE list_likes (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    list_id UUID NOT NULL REFERENCES user_lists(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, list_id)
);

-- Indexes for lists
CREATE INDEX idx_user_lists_user_id ON user_lists(user_id);
CREATE INDEX idx_user_lists_is_public ON user_lists(is_public) WHERE is_public = TRUE;
CREATE INDEX idx_user_lists_default ON user_lists(user_id, is_default, default_list_type);
CREATE INDEX idx_list_items_list ON list_items(list_id);
CREATE INDEX idx_list_items_movie ON list_items(movie_id) WHERE movie_id IS NOT NULL;
CREATE INDEX idx_list_items_tv_show ON list_items(tv_show_id) WHERE tv_show_id IS NOT NULL;
CREATE INDEX idx_list_items_content_type ON list_items(content_type);
CREATE INDEX idx_list_likes_list ON list_likes(list_id);

-- ============================================================================
-- 5. REVIEWS & RATINGS
-- ============================================================================
-- Design: Polymorphic reviews supporting both movies and TV shows
-- Constraint: One review per user per content item

CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_type VARCHAR(20) NOT NULL CHECK (content_type IN ('MOVIE', 'TV_SHOW')),
    movie_id UUID REFERENCES movies(id) ON DELETE CASCADE,
    tv_show_id UUID REFERENCES tv_shows(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 10),
    review_text TEXT,
    is_spoiler BOOLEAN DEFAULT FALSE,
    likes_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    -- Constraint: Exactly one of movie_id or tv_show_id must be set
    CHECK (
        (content_type = 'MOVIE' AND movie_id IS NOT NULL AND tv_show_id IS NULL) OR
        (content_type = 'TV_SHOW' AND tv_show_id IS NOT NULL AND movie_id IS NULL)
    ),
    -- One review per user per content item
    UNIQUE(user_id, movie_id),
    UNIQUE(user_id, tv_show_id)
);

-- Review likes (social feature)
CREATE TABLE review_likes (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, review_id)
);

-- Indexes for reviews
CREATE INDEX idx_reviews_user ON reviews(user_id);
CREATE INDEX idx_reviews_movie ON reviews(movie_id) WHERE movie_id IS NOT NULL;
CREATE INDEX idx_reviews_tv_show ON reviews(tv_show_id) WHERE tv_show_id IS NOT NULL;
CREATE INDEX idx_reviews_content_type ON reviews(content_type);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at DESC);
CREATE INDEX idx_review_likes_review ON review_likes(review_id);

-- ============================================================================
-- 6. MESSAGING SYSTEM (WebSocket Ready)
-- ============================================================================
-- Design: Supports private 1-on-1 chats and future group chats
-- Efficient unread message tracking

CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    is_group BOOLEAN DEFAULT FALSE,
    group_name VARCHAR(255), -- Only for group chats
    group_avatar_url VARCHAR(500),
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Conversation participants
CREATE TABLE conversation_participants (
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP WITH TIME ZONE, -- NULL if still active
    last_read_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP, -- For unread tracking
    is_muted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (conversation_id, user_id)
);

-- Messages
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL, -- Preserve messages if user deleted
    content TEXT NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT' CHECK (message_type IN ('TEXT', 'IMAGE', 'FILE', 'SYSTEM')),
    attachment_url VARCHAR(500), -- For images/files
    is_deleted BOOLEAN DEFAULT FALSE, -- Soft delete
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    edited_at TIMESTAMP WITH TIME ZONE
);

-- Message read receipts (optional - for "seen by" feature)
CREATE TABLE message_read_receipts (
    message_id UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    read_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id, user_id)
);

-- Indexes for messaging
CREATE INDEX idx_conversations_created_by ON conversations(created_by);
CREATE INDEX idx_conversation_participants_user ON conversation_participants(user_id);
CREATE INDEX idx_conversation_participants_conversation ON conversation_participants(conversation_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id, sent_at DESC);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at DESC);
CREATE INDEX idx_message_receipts_message ON message_read_receipts(message_id);
CREATE INDEX idx_message_receipts_user ON message_read_receipts(user_id);

-- ============================================================================
-- 7. NOTIFICATIONS (Real-Time WebSocket Support)
-- ============================================================================
-- Design: Flexible notification system for various event types

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL CHECK (
        type IN ('MESSAGE', 'FOLLOW', 'LIKE', 'REVIEW', 'LIST_PUBLISHED', 'MENTION', 'SYSTEM')
    ),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    -- Polymorphic reference to the related entity
    reference_type VARCHAR(50), -- USER, REVIEW, LIST, MESSAGE, etc.
    reference_id UUID, -- ID of the related entity
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL, -- User who triggered notification
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for notifications
CREATE INDEX idx_notifications_user ON notifications(user_id, created_at DESC);
CREATE INDEX idx_notifications_is_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_actor ON notifications(actor_id);

-- ============================================================================
-- 8. STREAMING PLATFORMS
-- ============================================================================
-- Track where content is available (Netflix, Disney+, etc.)

CREATE TABLE streaming_platforms (
    id SERIAL PRIMARY KEY,
    tmdb_id INTEGER UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(500),
    website_url VARCHAR(255)
);

-- Movie streaming availability
CREATE TABLE movie_streaming_platforms (
    movie_id UUID NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    platform_id INTEGER NOT NULL REFERENCES streaming_platforms(id) ON DELETE CASCADE,
    region_code VARCHAR(2) DEFAULT 'US', -- ISO 3166-1 alpha-2
    available_from DATE,
    available_until DATE,
    PRIMARY KEY (movie_id, platform_id, region_code)
);

-- TV Show streaming availability
CREATE TABLE tv_show_streaming_platforms (
    tv_show_id UUID NOT NULL REFERENCES tv_shows(id) ON DELETE CASCADE,
    platform_id INTEGER NOT NULL REFERENCES streaming_platforms(id) ON DELETE CASCADE,
    region_code VARCHAR(2) DEFAULT 'US',
    available_from DATE,
    available_until DATE,
    PRIMARY KEY (tv_show_id, platform_id, region_code)
);

-- Indexes for streaming platforms
CREATE INDEX idx_movie_streaming_movie ON movie_streaming_platforms(movie_id);
CREATE INDEX idx_movie_streaming_platform ON movie_streaming_platforms(platform_id);
CREATE INDEX idx_tv_show_streaming_show ON tv_show_streaming_platforms(tv_show_id);
CREATE INDEX idx_tv_show_streaming_platform ON tv_show_streaming_platforms(platform_id);

-- ============================================================================
-- 9. AUDIT LOG (Optional - For Security & Compliance)
-- ============================================================================

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL, -- LOGIN, LOGOUT, CREATE, UPDATE, DELETE
    entity_type VARCHAR(50), -- USER, REVIEW, LIST, etc.
    entity_id UUID,
    ip_address INET,
    user_agent TEXT,
    changes JSONB, -- Store what changed (for updates)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id, created_at DESC);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);

-- ============================================================================
-- 10. FUNCTIONS & TRIGGERS
-- ============================================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply updated_at trigger to relevant tables
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_oauth_providers_updated_at BEFORE UPDATE ON oauth_providers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_movies_updated_at BEFORE UPDATE ON movies
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_tv_shows_updated_at BEFORE UPDATE ON tv_shows
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_lists_updated_at BEFORE UPDATE ON user_lists
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reviews_updated_at BEFORE UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_conversations_updated_at BEFORE UPDATE ON conversations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Function to automatically create default lists for new users
CREATE OR REPLACE FUNCTION create_default_lists_for_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO user_lists (user_id, name, is_default, default_list_type, is_public)
    VALUES
        (NEW.id, 'Watch Later', TRUE, 'WATCH_LATER', FALSE),
        (NEW.id, 'Favorites', TRUE, 'FAVORITES', FALSE),
        (NEW.id, 'Liked', TRUE, 'LIKED', FALSE);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_user_default_lists AFTER INSERT ON users
    FOR EACH ROW EXECUTE FUNCTION create_default_lists_for_user();

-- Function to update review likes count
CREATE OR REPLACE FUNCTION update_review_likes_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE reviews SET likes_count = likes_count + 1 WHERE id = NEW.review_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE reviews SET likes_count = likes_count - 1 WHERE id = OLD.review_id;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_review_likes_count_trigger
AFTER INSERT OR DELETE ON review_likes
FOR EACH ROW EXECUTE FUNCTION update_review_likes_count();

-- ============================================================================
-- 11. VIEWS FOR COMMON QUERIES
-- ============================================================================

-- View: User statistics
CREATE OR REPLACE VIEW user_statistics AS
SELECT
    u.id,
    u.username,
    COUNT(DISTINCT r.id) AS total_reviews,
    COUNT(DISTINCT ul.id) FILTER (WHERE ul.is_default = FALSE AND ul.is_public = TRUE) AS public_lists_count,
    COUNT(DISTINCT f1.follower_id) AS followers_count,
    COUNT(DISTINCT f2.following_id) AS following_count
FROM users u
LEFT JOIN reviews r ON u.id = r.user_id
LEFT JOIN user_lists ul ON u.id = ul.user_id
LEFT JOIN user_follows f1 ON u.id = f1.following_id
LEFT JOIN user_follows f2 ON u.id = f2.follower_id
GROUP BY u.id, u.username;

-- View: Unread messages count per conversation
CREATE OR REPLACE VIEW unread_messages_count AS
SELECT
    cp.user_id,
    cp.conversation_id,
    COUNT(m.id) AS unread_count
FROM conversation_participants cp
LEFT JOIN messages m ON m.conversation_id = cp.conversation_id
    AND m.sent_at > cp.last_read_at
    AND m.sender_id != cp.user_id
    AND m.is_deleted = FALSE
WHERE cp.left_at IS NULL
GROUP BY cp.user_id, cp.conversation_id;

-- View: Popular movies (with review stats)
CREATE OR REPLACE VIEW popular_movies AS
SELECT
    m.*,
    COUNT(DISTINCT r.id) AS review_count,
    COALESCE(AVG(r.rating), 0) AS avg_user_rating
FROM movies m
LEFT JOIN reviews r ON m.id = r.movie_id
GROUP BY m.id
ORDER BY m.popularity DESC;

-- View: Popular TV shows (with review stats)
CREATE OR REPLACE VIEW popular_tv_shows AS
SELECT
    t.*,
    COUNT(DISTINCT r.id) AS review_count,
    COALESCE(AVG(r.rating), 0) AS avg_user_rating
FROM tv_shows t
LEFT JOIN reviews r ON t.id = r.tv_show_id
GROUP BY t.id
ORDER BY t.popularity DESC;

-- ============================================================================
-- 12. COMMENTS & DESIGN DECISIONS
-- ============================================================================

/*
DESIGN DECISIONS & RATIONALE:

1. UUID vs SERIAL for Primary Keys:
   - UUIDs used for user-facing entities (users, lists, reviews)
   - Benefits: No sequential ID enumeration, easier distributed systems, API security
   - SERIAL used for reference data (platforms) for smaller index size

2. Polymorphic Design (list_items, reviews):
   - Used content_type discriminator + nullable FKs approach
   - Alternative: Separate junction tables (more normalized but more joins)
   - CHECK constraints ensure data integrity
   - Indexes on nullable FKs with WHERE clause for efficiency

3. ON DELETE Rules:
   - CASCADE: When parent deletion should remove children (user -> lists, conversation -> messages)
   - SET NULL: When data should be preserved (message sender deletion)
   - Careful consideration for each relationship

4. Soft Delete vs Hard Delete:
   - users.is_active for soft delete (preserve message history, reviews)
   - messages.is_deleted for user-initiated message deletion
   - Hard delete allowed for lists, list_items (user control)

5. WebSocket Support:
   - conversation_participants.last_read_at enables unread count without complex queries
   - messages indexed by (conversation_id, sent_at) for efficient pagination
   - Notifications table structure supports real-time push

6. Performance Optimizations:
   - Composite indexes on frequently queried combinations
   - Partial indexes (WHERE clause) on filtered queries
   - Denormalized likes_count on reviews (with trigger maintenance)
   - Views for complex aggregations

7. Scalability Considerations:
   - Timestamps with time zone for global user base
   - Region-aware streaming platform availability
   - Audit logs with JSONB for flexible tracking
   - Prepared for partitioning on messages, notifications by date

8. Security:
   - Password stored as hash (BCrypt in application layer)
   - OAuth tokens should be encrypted in application
   - Audit logs capture sensitive operations
   - CHECK constraints prevent invalid states

9. Default Lists Strategy:
   - Automatically created via trigger when user registers
   - default_list_type ensures uniqueness
   - Same table structure for custom and default lists (flexible)

10. Future Extensibility:
    - Easy to add: Comments on reviews, TV show seasons/episodes tracking
    - Conversation system supports group chats
    - Notification system handles new types easily
    - Genres stored as TMDB ID arrays for flexible filtering

11. Minimal Caching Strategy:
    - Movies/TV shows cache only essential display data (title, poster, release date)
    - Full details (overview, ratings, cast) fetched from TMDB API on-demand
    - Keeps data fresh, reduces database size, simplifies maintenance
    - Genre IDs stored as arrays with GIN indexes for fast filtering
    - Industry standard approach (used by Netflix, IMDb, YouTube)

12. Indexes Strategy:
    - All foreign keys indexed for join performance
    - Unique constraints automatically create indexes
    - Composite indexes for common query patterns
    - DESC indexes on timestamp columns for recent-first queries

13. Data Integrity:
    - NOT NULL enforced where appropriate
    - CHECK constraints for enums and business rules
    - UNIQUE constraints prevent duplicates
    - Foreign keys maintain referential integrity
*/

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================

-- To initialize with sample data, see separate seed.sql file
