-- Fix user #6 with correct data
UPDATE users 
SET 
    username = 'ziyadbz666',
    bio = 'Full-stack developer and movie enthusiast!',
    email = 'ziyad@example.com',
    updated_at = NOW()
WHERE id = 6;

-- Verify the update
SELECT id, username, email, bio FROM users WHERE id = 6;
