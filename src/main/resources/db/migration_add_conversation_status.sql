-- Add status column to conversations table
-- This migration adds conversation status support for message requests

-- Add status column with default value
ALTER TABLE conversations 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACCEPTED';

-- Update existing conversations to have ACCEPTED status
UPDATE conversations 
SET status = 'ACCEPTED' 
WHERE status IS NULL OR status = '';

-- Add check constraint for valid status values
ALTER TABLE conversations
ADD CONSTRAINT chk_conversation_status 
CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED'));
