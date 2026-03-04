-- ============================================================================
-- Generate 1000 Test Users with Profile Pictures and Activity
-- ============================================================================
-- This script generates realistic test data for user profiles
-- Run this after seed.sql to add bulk test users
-- ============================================================================

-- Array of first names for random generation
DO $$
DECLARE
    first_names TEXT[] := ARRAY[
        'James', 'Mary', 'John', 'Patricia', 'Robert', 'Jennifer', 'Michael', 'Linda',
        'William', 'Barbara', 'David', 'Elizabeth', 'Richard', 'Susan', 'Joseph', 'Jessica',
        'Thomas', 'Sarah', 'Charles', 'Karen', 'Christopher', 'Nancy', 'Daniel', 'Lisa',
        'Matthew', 'Betty', 'Anthony', 'Margaret', 'Mark', 'Sandra', 'Donald', 'Ashley',
        'Steven', 'Kimberly', 'Paul', 'Emily', 'Andrew', 'Donna', 'Joshua', 'Michelle',
        'Kenneth', 'Dorothy', 'Kevin', 'Carol', 'Brian', 'Amanda', 'George', 'Melissa',
        'Edward', 'Deborah', 'Ronald', 'Stephanie', 'Timothy', 'Rebecca', 'Jason', 'Sharon',
        'Jeffrey', 'Laura', 'Ryan', 'Cynthia', 'Jacob', 'Kathleen', 'Gary', 'Amy',
        'Nicholas', 'Shirley', 'Eric', 'Angela', 'Jonathan', 'Helen', 'Stephen', 'Anna',
        'Larry', 'Brenda', 'Justin', 'Pamela', 'Scott', 'Nicole', 'Brandon', 'Emma',
        'Benjamin', 'Samantha', 'Samuel', 'Katherine', 'Raymond', 'Christine', 'Gregory', 'Debra',
        'Alexander', 'Rachel', 'Patrick', 'Catherine', 'Frank', 'Carolyn', 'Jack', 'Janet',
        'Dennis', 'Ruth', 'Jerry', 'Maria', 'Tyler', 'Heather', 'Aaron', 'Diane',
        'Jose', 'Virginia', 'Adam', 'Julie', 'Henry', 'Joyce', 'Nathan', 'Victoria'
    ];
    
    last_names TEXT[] := ARRAY[
        'Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis',
        'Rodriguez', 'Martinez', 'Hernandez', 'Lopez', 'Gonzalez', 'Wilson', 'Anderson', 'Thomas',
        'Taylor', 'Moore', 'Jackson', 'Martin', 'Lee', 'Perez', 'Thompson', 'White',
        'Harris', 'Sanchez', 'Clark', 'Ramirez', 'Lewis', 'Robinson', 'Walker', 'Young',
        'Allen', 'King', 'Wright', 'Scott', 'Torres', 'Nguyen', 'Hill', 'Flores',
        'Green', 'Adams', 'Nelson', 'Baker', 'Hall', 'Rivera', 'Campbell', 'Mitchell',
        'Carter', 'Roberts', 'Gomez', 'Phillips', 'Evans', 'Turner', 'Diaz', 'Parker',
        'Cruz', 'Edwards', 'Collins', 'Reyes', 'Stewart', 'Morris', 'Morales', 'Murphy',
        'Cook', 'Rogers', 'Gutierrez', 'Ortiz', 'Morgan', 'Cooper', 'Peterson', 'Bailey',
        'Reed', 'Kelly', 'Howard', 'Ramos', 'Kim', 'Cox', 'Ward', 'Richardson',
        'Watson', 'Brooks', 'Chavez', 'Wood', 'James', 'Bennett', 'Gray', 'Mendoza',
        'Ruiz', 'Hughes', 'Price', 'Alvarez', 'Castillo', 'Sanders', 'Patel', 'Myers',
        'Long', 'Ross', 'Foster', 'Jimenez', 'Powell', 'Jenkins', 'Perry', 'Russell'
    ];
    
    bio_templates TEXT[] := ARRAY[
        'Movie enthusiast and avid reviewer',
        'Passionate about cinema and storytelling',
        'Binge-watcher extraordinaire',
        'Film critic and pop culture junkie',
        'Love discovering hidden gems',
        'Sci-fi and fantasy fan',
        'Horror movie aficionado',
        'Rom-com lover at heart',
        'Documentary enthusiast',
        'Action and thriller junkie',
        'Classic cinema appreciator',
        'Anime and international film fan',
        'Marvel and DC superfan',
        'Netflix and chill expert',
        'Weekend movie marathon planner',
        'Popcorn connoisseur and film buff',
        'Always searching for the next great series',
        'Movie quotes are my second language',
        'Living for plot twists and cliffhangers',
        'Collecting movies like infinity stones'
    ];
    
    i INT;
    username TEXT;
    email TEXT;
    bio TEXT;
    profile_pic TEXT;
    first_name TEXT;
    last_name TEXT;
    random_num INT;
    
    -- Variables for content IDs
    user_id BIGINT;
    num_movies_watched INT;
    num_series_watched INT;
    num_reviews INT;
    num_favorites INT;
    content_ref_id UUID;
    
