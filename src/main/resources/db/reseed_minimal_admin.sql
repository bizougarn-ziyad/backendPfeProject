-- Minimal reseed for admin dashboard validation
-- Target shape:
-- - 5 users total
-- - 1 community topic
-- - 10 watched movies per user

DO $$
DECLARE
    all_tables text;
BEGIN
    SELECT string_agg(format('%I.%I', schemaname, tablename), ', ')
    INTO all_tables
    FROM pg_tables
    WHERE schemaname = 'public';

    IF all_tables IS NOT NULL THEN
        EXECUTE 'TRUNCATE TABLE ' || all_tables || ' RESTART IDENTITY CASCADE';
    END IF;
END $$;

ALTER TABLE users ADD COLUMN IF NOT EXISTS country VARCHAR(100);

-- BCrypt hash for password: pass123
INSERT INTO users (username, email, password, bio, role, is_active, is_suspended, created_at, updated_at, country)
VALUES
('admin',  'admin@test.com', '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Platform admin', 'ADMIN', TRUE, FALSE, NOW() - INTERVAL '5 days', NOW(), 'Algeria'),
('alice',  'alice@test.com', '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Movie lover', 'USER', TRUE, FALSE, NOW() - INTERVAL '4 days', NOW(), 'France'),
('bob',    'bob@test.com',   '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Sci-fi fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '3 days', NOW(), 'United States'),
('carla',  'carla@test.com', '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Drama fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '2 days', NOW(), 'Morocco'),
('david',  'david@test.com', '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Action fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '1 day', NOW(), 'Japan');

INSERT INTO content_references (id, tmdb_id, content_type, created_at)
VALUES
(gen_random_uuid(), 550,    'MOVIE', NOW()),
(gen_random_uuid(), 278,    'MOVIE', NOW()),
(gen_random_uuid(), 238,    'MOVIE', NOW()),
(gen_random_uuid(), 424,    'MOVIE', NOW()),
(gen_random_uuid(), 27205,  'MOVIE', NOW()),
(gen_random_uuid(), 157336, 'MOVIE', NOW()),
(gen_random_uuid(), 155,    'MOVIE', NOW()),
(gen_random_uuid(), 680,    'MOVIE', NOW()),
(gen_random_uuid(), 13,     'MOVIE', NOW()),
(gen_random_uuid(), 122,    'MOVIE', NOW());

-- Each of the 5 users watches all 10 movies => 50 watched rows total
INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
SELECT
    gen_random_uuid(),
    u.id,
    c.id,
    NOW() - (ROW_NUMBER() OVER (PARTITION BY u.id ORDER BY c.tmdb_id) || ' hours')::interval
FROM users u
CROSS JOIN content_references c
WHERE c.content_type = 'MOVIE';

-- Exactly 1 community topic
INSERT INTO community_topics (
    id, title, content, category, author_id,
    is_pinned, is_locked, upvote_count, reply_count, view_count,
    created_at, last_activity_at
)
SELECT
    gen_random_uuid(),
    'Welcome to the community',
    'This is the only seeded discussion topic for admin dashboard checks.',
    'MOVIES',
    u.id,
    FALSE, FALSE, 0, 0, 5,
    NOW() - INTERVAL '12 hours',
    NOW() - INTERVAL '1 hour'
FROM users u
WHERE u.username = 'alice'
LIMIT 1;
