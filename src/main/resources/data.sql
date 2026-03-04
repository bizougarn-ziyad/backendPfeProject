-- Fix user #6 on application startup
UPDATE users SET username = 'ziyadbz666', bio = 'Full-stack developer and movie enthusiast!', email = 'ziyad@example.com', updated_at = CURRENT_TIMESTAMP WHERE id = 6;
