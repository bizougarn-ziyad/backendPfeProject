-- Add activity to the 1000 generated users
DO $$
DECLARE
    cur_user RECORD;
    content_ref_id UUID;
    num_movies INT;
    num_tv INT;
    num_favs INT;
    num_revs INT;
    num_follows INT;
    i INT;
    review_texts TEXT[] := ARRAY[
        'Amazing! Loved it!',
        'Great film with excellent performances',
        'Entertaining but could be better',
        'Solid movie worth watching',
        'Interesting story and good execution'
    ];
BEGIN
    FOR cur_user IN SELECT id FROM users WHERE id > 13 LOOP
        -- Random watched movies (0-5)
        num_movies := floor(random() * 6)::int;
        FOR i IN 1..num_movies LOOP
            BEGIN
                INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
                SELECT gen_random_uuid(), cur_user.id, cr.id, NOW() - (random() * INTERVAL '180 days')
                FROM content_references cr
                WHERE cr.content_type = 'MOVIE'
                ORDER BY RANDOM()
                LIMIT 1
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN NULL;
            END;
        END LOOP;
        
        -- Random watched TV (0-5)
        num_tv := floor(random() * 6)::int;
        FOR i IN 1..num_tv LOOP
            BEGIN
                INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
                SELECT gen_random_uuid(), cur_user.id, cr.id, NOW() - (random() * INTERVAL '180 days')
                FROM content_references cr
                WHERE cr.content_type = 'TV'
                ORDER BY RANDOM()
                LIMIT 1
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN NULL;
            END;
        END LOOP;
        
        -- Random favorites (0-3)
        num_favs := floor(random() * 4)::int;
        FOR i IN 1..num_favs LOOP
            BEGIN
                INSERT INTO user_favorites (id, user_id, content_reference_id, created_at)
                SELECT gen_random_uuid(), cur_user.id, cr.id, NOW() - (random() * INTERVAL '365 days')
                FROM content_references cr
                ORDER BY RANDOM()
                LIMIT 1
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN NULL;
            END;
        END LOOP;
        
        -- Random reviews (0-2)
        num_revs := floor(random() * 3)::int;
        FOR i IN 1..num_revs LOOP
            BEGIN
                SELECT id INTO content_ref_id
                FROM content_references
                ORDER BY RANDOM()
                LIMIT 1;
                
                INSERT INTO reviews (id, user_id, content_reference_id, rating, review_text, created_at, updated_at, likes_count)
                VALUES (
                    gen_random_uuid(),
                    cur_user.id,
                    content_ref_id,
                    5 + floor(random() * 6)::int,
                    review_texts[1 + floor(random() * 5)::int],
                    NOW() - (random() * INTERVAL '180 days'),
                    NOW() - (random() * INTERVAL '180 days'),
                    0
                )
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN NULL;
            END;
        END LOOP;
        
        -- Random follows (0-10)
        num_follows := floor(random() * 11)::int;
        FOR i IN 1..num_follows LOOP
            BEGIN
                INSERT INTO user_follows (id, follower_id, following_id, created_at)
                SELECT gen_random_uuid(), cur_user.id, u2.id, NOW() - (random() * INTERVAL '180 days')
                FROM users u2
                WHERE u2.id != cur_user.id
                ORDER BY RANDOM()
                LIMIT 1
                ON CONFLICT DO NOTHING;
            EXCEPTION WHEN OTHERS THEN NULL;
            END;
        END LOOP;
    END LOOP;
    
    RAISE NOTICE 'Added activity to all users!';
END $$;

-- Summary
SELECT 
    'Total Users' as metric,
    COUNT(*)::text as value
FROM users
UNION ALL
SELECT 
    'Users with Profiles',
    COUNT(*)::text
FROM users 
WHERE profile_picture_url IS NOT NULL AND id > 13
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