BEGIN
    -- Seed some content references if they don't exist
    INSERT INTO content_references (id, tmdb_id, content_type, created_at) VALUES
        (gen_random_uuid(), 550, 'MOVIE', NOW()),      -- Fight Club
        (gen_random_uuid(), 278, 'MOVIE', NOW()),      -- Shawshank Redemption
        (gen_random_uuid(), 238, 'MOVIE', NOW()),      -- The Godfather
        (gen_random_uuid(), 424, 'MOVIE', NOW()),      -- The Dark Knight
        (gen_random_uuid(), 680, 'MOVIE', NOW()),      -- Pulp Fiction
        (gen_random_uuid(), 13, 'MOVIE', NOW()),       -- Forrest Gump
        (gen_random_uuid(), 155, 'MOVIE', NOW()),      -- The Dark Knight Rises
        (gen_random_uuid(), 497, 'MOVIE', NOW()),      -- The Green Mile
        (gen_random_uuid(), 389, 'MOVIE', NOW()),      -- 12 Angry Men
        (gen_random_uuid(), 769, 'MOVIE', NOW()),      -- GoodFellas
        (gen_random_uuid(), 1396, 'TV', NOW()),   -- Breaking Bad
        (gen_random_uuid(), 1399, 'TV', NOW()),   -- Game of Thrones
        (gen_random_uuid(), 60735, 'TV', NOW()),  -- The Flash
        (gen_random_uuid(), 1402, 'TV', NOW()),   -- The Walking Dead
        (gen_random_uuid(), 1412, 'TV', NOW()),   -- Arrow
        (gen_random_uuid(), 46952, 'TV', NOW()),  -- The Witcher
        (gen_random_uuid(), 94605, 'TV', NOW()),  -- Arcane
        (gen_random_uuid(), 85271, 'TV', NOW()),  -- WandaVision
        (gen_random_uuid(), 88396, 'TV', NOW()),  -- The Falcon and the Winter Soldier
        (gen_random_uuid(), 95057, 'TV', NOW())   -- Loki
    ON CONFLICT (tmdb_id, content_type) DO NOTHING;
    
    -- Generate 1000 users
    FOR i IN 1..1000 LOOP
        -- Generate random name
        first_name := first_names[1 + floor(random() * array_length(first_names, 1))::int];
        last_name := last_names[1 + floor(random() * array_length(last_names, 1))::int];
        random_num := floor(random() * 9999)::int;
        
        -- Create username (lowercase firstname + number)
        username := lower(first_name) || random_num;
        
        -- Create email
        email := username || '@example.com';
        
        -- Random bio
        bio := bio_templates[1 + floor(random() * array_length(bio_templates, 1))::int];
        
        -- Generate profile picture URL using UI Avatars service
        -- This creates colorful avatar images with initials
        profile_pic := 'https://ui-avatars.com/api/?name=' || 
                      replace(first_name || '+' || last_name, ' ', '+') || 
                      '&size=200&background=' || 
                      lpad(to_hex(floor(random() * 16777215)::int), 6, '0') ||
                      '&color=fff&bold=true';
        
        -- Insert user
        INSERT INTO users (username, email, password, bio, profile_picture_url, role, is_active, created_at, updated_at)
        VALUES (
            username,
            email,
            '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKK3DKnJYe', -- password123
            bio,
            profile_pic,
            'USER',
            TRUE,
            NOW() - (random() * INTERVAL '365 days'), -- Random creation time in last year
            NOW() - (random() * INTERVAL '30 days')   -- Random update time in last month
        )
        RETURNING id INTO user_id;
        
        -- Randomly add watched movies (0-5 movies per user)
        num_movies_watched := floor(random() * 6)::int;
        IF num_movies_watched > 0 THEN
            FOR j IN 1..num_movies_watched LOOP
                BEGIN
                    INSERT INTO user_watched (user_id, content_reference_id, watched_at)
                    SELECT 
                        user_id,
                        cr.id,
                        NOW() - (random() * INTERVAL '180 days')
                    FROM content_references cr
                    WHERE cr.content_type = 'MOVIE'
                    ORDER BY RANDOM()
                    LIMIT 1
                    ON CONFLICT DO NOTHING;
                EXCEPTION WHEN OTHERS THEN
                    -- Skip if error
                    CONTINUE;
                END;
            END LOOP;
        END IF;
        
        -- Randomly add watched TV shows (0-5 series per user)
        num_series_watched := floor(random() * 6)::int;
        IF num_series_watched > 0 THEN
            FOR j IN 1..num_series_watched LOOP
                BEGIN
                    INSERT INTO user_watched (user_id, content_reference_id, watched_at)
                    SELECT 
                        user_id,
                        cr.id,
                        NOW() - (random() * INTERVAL '180 days')
                    FROM content_references cr
                    WHERE cr.content_type = 'TV'
                    ORDER BY RANDOM()
                    LIMIT 1
                    ON CONFLICT DO NOTHING;
                EXCEPTION WHEN OTHERS THEN
                    CONTINUE;
                END;
            END LOOP;
        END IF;
        
        -- Randomly add favorites (0-3 per user)
        num_favorites := floor(random() * 4)::int;
        IF num_favorites > 0 THEN
            FOR j IN 1..num_favorites LOOP
                BEGIN
                    INSERT INTO user_favorites (user_id, content_reference_id, created_at)
                    SELECT 
                        user_id,
                        cr.id,
                        NOW() - (random() * INTERVAL '180 days')
                    FROM content_references cr
                    ORDER BY RANDOM()
                    LIMIT 1
                    ON CONFLICT DO NOTHING;
                EXCEPTION WHEN OTHERS THEN
                    CONTINUE;
                END;
            END LOOP;
        END IF;
        
        -- Randomly add reviews (0-2 per user)
        num_reviews := floor(random() * 3)::int;
        IF num_reviews > 0 THEN
            FOR j IN 1..num_reviews LOOP
                BEGIN
                    SELECT id INTO content_ref_id
                    FROM content_references
                    WHERE content_type = CASE WHEN random() > 0.5 THEN 'MOVIE' ELSE 'TV' END
                    ORDER BY RANDOM()
                    LIMIT 1;
                    
                    IF content_ref_id IS NOT NULL THEN
                        INSERT INTO reviews (user_id, content_reference_id, rating, review_text, created_at, updated_at, likes_count)
                        VALUES (
                            user_id,
                            content_ref_id,
                            5 + floor(random() * 6)::int, -- Rating 5-10
                            CASE floor(random() * 5)::int
                                WHEN 0 THEN 'Absolutely amazing! Loved every minute of it.'
                                WHEN 1 THEN 'Great film with excellent performances.'
                                WHEN 2 THEN 'Entertaining but could be better.'
                                WHEN 3 THEN 'Solid movie, worth watching.'
                                ELSE 'Interesting story and good execution.'
                            END,
                            NOW() - (random() * INTERVAL '180 days'),
                            NOW() - (random() * INTERVAL '180 days'),
                            0
                        )
                        ON CONFLICT DO NOTHING;
                    END IF;
                EXCEPTION WHEN OTHERS THEN
                    CONTINUE;
                END;
            END LOOP;
        END IF;
        
        -- Add random follows (0-10 follows per user)
        FOR j IN 1..floor(random() * 11)::int LOOP
            BEGIN
                INSERT INTO user_follows (follower_id, following_id)
                VALUES (
                    user_id,
                    -- Follow a random user (avoid self-follow)
                    CASE 
                        WHEN user_id = 1 THEN 2
                        ELSE floor(1 + random() * (user_id - 1))::bigint
                    END
                )
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN
                CONTINUE;
            END;
        END LOOP;
        
        -- Progress indicator (every 100 users)
        IF i % 100 = 0 THEN
            RAISE NOTICE 'Generated % users...', i;
        END IF;
    END LOOP;
    
    RAISE NOTICE 'Successfully generated 1000 users with profiles and activity!';
