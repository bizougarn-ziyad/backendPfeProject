-- ============================================================================
-- Community Forum Tables Migration
-- ============================================================================

-- Community discussion topics
CREATE TABLE IF NOT EXISTS community_topics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('ANNOUNCEMENTS', 'MOVIES', 'TV_SHOWS', 'RECOMMENDATIONS', 'SUGGESTIONS', 'SUPPORT')),
    author_id BIGINT NOT NULL,
    is_pinned BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    upvote_count INTEGER DEFAULT 0,
    reply_count INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_activity_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Community topic replies
CREATE TABLE IF NOT EXISTS community_replies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_id UUID NOT NULL,
    author_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Community upvotes
CREATE TABLE IF NOT EXISTS community_upvotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_id UUID NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_community_upvotes_topic_user UNIQUE(topic_id, user_id)
);

-- Add foreign keys if tables exist
DO $$ 
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                      WHERE constraint_name = 'fk_community_topics_author' 
                      AND table_name = 'community_topics') THEN
            ALTER TABLE community_topics 
            ADD CONSTRAINT fk_community_topics_author 
            FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                      WHERE constraint_name = 'fk_community_replies_author' 
                      AND table_name = 'community_replies') THEN
            ALTER TABLE community_replies 
            ADD CONSTRAINT fk_community_replies_author 
            FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;

        IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                      WHERE constraint_name = 'fk_community_upvotes_user' 
                      AND table_name = 'community_upvotes') THEN
            ALTER TABLE community_upvotes 
            ADD CONSTRAINT fk_community_upvotes_user 
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
        END IF;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                  WHERE constraint_name = 'fk_community_replies_topic' 
                  AND table_name = 'community_replies') THEN
        ALTER TABLE community_replies 
        ADD CONSTRAINT fk_community_replies_topic 
        FOREIGN KEY (topic_id) REFERENCES community_topics(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints 
                  WHERE constraint_name = 'fk_community_upvotes_topic' 
                  AND table_name = 'community_upvotes') THEN
        ALTER TABLE community_upvotes 
        ADD CONSTRAINT fk_community_upvotes_topic 
        FOREIGN KEY (topic_id) REFERENCES community_topics(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Indexes for community tables
CREATE INDEX IF NOT EXISTS idx_community_topics_category ON community_topics(category);
CREATE INDEX IF NOT EXISTS idx_community_topics_author_id ON community_topics(author_id);
CREATE INDEX IF NOT EXISTS idx_community_topics_created_at ON community_topics(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_topics_is_pinned ON community_topics(is_pinned);
CREATE INDEX IF NOT EXISTS idx_community_topics_last_activity ON community_topics(last_activity_at DESC);
CREATE INDEX IF NOT EXISTS idx_community_replies_topic_id ON community_replies(topic_id);
CREATE INDEX IF NOT EXISTS idx_community_replies_author_id ON community_replies(author_id);
CREATE INDEX IF NOT EXISTS idx_community_replies_created_at ON community_replies(created_at ASC);
CREATE INDEX IF NOT EXISTS idx_community_upvotes_topic_id ON community_upvotes(topic_id);
CREATE INDEX IF NOT EXISTS idx_community_upvotes_user_id ON community_upvotes(user_id);
