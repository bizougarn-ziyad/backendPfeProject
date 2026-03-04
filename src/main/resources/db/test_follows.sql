-- Add test follow relationships
-- Make sure these user IDs exist in your users table first

-- User 1 follows User 2
INSERT INTO user_follows (follower_id, following_id) 
VALUES (1, 2) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- User 2 follows User 1 (mutual follow)
INSERT INTO user_follows (follower_id, following_id) 
VALUES (2, 1) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- User 1 follows User 3
INSERT INTO user_follows (follower_id, following_id) 
VALUES (1, 3) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- User 3 follows User 1
INSERT INTO user_follows (follower_id, following_id) 
VALUES (3, 1) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- User 4 follows User 1 (one-way follow)
INSERT INTO user_follows (follower_id, following_id) 
VALUES (4, 1) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- User 1 follows User 5 (one-way follow)
INSERT INTO user_follows (follower_id, following_id) 
VALUES (1, 5) 
ON CONFLICT (follower_id, following_id) DO NOTHING;

-- Check the results
SELECT 
    uf.follower_id,
    u1.username as follower_username,
    uf.following_id,
    u2.username as following_username
FROM user_follows uf
JOIN users u1 ON uf.follower_id = u1.id
JOIN users u2 ON uf.following_id = u2.id
WHERE uf.follower_id = 1 OR uf.following_id = 1
ORDER BY uf.follower_id, uf.following_id;