END $$;

-- ============================================================================
-- Verification
-- ============================================================================
SELECT 
    'Total Users' as metric,
    COUNT(*)::text as value
FROM users
UNION ALL
SELECT 
    'Users with Profile Pictures',
    COUNT(*)::text
FROM users 
WHERE profile_picture_url IS NOT NULL
UNION ALL
SELECT 
    'Total Watched Items',
    COUNT(*)::text
FROM user_watched
UNION ALL
SELECT 
    'Total Favorites',
    COUNT(*)::text
FROM user_favorites
UNION ALL
SELECT 
    'Total Reviews',
    COUNT(*)::text
FROM reviews
UNION ALL
SELECT 
    'Total Follows',
    COUNT(*)::text
FROM user_follows;

-- Sample query to see a few users with their stats
SELECT 
    u.id,
    u.username,
    u.bio,
    LEFT(u.profile_picture_url, 50) || '...' as profile_pic_preview,
    (SELECT COUNT(*) FROM user_watched uw WHERE uw.user_id = u.id) as watched_count,
    (SELECT COUNT(*) FROM user_favorites uf WHERE uf.user_id = u.id) as favorites_count,
    (SELECT COUNT(*) FROM reviews r WHERE r.user_id = u.id) as reviews_count,
    (SELECT COUNT(*) FROM user_follows uf WHERE uf.follower_id = u.id) as following_count,
    (SELECT COUNT(*) FROM user_follows uf WHERE uf.following_id = u.id) as followers_count
FROM users u
WHERE u.id > 13  -- Skip the initial seed users
ORDER BY u.id
LIMIT 20;
