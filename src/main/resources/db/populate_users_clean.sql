-- ============================================================================
-- Generate 1000 Test Users with Profile Pictures and Activity
-- ============================================================================

-- Clear existing test data (keep users with id < 14 as those are from seed.sql)
DELETE FROM user_follows WHERE follower_id > 13  OR following_id > 13;
DELETE FROM reviews WHERE user_id > 13;
DELETE FROM user_favorites WHERE user_id > 13;
DELETE FROM user_watched WHERE user_id > 13;
DELETE FROM users WHERE id > 13;

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
        'Nicholas', 'Shirley', 'Eric', 'Angela', 'Jonathan', 'Helen', 'Stephen', 'Anna'        
    ];
    
    last_names TEXT[] := ARRAY[
        'Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson',
        'Moore', 'Taylor', 'Anderson', 'Thomas', 'Jackson', 'White', 'Harris', 'Martin',
        'Thompson', 'Garcia', 'Martinez', 'Robinson', 'Clark', 'Rodriguez', 'Lewis', 'Lee',
        'Walker', 'Hall', 'Allen', 'Young', 'King', 'Wright', 'Lopez', 'Hill',
        'Scott', 'Green', 'Adams', 'Baker', 'Gonzalez', 'Nelson', 'Carter', 'Mitchell',
        'Perez', 'Roberts', 'Turner', 'Phillips', 'Campbell', 'Parker', 'Evans', 'Edwards',
        'Collins', 'Stewart', 'Sanchez', 'Morris', 'Rogers', 'Reed', 'Cook', 'Morgan',
        'Bell', 'Murphy', 'Bailey', 'Rivera', 'Cooper', 'Richardson', 'Cox', 'Howard'
    ];
    
    bg_colors TEXT[] := ARRAY[
        '007bff', 'ff6347', '20c997', 'ffc107', '6f42c1', 'fd7e14', 
        '28a745', 'dc3545', '17a2b8', 'e83e8c', '343a40', '6610f2'
    ];
    
    bios TEXT[] := ARRAY[
        'Movie enthusiast - Love sci-fi and thrillers',
        'Binge-watcher extraordinaire',
        'Critic by day, couch potato by night',
        'Always hunting for hidden gems',
        'Film buff - Horror fan - Netflix addict',
        'Classic cinema lover',
        'Anime and action movies are my jam',
        'Documentary enthusiast',
        'Rom-com defender till the end',
        'Marvel fanatic - DC curious',
        'Indie films all the way',
        'Award season is my favorite season',
        '90''s movies hit different',
        'Streaming all the things',
        'Film student - Future director',
        'Just here for the popcorn',
        'Quality over quantity... mostly',
        'Foreign films appreciation club',
        'Superhero movies and chill',
        'Cinema is my therapy'
    ];
    
    i INT;
    bio TEXT;
    profile_pic TEXT;
    first_name TEXT;
    last_name TEXT;
    random_num INT;
    
    user_id BIGINT;
    num_movies_watched INT;
    num_series_watched INT;
    num_reviews INT;
    num_favorites INT;
    content_ref_id UUID;
    
