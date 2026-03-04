-- Test with just 10 users
DO $$ 
DECLARE
    i INT;
BEGIN
    -- Insert 10 test users
    FOR i IN 1..10 LOOP
        INSERT INTO users (username, email, password, bio, profile_picture_url, created_at)
        VALUES (
            'user' || i,
            'user' || i || '@test.com',
            'password',
            'Test user bio',
            'https://ui-avatars.com/api/?name=User' || i,
            NOW()
        );
    END LOOP;
    
    RAISE NOTICE 'Created 10 test users';
END $$;

SELECT COUNT(*) as total_users FROM users;
