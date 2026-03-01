-- Generate 1000 test users with profiles and activity
DELETE FROM user_follows WHERE follower_id > 13 OR following_id > 13;
DELETE FROM reviews WHERE user_id > 13;
DELETE FROM user_favorites WHERE user_id > 13;
DELETE FROM user_watched WHERE user_id > 13;
DELETE FROM users WHERE id > 13;

-- Seed content for testing
INSERT INTO content_references (id, tmdb_id, content_type, created_at) VALUES
    (gen_random_uuid(), 550, 'MOVIE', NOW()),
    (gen_random_uuid(), 278, 'MOVIE', NOW()),
    (gen_random_uuid(), 238, 'MOVIE', NOW()),
    (gen_random_uuid(), 424, 'MOVIE', NOW()),
    (gen_random_uuid(), 13, 'MOVIE', NOW()),
    (gen_random_uuid(), 680, 'MOVIE', NOW()),
    (gen_random_uuid(), 27205, 'MOVIE', NOW()),
    (gen_random_uuid(), 155, 'MOVIE', NOW()),
    (gen_random_uuid(), 497, 'MOVIE', NOW()),
    (gen_random_uuid(), 129, 'MOVIE', NOW()),
    (gen_random_uuid(), 1399, 'TV', NOW()),
    (gen_random_uuid(), 1396, 'TV', NOW()),
    (gen_random_uuid(), 60625, 'TV', NOW()),
    (gen_random_uuid(), 1668, 'TV', NOW()),
    (gen_random_uuid(), 94605, 'TV', NOW()),
    (gen_random_uuid(), 82856, 'TV', NOW()),
    (gen_random_uuid(), 456, 'TV', NOW()),
    (gen_random_uuid(), 46952, 'TV', NOW()),
    (gen_random_uuid(), 85271, 'TV', NOW()),
    (gen_random_uuid(), 71446, 'TV', NOW())
ON CONFLICT DO NOTHING;

-- Generate users
DO $$
DECLARE
    names TEXT[] := ARRAY['James Smith', 'Mary Johnson', 'John Williams', 'Patricia Jones', 'Robert Brown'];
    bios TEXT[] := ARRAY['Movie lover', 'Film buff', 'Cinema enthusiast', 'Binge watcher', 'Movie critic'];
    colors TEXT[] := ARRAY['007bff', 'ff6347', '20c997', 'ffc107', '6f42c1'];
    i INT;
    user_id BIGINT;
BEGIN
    FOR i IN 1..1000 LOOP
        INSERT INTO users (username, email, password, bio, profile_picture_url, is_active, role, created_at, updated_at)
        VALUES (
            'user' || i,
            'user' || i || '@example.com',
            'password',
            bios[1 + floor(random() * 5)::int],
            'https://ui-avatars.com/api/?name=User' || i || '&background=' || colors[1 + floor(random() * 5)::int],
            true,
            'USER',
            NOW(),
            NOW()
        )
        RETURNING id INTO user_id;
        
        IF i % 100 = 0 THEN
            RAISE NOTICE 'Generated % users', i;
        END IF;
    END LOOP;
    RAISE NOTICE 'Done!';
END $$;

SELECT COUNT(*) as total_users FROM users;
