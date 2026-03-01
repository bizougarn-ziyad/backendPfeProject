-- ============================================================================
-- Movie & TV Show Tracker - Sample Data (seed.sql)
-- ============================================================================
-- Purpose: Populate database with test data for development
-- Run after: schema.sql
-- ============================================================================

-- Clear existing data (be careful in production!)
-- TRUNCATE users, movies, tv_shows, genres CASCADE;

-- ============================================================================
-- 1. INSERT USERS
-- ============================================================================
-- Password for all test users: "password123"
-- Hashed with BCrypt: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe
-- NOTE: Using auto-increment IDs to match User entity (Long with IDENTITY strategy)

INSERT INTO users (username, email, password, bio, role, is_active, created_at, updated_at) VALUES
('johndoe', 'john@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Movie enthusiast and critic. Love sci-fi and thrillers!', 'USER', TRUE, NOW(), NOW()),
('janesmith', 'jane@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'TV show addict. Currently binge-watching everything!', 'USER', TRUE, NOW(), NOW()),
('admin', 'admin@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Platform administrator', 'ADMIN', TRUE, NOW(), NOW()),
('moviebuff', 'buff@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Watching movies since 1990. Classic cinema lover.', 'USER', TRUE, NOW(), NOW()),
('cinephile', 'cine@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Art house and indie film aficionado. Cannes Film Festival regular.', 'USER', TRUE, NOW(), NOW()),
('actionfan', 'action@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Explosions, car chases, and martial arts. Living for the adrenaline!', 'USER', TRUE, NOW(), NOW()),
('ziyadbz666', 'ziyad@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Full-stack developer and movie enthusiast!', 'USER', TRUE, NOW(), NOW()),
('horrorlover', 'horror@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Fear is my favorite emotion. Horror movies since childhood.', 'USER', TRUE, NOW(), NOW()),
('comicfan', 'comics@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Marvel and DC enthusiast. Superhero movies are my jam!', 'USER', TRUE, NOW(), NOW()),
('animelover', 'anime@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Anime and Japanese cinema. Studio Ghibli forever!', 'USER', TRUE, NOW(), NOW()),
('documentary', 'docs@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Real stories, real impact. Documentary filmmaker and viewer.', 'USER', TRUE, NOW(), NOW()),
('romcom', 'romcom@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Romantic comedies are my guilty pleasure. Love stories all day!', 'USER', TRUE, NOW(), NOW()),
('scifiguru', 'scifi@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', 'Science fiction fanatic. From Star Wars to Blade Runner.', 'USER', TRUE, NOW(), NOW());

-- Note: Default lists (WATCH_LATER, FAVORITES, LIKED) are auto-created by trigger

-- ============================================================================
-- 2. INSERT MOVIES (Minimal Cache)
-- ============================================================================
-- Note: Only essential data is cached. Full details fetched from TMDB API on-demand
-- genre_ids format: TMDB genre IDs as array
-- Common genres: 28=Action, 12=Adventure, 18=Drama, 80=Crime, 53=Thriller, 878=Sci-Fi

INSERT INTO movies (id, tmdb_id, title, poster_url, release_date, genre_ids) VALUES
(
    'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
    550,
    'Fight Club',
    '/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg',
    '1999-10-15',
    ARRAY[18] -- Drama
),
(
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    278,
    'The Shawshank Redemption',
    '/q6y0Go1tsGEsmtFryDOJo3dEmqu.jpg',
    '1994-09-23',
    ARRAY[18, 80] -- Drama, Crime
),
(
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    238,
    'The Godfather',
    '/3bhkrj58Vtu7enYsRolD1fZdja1.jpg',
    '1972-03-14',
    ARRAY[18, 80] -- Drama, Crime
),
(
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    424,
    'The Dark Knight',
    '/qJ2tW6WMUDux911r6m7haRef0WH.jpg',
    '2008-07-16',
    ARRAY[28, 80, 18] -- Action, Crime, Drama
),
(
    'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
    680,
    'Pulp Fiction',
    '/d5iIlFn5s0ImszYzBPb8JPIfbXD.jpg',
    '1994-10-14',
    ARRAY[53, 80] -- Thriller, Crime
);

-- ============================================================================
-- 3. INSERT TV SHOWS (Minimal Cache)
-- ============================================================================
-- Only caching essential data. Full details fetched from TMDB API on-demand.
-- genre_ids: TMDB TV genre IDs as array (18=Drama, 80=Crime, 10759=Action&Adventure, 10765=Sci-Fi&Fantasy, etc.)

INSERT INTO tv_shows (id, tmdb_id, name, poster_url, first_air_date, genre_ids) VALUES
(
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    1396,
    'Breaking Bad',
    '/ggFHVNu6YYI5L9pCfOacjizRGt.jpg',
    '2008-01-20',
    ARRAY[18, 80] -- Drama, Crime
),
(
    'gggggggg-gggg-gggg-gggg-gggggggggggg',
    1399,
    'Game of Thrones',
    '/1XS1oqL89opfnbLl8WnZY1O1uJx.jpg',
    '2011-04-17',
    ARRAY[18, 10765, 10759] -- Drama, Sci-Fi & Fantasy, Action & Adventure
),
(
    'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh',
    60735,
    'The Flash',
    '/lJA2RCMfsWoskqlQhXPSLFQGXEJ.jpg',
    '2014-10-07',
    ARRAY[10765, 10759, 18] -- Sci-Fi & Fantasy, Action & Adventure, Drama
),
(
    'iiiiiiii-iiii-iiii-iiii-iiiiiiiiiiii',
    1402,
    'The Walking Dead',
    '/xf9wuDcqlUPWABZNeDKPbZUjWx0.jpg',
    '2010-10-31',
    ARRAY[18, 10765, 10759] -- Drama, Sci-Fi & Fantasy, Action & Adventure
);

-- ============================================================================
-- 4. USER FOLLOWS
-- ============================================================================

INSERT INTO user_follows (follower_id, following_id) VALUES
-- johndoe follows janesmith and moviebuff
('11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222'),
('11111111-1111-1111-1111-111111111111', '44444444-4444-4444-4444-444444444444'),
-- janesmith follows johndoe
('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111'),
-- moviebuff follows everyone
('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111'),
('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222'),
('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333');

-- ============================================================================
-- 5. ADD ITEMS TO USER LISTS
-- ============================================================================

-- Get list IDs for users (since they were auto-created)
-- johndoe's lists
DO $$
DECLARE
    johndoe_watch_later UUID;
    johndoe_favorites UUID;
    janesmith_favorites UUID;
BEGIN
    -- Get johndoe's Watch Later list
    SELECT id INTO johndoe_watch_later 
    FROM user_lists 
    WHERE user_id = '11111111-1111-1111-1111-111111111111' 
      AND default_list_type = 'WATCH_LATER';
    
    -- Add movies to johndoe's Watch Later
    INSERT INTO list_items (list_id, content_type, movie_id, notes) VALUES
    (johndoe_watch_later, 'MOVIE', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Must watch this classic!'),
    (johndoe_watch_later, 'MOVIE', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Heard it''s amazing');
    
    -- Add TV show to johndoe's Watch Later
    INSERT INTO list_items (list_id, content_type, tv_show_id) VALUES
    (johndoe_watch_later, 'TV_SHOW', 'ffffffff-ffff-ffff-ffff-ffffffffffff');
    
    -- Get johndoe's Favorites list
    SELECT id INTO johndoe_favorites 
    FROM user_lists 
    WHERE user_id = '11111111-1111-1111-1111-111111111111' 
      AND default_list_type = 'FAVORITES';
    
    -- Add to johndoe's Favorites
    INSERT INTO list_items (list_id, content_type, movie_id) VALUES
    (johndoe_favorites, 'MOVIE', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
    (johndoe_favorites, 'MOVIE', 'cccccccc-cccc-cccc-cccc-cccccccccccc');
    
    -- Get janesmith's Favorites list
    SELECT id INTO janesmith_favorites 
    FROM user_lists 
    WHERE user_id = '22222222-2222-2222-2222-222222222222' 
      AND default_list_type = 'FAVORITES';
    
    -- Add to janesmith's Favorites
    INSERT INTO list_items (list_id, content_type, tv_show_id) VALUES
    (janesmith_favorites, 'TV_SHOW', 'gggggggg-gggg-gggg-gggg-gggggggggggg'),
    (janesmith_favorites, 'TV_SHOW', 'hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh');
END $$;

-- ============================================================================
-- 6. CREATE CUSTOM LISTS
-- ============================================================================

INSERT INTO user_lists (id, user_id, name, description, is_default, is_public) VALUES
(
    'list1111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'Best Crime Movies',
    'My personal collection of the best crime movies ever made',
    FALSE,
    TRUE
),
(
    'list2222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222222',
    'Must-Watch TV Shows',
    'Top TV shows everyone should watch',
    FALSE,
    TRUE
),
(
    'list3333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444',
    'Classic Cinema',
    'Timeless classics from the golden age',
    FALSE,
    TRUE
);

-- Add items to custom lists
INSERT INTO list_items (list_id, content_type, movie_id) VALUES
-- johndoe's Best Crime Movies
('list1111-1111-1111-1111-111111111111', 'MOVIE', 'cccccccc-cccc-cccc-cccc-cccccccccccc'),
('list1111-1111-1111-1111-111111111111', 'MOVIE', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee'),
-- moviebuff's Classic Cinema
('list3333-3333-3333-3333-333333333333', 'MOVIE', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'),
('list3333-3333-3333-3333-333333333333', 'MOVIE', 'cccccccc-cccc-cccc-cccc-cccccccccccc');

INSERT INTO list_items (list_id, content_type, tv_show_id) VALUES
-- janesmith's Must-Watch TV Shows
('list2222-2222-2222-2222-222222222222', 'TV_SHOW', 'ffffffff-ffff-ffff-ffff-ffffffffffff'),
('list2222-2222-2222-2222-222222222222', 'TV_SHOW', 'gggggggg-gggg-gggg-gggg-gggggggggggg');

-- ============================================================================
-- 7. LIST LIKES
-- ============================================================================

INSERT INTO list_likes (user_id, list_id) VALUES
-- janesmith likes johndoe's crime movies list
('22222222-2222-2222-2222-222222222222', 'list1111-1111-1111-1111-111111111111'),
-- johndoe likes janesmith's TV shows list
('11111111-1111-1111-1111-111111111111', 'list2222-2222-2222-2222-222222222222'),
-- moviebuff likes both
('44444444-4444-4444-4444-444444444444', 'list1111-1111-1111-1111-111111111111'),
('44444444-4444-4444-4444-444444444444', 'list2222-2222-2222-2222-222222222222');

-- ============================================================================
-- 8. REVIEWS
-- ============================================================================

INSERT INTO reviews (id, user_id, content_type, movie_id, rating, review_text, is_spoiler) VALUES
(
    'rev11111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'MOVIE',
    'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
    10,
    'An absolute masterpiece! The storytelling, acting, and direction are all perfect. A must-watch for any cinema lover.',
    FALSE
),
(
    'rev22222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'MOVIE',
    'cccccccc-cccc-cccc-cccc-cccccccccccc',
    9,
    'One of the greatest films ever made. Brando''s performance is legendary.',
    FALSE
),
(
    'rev33333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444',
    'MOVIE',
    'dddddddd-dddd-dddd-dddd-dddddddddddd',
    10,
    'Heath Ledger''s Joker is unforgettable. This redefined superhero movies.',
    FALSE
);

INSERT INTO reviews (id, user_id, content_type, tv_show_id, rating, review_text, is_spoiler) VALUES
(
    'rev44444-4444-4444-4444-444444444444',
    '22222222-2222-2222-2222-222222222222',
    'TV_SHOW',
    'ffffffff-ffff-ffff-ffff-ffffffffffff',
    10,
    'Breaking Bad is a masterclass in storytelling. Every episode is perfectly crafted.',
    FALSE
),
(
    'rev55555-5555-5555-5555-555555555555',
    '22222222-2222-2222-2222-222222222222',
    'TV_SHOW',
    'gggggggg-gggg-gggg-gggg-gggggggggggg',
    8,
    'Epic fantasy series with incredible production. The ending was controversial but overall amazing.',
    FALSE
);

-- ============================================================================
-- 9. REVIEW LIKES
-- ============================================================================

INSERT INTO review_likes (user_id, review_id) VALUES
-- People like johndoe's Shawshank review
('22222222-2222-2222-2222-222222222222', 'rev11111-1111-1111-1111-111111111111'),
('44444444-4444-4444-4444-444444444444', 'rev11111-1111-1111-1111-111111111111'),
-- People like janesmith's Breaking Bad review
('11111111-1111-1111-1111-111111111111', 'rev44444-4444-4444-4444-444444444444'),
('44444444-4444-4444-4444-444444444444', 'rev44444-4444-4444-4444-444444444444');

-- ============================================================================
-- 10. CONVERSATIONS & MESSAGES
-- ============================================================================

-- Private conversation between johndoe and janesmith
INSERT INTO conversations (id, is_group, created_by) VALUES
(
    'conv1111-1111-1111-1111-111111111111',
    FALSE,
    '11111111-1111-1111-1111-111111111111'
);

-- Add participants
INSERT INTO conversation_participants (conversation_id, user_id, last_read_at) VALUES
('conv1111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', NOW()),
('conv1111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '5 minutes');

-- Messages
INSERT INTO messages (id, conversation_id, sender_id, content, message_type, sent_at) VALUES
(
    'msg11111-1111-1111-1111-111111111111',
    'conv1111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'Hey Jane! Have you seen Breaking Bad? I just started watching it.',
    'TEXT',
    NOW() - INTERVAL '30 minutes'
),
(
    'msg22222-2222-2222-2222-222222222222',
    'conv1111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'Yes! It''s amazing! You''re in for a treat. Which episode are you on?',
    'TEXT',
    NOW() - INTERVAL '25 minutes'
),
(
    'msg33333-3333-3333-3333-333333333333',
    'conv1111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'Just finished season 1. Already hooked!',
    'TEXT',
    NOW() - INTERVAL '20 minutes'
),
(
    'msg44444-4444-4444-4444-444444444444',
    'conv1111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222',
    'Wait till you get to season 4! 🔥',
    'TEXT',
    NOW() - INTERVAL '5 minutes'
);

-- ============================================================================
-- 11. NOTIFICATIONS
-- ============================================================================

INSERT INTO notifications (id, user_id, type, title, content, reference_type, reference_id, actor_id, is_read) VALUES
(
    'notif111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    'FOLLOW',
    'New Follower',
    'janesmith started following you',
    'USER',
    '22222222-2222-2222-2222-222222222222',
    '22222222-2222-2222-2222-222222222222',
    TRUE
),
(
    'notif222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'LIKE',
    'List Liked',
    'moviebuff liked your list "Best Crime Movies"',
    'LIST',
    'list1111-1111-1111-1111-111111111111',
    '44444444-4444-4444-4444-444444444444',
    FALSE
),
(
    'notif333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222',
    'MESSAGE',
    'New Message',
    'johndoe sent you a message',
    'MESSAGE',
    'msg11111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    TRUE
),
(
    'notif444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    'MESSAGE',
    'New Message',
    'janesmith sent you a message',
    'MESSAGE',
    'msg44444-4444-4444-4444-444444444444',
    '22222222-2222-2222-2222-222222222222',
    FALSE
);

-- ============================================================================
-- 12. STREAMING PLATFORMS
-- ============================================================================

INSERT INTO streaming_platforms (tmdb_id, name, logo_url) VALUES
(8, 'Netflix', '/path/to/netflix-logo.png'),
(337, 'Disney Plus', '/path/to/disney-logo.png'),
(2, 'Apple TV Plus', '/path/to/appletv-logo.png'),
(119, 'Amazon Prime Video', '/path/to/prime-logo.png'),
(384, 'HBO Max', '/path/to/hbo-logo.png'),
(15, 'Hulu', '/path/to/hulu-logo.png');

-- Assign movies to platforms
INSERT INTO movie_streaming_platforms (movie_id, platform_id, region_code) VALUES
-- Fight Club on Netflix
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', (SELECT id FROM streaming_platforms WHERE name = 'Netflix'), 'US'),
-- Shawshank on Netflix and Prime
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', (SELECT id FROM streaming_platforms WHERE name = 'Netflix'), 'US'),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', (SELECT id FROM streaming_platforms WHERE name = 'Amazon Prime Video'), 'US'),
-- Dark Knight on HBO Max
('dddddddd-dddd-dddd-dddd-dddddddddddd', (SELECT id FROM streaming_platforms WHERE name = 'HBO Max'), 'US');

-- Assign TV shows to platforms
INSERT INTO tv_show_streaming_platforms (tv_show_id, platform_id, region_code) VALUES
-- Breaking Bad on Netflix
('ffffffff-ffff-ffff-ffff-ffffffffffff', (SELECT id FROM streaming_platforms WHERE name = 'Netflix'), 'US'),
-- Game of Thrones on HBO Max
('gggggggg-gggg-gggg-gggg-gggggggggggg', (SELECT id FROM streaming_platforms WHERE name = 'HBO Max'), 'US'),
-- The Flash on Netflix
('hhhhhhhh-hhhh-hhhh-hhhh-hhhhhhhhhhhh', (SELECT id FROM streaming_platforms WHERE name = 'Netflix'), 'US');

-- ============================================================================
-- 13. AUDIT LOG EXAMPLES
-- ============================================================================

INSERT INTO audit_logs (user_id, action, entity_type, entity_id, changes) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'CREATE',
    'LIST',
    'list1111-1111-1111-1111-111111111111',
    '{"name": "Best Crime Movies", "is_public": true}'::jsonb
),
(
    '11111111-1111-1111-1111-111111111111',
    'CREATE',
    'REVIEW',
    'rev11111-1111-1111-1111-111111111111',
    '{"movie_id": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", "rating": 10}'::jsonb
);

-- ============================================================================
-- VERIFICATION QUERIES
-- ============================================================================

-- Verify data
SELECT 'Users' AS table_name, COUNT(*) AS count FROM users
UNION ALL
SELECT 'Movies', COUNT(*) FROM movies
UNION ALL
SELECT 'TV Shows', COUNT(*) FROM tv_shows
UNION ALL
SELECT 'Genres', COUNT(*) FROM genres
UNION ALL
SELECT 'User Lists', COUNT(*) FROM user_lists
UNION ALL
SELECT 'List Items', COUNT(*) FROM list_items
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Messages', COUNT(*) FROM messages
UNION ALL
SELECT 'Notifications', COUNT(*) FROM notifications;

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================

-- You can query the data like:
-- SELECT * FROM user_statistics;
-- SELECT * FROM popular_movies LIMIT 5;
-- SELECT * FROM unread_messages_count;