BEGIN
    -- Seed content references (movies and TV shows)
    INSERT INTO content_references (id, tmdb_id, content_type, created_at) VALUES
        (gen_random_uuid(), 550, 'MOVIE', NOW()),      -- Fight Club
        (gen_random_uuid(), 278, 'MOVIE', NOW()),      -- Shawshank Redemption
        (gen_random_uuid(), 238, 'MOVIE', NOW()),      -- The Godfather
        (gen_random_uuid(), 424, 'MOVIE', NOW()),      -- Dark Knight
        (gen_random_uuid(), 13, 'MOVIE', NOW()),       -- Forrest Gump
        (gen_random_uuid(), 680, 'MOVIE', NOW()),      -- Pulp Fiction
        (gen_random_uuid(), 27205, 'MOVIE', NOW()),    -- Inception
        (gen_random_uuid(), 155, 'MOVIE', NOW'),        -- Dark Knight Rises
        (gen_random_uuid(), 497, 'MOVIE', NOW()),      -- The Green Mile
        (gen_random_uuid(), 129, 'MOVIE', NOW()),      -- Spirited Away
        (gen_random_uuid(), 1399, 'TV', NOW()),        -- Game of Thrones
        (gen_random_uuid(), 1396, 'TV', NOW()),        -- Breaking Bad
        (gen_random_uuid(), 60625, 'TV', NOW()),       -- Rick and Morty
        (gen_random_uuid(), 1668, 'TV', NOW()),        -- Friends
        (gen_random_uuid(), 94605, 'TV', NOW()),       -- Arcane
        (gen_random_uuid(), 82856, 'TV', NOW()),       -- The Mandalorian
        (gen_random_uuid(), 456, 'TV', NOW()),          -- The Simpsons
        (gen_random_uuid(), 46952, 'TV', NOW()),       -- The Witcher
        (gen_random_uuid(), 85271, 'TV', NOW()),       -- WandaVision
        (gen_random_uuid(), 71446, 'TV', NOW())        -- Money Heist
    ON CONFLICT DO NOTHING;
    
    -- Generate 1000 users
    FOR i IN 1..1000 LOOP
        -- Random name
        first_name := first_names[1 + floor(random() * array_length(first_names, 1))::int];
        last_name := last_names[1 + floor(random() * array_length(last_names, 1))::int];
        
        -- Random bio
        bio := bios[1 + floor(random() * array_length(bios, 1))::int];
        
        -- Random profile picture using UI Avatars
        profile_pic := 'https://ui-avatars.com/api/?name=' || 
                      replace(first_name || '+' || last_name, ' ', '+') || 
                      '&background=' || bg_colors[1 + floor(random() * array_length(bg_colors, 1))::int] || 
                      '&color=fff&size=200';
        
        -- Insert user
        INSERT INTO users (username, email, password, bio, profile_picture_url, is_active, role, created_at, updated_at)
        VALUES (
            lower(first_name || '.' || last_name || floor(random() * 1000)::int),
            lower(first_name || '.' || last_name || floor(random() * 1000)::int || '@example.com'),
            'password',
            bio,
            profile_pic,
            true,
            'USER',
            NOW() - (random() * INTERVAL '365 days'),
            NOW() - (random() * INTERVAL '365 days')
        )
        RETURNING id INTO user_id;
        
        -- Randomly add watched movies (0-5 per user)
        num_movies_watched := floor(random() * 6)::int;
        IF num_movies_watched > 0 THEN
            FOR j IN 1..num_movies_watched LOOP
                BEGIN
                    INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
                    SELECT 
                        gen_random_uuid(),
                        user_id,
                        cr.id,
                        NOW() - (random() * INTERVAL '180 days')
                    FROM content_references cr
                    WHERE cr.content_type = 'MOVIE'
                    ORDER BY RANDOM()
                    LIMIT 1
                    ON CONFLICT DO NOTHING;
                EXCEPTION WHEN OTHERS THEN
                    CONTINUE;
                END;
            END LOOP;
        END IF;
        
        -- Randomly add watched TV shows (0-5 per user)
        num_series_watched := floor(random() * 6)::int;
        IF num_series_watched > 0 THEN
            FOR j IN 1..num_series_watched LOOP
                BEGIN
                    INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
                    SELECT 
                        gen_random_uuid(),
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
                    INSERT INTO user_favorites (id, user_id, content_reference_id, created_at)
                    SELECT 
                        gen_random_uuid(),
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
                        INSERT INTO reviews (id, user_id, content_reference_id, rating, review_text, created_at, updated_at, likes_count)
                        VALUES (
                            gen_random_uuid(),
                            user_id,
                            content_ref_id,
                            5 + floor(random() * 6)::int,
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
        
        -- Randomly add follows (0-10 per user)
        FOR j IN 1..floor(random() * 11)::int LOOP
            BEGIN
                INSERT INTO user_follows (id, follower_id, following_id, created_at)
                SELECT 
                    gen_random_uuid(),
                    user_id,
                    u2.id,
                    NOW() - (random() * INTERVAL '180 days')
                FROM users u2
                WHERE u2.id != user_id
                ORDER BY RANDOM()
                LIMIT 1
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN
                CONTINUE;
            END;
        END LOOP;
        
        IF i % 100 = 0 THEN
            RAISE NOTICE 'Generated % users...', i;
        END IF;
    END LOOP;
    
    RAISE NOTICE 'Successfully generated 1000 users!';
END $$;

-- Verification query
SELECT 
    'Total Users' as metric,
    COUNT(*)::text as value
FROM users
UNION ALL
SELECT 
    'Users with Profiles',
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
