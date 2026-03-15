-- =============================================================================
-- FULL RESET & RESEED — 100 users, rich data for all dashboard stats
-- Targets the Hibernate-managed schema (BIGSERIAL user IDs, UUID content_references)
-- Password for all users: "pass123" (bcrypt hash below)
-- Admin: admin@test.com / pass123
-- =============================================================================

-- ── 1. WIPE EVERYTHING ──────────────────────────────────────────────────────
TRUNCATE TABLE
  community_upvotes,
  community_replies,
  community_topics,
  reviews,
  user_ratings,
  user_favorites,
  user_watched,
  list_items,
  user_lists,
  content_references,
  users
RESTART IDENTITY CASCADE;

-- ── 1.5. ENSURE SCHEMA IS CORRECT ───────────────────────────────────────────
ALTER TABLE users ADD COLUMN IF NOT EXISTS country VARCHAR(100);

-- ── 2. INSERT 100 USERS ─────────────────────────────────────────────────────
-- BCrypt of "pass123": $2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu
INSERT INTO users (username, email, password, bio, role, is_active, is_suspended, created_at, updated_at) VALUES
('admin',       'admin@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Platform administrator', 'ADMIN', TRUE, FALSE, NOW() - INTERVAL '90 days', NOW()),
('alex_m',      'alex@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Huge film nerd from Paris', 'USER', TRUE, FALSE, NOW() - INTERVAL '89 days', NOW()),
('sarah_c',     'sarah@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Binge-watcher from London', 'USER', TRUE, FALSE, NOW() - INTERVAL '88 days', NOW()),
('omar_k',      'omar@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Movie lover from Algiers', 'USER', TRUE, FALSE, NOW() - INTERVAL '87 days', NOW()),
('nina_b',      'nina@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Sci-fi fanatic from Berlin', 'USER', TRUE, FALSE, NOW() - INTERVAL '86 days', NOW()),
('lucas_r',     'lucas@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Horror & thriller fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '85 days', NOW()),
('yuki_t',      'yuki@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Anime and J-cinema lover from Tokyo', 'USER', TRUE, FALSE, NOW() - INTERVAL '84 days', NOW()),
('emma_w',      'emma@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Drama queen in the best way', 'USER', TRUE, FALSE, NOW() - INTERVAL '83 days', NOW()),
('theo_p',      'theo@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Classic cinema enthusiast', 'USER', TRUE, FALSE, NOW() - INTERVAL '82 days', NOW()),
('lena_v',      'lena@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Documentary and indie films', 'USER', TRUE, FALSE, NOW() - INTERVAL '81 days', NOW()),
('carlos_g',    'carlos@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Action movies all day from Madrid', 'USER', TRUE, FALSE, NOW() - INTERVAL '80 days', NOW()),
('amina_d',     'amina@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Romance and period dramas', 'USER', TRUE, FALSE, NOW() - INTERVAL '79 days', NOW()),
('james_k',     'james@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'MCU fanboy from New York', 'USER', TRUE, FALSE, NOW() - INTERVAL '78 days', NOW()),
('sofia_n',     'sofia@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Watching everything on Netflix', 'USER', TRUE, FALSE, NOW() - INTERVAL '77 days', NOW()),
('rayan_h',     'rayan@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Casual viewer from Casablanca', 'USER', TRUE, FALSE, NOW() - INTERVAL '76 days', NOW()),
('priya_s',     'priya@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Bollywood + Hollywood mix from Mumbai', 'USER', TRUE, FALSE, NOW() - INTERVAL '75 days', NOW()),
('marco_f',     'marco@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Italian cinema & Hollywood blockbusters', 'USER', TRUE, FALSE, NOW() - INTERVAL '74 days', NOW()),
('aisha_l',     'aisha@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Series addict from Lagos', 'USER', TRUE, FALSE, NOW() - INTERVAL '73 days', NOW()),
('felix_b',     'felix@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Film critic wannabe from Vienna', 'USER', TRUE, FALSE, NOW() - INTERVAL '72 days', NOW()),
('hana_m',      'hana@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'K-drama obsessed from Seoul', 'USER', TRUE, FALSE, NOW() - INTERVAL '71 days', NOW()),
('diego_r',     'diego@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Cinema freak from Buenos Aires', 'USER', TRUE, FALSE, NOW() - INTERVAL '70 days', NOW()),
('kiri_o',      'kiri@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Action & animation fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '69 days', NOW()),
('tanya_b',     'tanya@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Thriller and mystery lover', 'USER', TRUE, FALSE, NOW() - INTERVAL '68 days', NOW()),
('ibrahim_a',   'ibrahim@test.com',    '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Movies and tea from Cairo', 'USER', TRUE, FALSE, NOW() - INTERVAL '67 days', NOW()),
('chloe_d',     'chloe@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'French cinema and Netflix obsession', 'USER', TRUE, FALSE, NOW() - INTERVAL '66 days', NOW()),
('adam_t',      'adam@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Casual watcher from Toronto', 'USER', TRUE, FALSE, NOW() - INTERVAL '65 days', NOW()),
('fatima_z',    'fatima@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Historical dramas and documentaries', 'USER', TRUE, FALSE, NOW() - INTERVAL '64 days', NOW()),
('ben_s',       'ben@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Sci-fi and tech movies', 'USER', TRUE, FALSE, NOW() - INTERVAL '63 days', NOW()),
('mei_l',       'mei@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Asian cinema aficionado from Hong Kong', 'USER', TRUE, FALSE, NOW() - INTERVAL '62 days', NOW()),
('ethan_c',     'ethan@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Comic book movies and series', 'USER', TRUE, FALSE, NOW() - INTERVAL '61 days', NOW()),
('layla_h',     'layla@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Emotional dramas and tearjerkers', 'USER', TRUE, FALSE, NOW() - INTERVAL '60 days', NOW()),
('ryan_p',      'ryanp@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Blockbusters and popcorn films', 'USER', TRUE, FALSE, NOW() - INTERVAL '59 days', NOW()),
('nadia_k',     'nadia@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Psychological thrillers from Moscow', 'USER', TRUE, FALSE, NOW() - INTERVAL '58 days', NOW()),
('joe_m',       'joe@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Sports movies and biopics', 'USER', TRUE, FALSE, NOW() - INTERVAL '57 days', NOW()),
('sara_e',      'sarae@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Reality TV and drama series', 'USER', TRUE, FALSE, NOW() - INTERVAL '56 days', NOW()),
('kevin_l',     'kevin@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Animation movies and Pixar fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '55 days', NOW()),
('ines_v',      'ines@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Foreign films and subtitles', 'USER', TRUE, FALSE, NOW() - INTERVAL '54 days', NOW()),
('dan_o',       'dan@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Crime dramas and heist films', 'USER', TRUE, FALSE, NOW() - INTERVAL '53 days', NOW()),
('yara_s',      'yara@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Fantasy epics and adventure', 'USER', TRUE, FALSE, NOW() - INTERVAL '52 days', NOW()),
('tom_h',       'tom@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'War movies and history buff', 'USER', TRUE, FALSE, NOW() - INTERVAL '51 days', NOW()),
('nour_a',      'nour@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Arthouse and experimental cinema', 'USER', TRUE, FALSE, NOW() - INTERVAL '50 days', NOW()),
('leo_f',       'leo@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Music films and musicals', 'USER', TRUE, FALSE, NOW() - INTERVAL '49 days', NOW()),
('clara_x',     'clara@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Mystery and detective shows', 'USER', TRUE, FALSE, NOW() - INTERVAL '48 days', NOW()),
('mike_d',      'mike@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Comedy and sitcoms fanatic', 'USER', TRUE, FALSE, NOW() - INTERVAL '47 days', NOW()),
('zoe_r',       'zoe@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Emotional drama and tearjerker queen', 'USER', TRUE, FALSE, NOW() - INTERVAL '46 days', NOW()),
('aarav_p',     'aarav@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Bollywood devotee from Delhi', 'USER', TRUE, FALSE, NOW() - INTERVAL '45 days', NOW()),
('grace_l',     'grace@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Vintage Hollywood lover', 'USER', TRUE, FALSE, NOW() - INTERVAL '44 days', NOW()),
('nathan_b',    'nathan@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Cyberpunk and dystopian films', 'USER', TRUE, FALSE, NOW() - INTERVAL '43 days', NOW()),
('lara_g',      'lara@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'True crime and dark documentaries', 'USER', TRUE, FALSE, NOW() - INTERVAL '42 days', NOW()),
('mo_a',        'mo@test.com',         '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Mixed genre watcher from Dubai', 'USER', TRUE, FALSE, NOW() - INTERVAL '41 days', NOW()),
('anna_c',      'anna@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Scandinavian noir and crime', 'USER', TRUE, FALSE, NOW() - INTERVAL '40 days', NOW()),
('peter_v',     'peter@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Space and exploration movies', 'USER', TRUE, FALSE, NOW() - INTERVAL '39 days', NOW()),
('mia_f',       'mia@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'RomCom and feel-good films', 'USER', TRUE, FALSE, NOW() - INTERVAL '38 days', NOW()),
('jake_s',      'jake@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Post-apocalyptic fan from Sydney', 'USER', TRUE, FALSE, NOW() - INTERVAL '37 days', NOW()),
('elena_b',     'elena@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'European cinema enthusiast', 'USER', TRUE, FALSE, NOW() - INTERVAL '36 days', NOW()),
('hassan_k',    'hassan@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Action-comedy blend watcher', 'USER', TRUE, FALSE, NOW() - INTERVAL '35 days', NOW()),
('lily_c',      'lily@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Fantasy and fairy-tale fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '34 days', NOW()),
('sam_r',       'sam@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Dark comedies and satire', 'USER', TRUE, FALSE, NOW() - INTERVAL '33 days', NOW()),
('aya_m',       'aya@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Art films and photography', 'USER', TRUE, FALSE, NOW() - INTERVAL '32 days', NOW()),
('victor_h',    'victor@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Epic dramas and biography films', 'USER', TRUE, FALSE, NOW() - INTERVAL '31 days', NOW()),
('julia_m',     'julia@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Historical movies and series', 'USER', TRUE, FALSE, NOW() - INTERVAL '30 days', NOW()),
('nico_b',      'nico@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Short films and festival movies', 'USER', TRUE, FALSE, NOW() - INTERVAL '29 days', NOW()),
('rahul_s',     'rahul@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Indian cinema across genres', 'USER', TRUE, FALSE, NOW() - INTERVAL '28 days', NOW()),
('isabelle_r',  'isabelle@test.com',   '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Art-house French cinema lover', 'USER', TRUE, FALSE, NOW() - INTERVAL '27 days', NOW()),
('ali_m',       'ali@test.com',        '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Mixed genres from Tunis', 'USER', TRUE, FALSE, NOW() - INTERVAL '26 days', NOW()),
('sophie_g',    'sophie@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Costume dramas and period pieces', 'USER', TRUE, FALSE, NOW() - INTERVAL '25 days', NOW()),
('matt_j',      'matt@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Heist and spy thrillers', 'USER', TRUE, FALSE, NOW() - INTERVAL '24 days', NOW()),
('hira_a',      'hira@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Pakistani drama and world cinema', 'USER', TRUE, FALSE, NOW() - INTERVAL '23 days', NOW()),
('george_p',    'george@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Greek tragedy and modern drama', 'USER', TRUE, FALSE, NOW() - INTERVAL '22 days', NOW()),
('camille_d',   'camille@test.com',    '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Cozy mystery and detective shows', 'USER', TRUE, FALSE, NOW() - INTERVAL '21 days', NOW()),
('karim_b',     'karim@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Football movies and sports dramas', 'USER', TRUE, FALSE, NOW() - INTERVAL '20 days', NOW()),
('tina_l',      'tina@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Sitcoms and comedy series', 'USER', TRUE, FALSE, NOW() - INTERVAL '19 days', NOW()),
('oscar_v',     'oscar@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Oscar-winners and prestige TV', 'USER', TRUE, FALSE, NOW() - INTERVAL '18 days', NOW()),
('iris_m',      'iris@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Psychological and mindbender films', 'USER', TRUE, FALSE, NOW() - INTERVAL '17 days', NOW()),
('hugo_l',      'hugo@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Nature docs and wildlife', 'USER', TRUE, FALSE, NOW() - INTERVAL '16 days', NOW()),
('diane_t',     'diane@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'True stories and biopics', 'USER', TRUE, FALSE, NOW() - INTERVAL '15 days', NOW()),
('paul_n',      'paul@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Gritty crime and noir dramas', 'USER', TRUE, FALSE, NOW() - INTERVAL '14 days', NOW()),
('rima_a',      'rima@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Arab cinema and world films', 'USER', TRUE, FALSE, NOW() - INTERVAL '13 days', NOW()),
('chris_o',     'chris@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Indie films and festival darlings', 'USER', TRUE, FALSE, NOW() - INTERVAL '12 days', NOW()),
('vera_k',      'vera@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Eastern European cinema fan', 'USER', TRUE, FALSE, NOW() - INTERVAL '11 days', NOW()),
('lance_h',     'lance@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Action blockbusters from Chicago', 'USER', TRUE, FALSE, NOW() - INTERVAL '10 days', NOW()),
('sana_r',      'sana@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Lifestyle and feel-good shows', 'USER', TRUE, FALSE, NOW() - INTERVAL '9 days', NOW()),
('eric_m',      'eric@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Zombie and horror series', 'USER', TRUE, FALSE, NOW() - INTERVAL '8 days', NOW()),
('isla_p',      'isla@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Fantasy and mythology films', 'USER', TRUE, FALSE, NOW() - INTERVAL '7 days', NOW()),
('david_r',     'david@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Thriller series and whodunits', 'USER', TRUE, FALSE, NOW() - INTERVAL '6 days', NOW()),
('sara_f',      'saraf@test.com',      '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Medical and legal dramas', 'USER', TRUE, FALSE, NOW() - INTERVAL '5 days', NOW()),
('owen_l',      'owen@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Road-trip and adventure films', 'USER', TRUE, FALSE, NOW() - INTERVAL '4 days', NOW()),
('nadia_s',     'nadias@test.com',     '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Crime documentaries addict', 'USER', TRUE, FALSE, NOW() - INTERVAL '3 days', NOW()),
('marc_t',      'marc@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Superhero and sci-fi crossovers', 'USER', TRUE, FALSE, NOW() - INTERVAL '2 days', NOW()),
('rose_b',      'rose@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'Cozy crime and comfort TV', 'USER', TRUE, FALSE, NOW() - INTERVAL '1 day', NOW()),
('test_user',   'test@test.com',       '$2a$10$ti4kBMIOyYFPrKVJXsOCeeVBojgYJTCil1OlFTj6yWu4Ai4TnZOgu', 'General test user', 'USER', TRUE, FALSE, NOW(), NOW());

-- ── 3. UPDATE COUNTRIES ────────────────────────────────────────────────────
UPDATE users SET country = 'France'       WHERE username IN ('alex_m','isabelle_r','chloe_d','camille_d');
UPDATE users SET country = 'United Kingdom' WHERE username IN ('sarah_c','leo_f','lance_h');
UPDATE users SET country = 'Algeria'      WHERE username IN ('omar_k','amina_d','aya_m','ali_m');
UPDATE users SET country = 'Germany'      WHERE username IN ('nina_b','felix_b','vera_k');
UPDATE users SET country = 'Japan'        WHERE username IN ('yuki_t','hana_m','mei_l');
UPDATE users SET country = 'United States' WHERE username IN ('james_k','ryan_p','dan_o','mike_d','joe_m','kevin_l','ethan_c','lance_h','eric_m');
UPDATE users SET country = 'Spain'        WHERE username IN ('carlos_g','diego_r');
UPDATE users SET country = 'Morocco'      WHERE username IN ('rayan_h','nour_a','rima_a');
UPDATE users SET country = 'India'        WHERE username IN ('priya_s','aarav_p','rahul_s');
UPDATE users SET country = 'Italy'        WHERE username IN ('marco_f','nico_b');
UPDATE users SET country = 'Nigeria'      WHERE username IN ('aisha_l','karim_b');
UPDATE users SET country = 'South Korea'  WHERE username IN ('hana_m','emma_w');
UPDATE users SET country = 'Canada'       WHERE username IN ('adam_t','sam_r');
UPDATE users SET country = 'Egypt'        WHERE username IN ('ibrahim_a','fatima_z');
UPDATE users SET country = 'Russia'       WHERE username IN ('nadia_k','elena_b');
UPDATE users SET country = 'Brazil'       WHERE username IN ('tanya_b','mia_f');
UPDATE users SET country = 'Australia'    WHERE username IN ('jake_s','grace_l');
UPDATE users SET country = 'Netherlands'  WHERE username IN ('peter_v','anne_c');
UPDATE users SET country = 'Tunisia'      WHERE username IN ('ali_m','sana_r');
UPDATE users SET country = 'UAE'          WHERE username IN ('mo_a','yara_s');

-- ── 4. INSERT CONTENT REFERENCES ────────────────────────────────────────────
-- Movies (TMDB IDs of popular real movies)
INSERT INTO content_references (id, tmdb_id, content_type, created_at) VALUES
(gen_random_uuid(), 550,    'MOVIE', NOW()),   -- Fight Club
(gen_random_uuid(), 278,    'MOVIE', NOW()),   -- Shawshank Redemption
(gen_random_uuid(), 238,    'MOVIE', NOW()),   -- The Godfather
(gen_random_uuid(), 424,    'MOVIE', NOW()),   -- Schindler's List
(gen_random_uuid(), 27205,  'MOVIE', NOW()),   -- Inception
(gen_random_uuid(), 157336, 'MOVIE', NOW()),   -- Interstellar
(gen_random_uuid(), 155,    'MOVIE', NOW()),   -- The Dark Knight
(gen_random_uuid(), 680,    'MOVIE', NOW()),   -- Pulp Fiction
(gen_random_uuid(), 13,     'MOVIE', NOW()),   -- Forrest Gump
(gen_random_uuid(), 122,    'MOVIE', NOW()),   -- The Lord of the Rings: Return
(gen_random_uuid(), 274,    'MOVIE', NOW()),   -- The Silence of the Lambs
(gen_random_uuid(), 11216,  'MOVIE', NOW()),   -- Cinema Paradiso
(gen_random_uuid(), 497,    'MOVIE', NOW()),   -- The Green Mile
(gen_random_uuid(), 372058, 'MOVIE', NOW()),   -- Your Name (Kimi no Na wa)
(gen_random_uuid(), 19404,  'MOVIE', NOW()),   -- Dilwale Dulhania Le Jayenge
(gen_random_uuid(), 120,    'MOVIE', NOW()),   -- LOTR: Fellowship
(gen_random_uuid(), 121,    'MOVIE', NOW()),   -- LOTR: Two Towers
(gen_random_uuid(), 807,    'MOVIE', NOW()),   -- Se7en
(gen_random_uuid(), 769,    'MOVIE', NOW()),   -- GoodFellas
(gen_random_uuid(), 598,    'MOVIE', NOW()),   -- City of God
-- TV Shows
(gen_random_uuid(), 1396,   'TV', NOW()), -- Breaking Bad
(gen_random_uuid(), 1399,   'TV', NOW()), -- Game of Thrones
(gen_random_uuid(), 66732,  'TV', NOW()), -- Stranger Things
(gen_random_uuid(), 1418,   'TV', NOW()), -- The Big Bang Theory
(gen_random_uuid(), 1668,   'TV', NOW()), -- Friends
(gen_random_uuid(), 60735,  'TV', NOW()), -- The Flash
(gen_random_uuid(), 1402,   'TV', NOW()), -- The Walking Dead
(gen_random_uuid(), 63174,  'TV', NOW()), -- Lucifer
(gen_random_uuid(), 87108,  'TV', NOW()), -- Chernobyl
(gen_random_uuid(), 1408,   'TV', NOW()), -- House MD
(gen_random_uuid(), 44217,  'TV', NOW()), -- Vikings
(gen_random_uuid(), 76479,  'TV', NOW()), -- The Boys
(gen_random_uuid(), 71446,  'TV', NOW()), -- Money Heist
(gen_random_uuid(), 1622,   'TV', NOW()), -- Supernatural
(gen_random_uuid(), 456,    'TV', NOW()); -- The Simpsons

-- ── 5. WATCH HISTORY (15+ movies AND 12+ shows per user) ────────────────────
-- Uses a PL/pgSQL block to loop over all regular users and insert watches
DO $$
DECLARE
  u RECORD;
  cr RECORD;
  movie_ids BIGINT[];
  show_ids BIGINT[];
  movie_tmdb BIGINT[] := ARRAY[550,278,238,424,27205,157336,155,680,13,122,274,11216,497,372058,19404,120,121,807,769,598];
  show_tmdb  BIGINT[] := ARRAY[1396,1399,66732,1418,1668,60735,1402,63174,87108,1408,44217,76479,71446,1622,456];
  m_tmdb BIGINT;
  s_tmdb BIGINT;
  cr_id UUID;
  watch_time TIMESTAMP;
  i INT;
BEGIN
  FOR u IN SELECT id FROM users WHERE role = 'USER' LOOP
    -- Assign 15–20 movies per user
    FOR i IN 1..15 LOOP
      m_tmdb := movie_tmdb[((u.id + i - 1) % array_length(movie_tmdb,1)) + 1];
      SELECT id INTO cr_id FROM content_references WHERE tmdb_id = m_tmdb AND content_type = 'MOVIE';
      IF cr_id IS NOT NULL THEN
        watch_time := NOW() - (random() * INTERVAL '60 days');
        INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
        VALUES (gen_random_uuid(), u.id, cr_id, watch_time)
        ON CONFLICT DO NOTHING;
      END IF;
    END LOOP;
    -- Additional random movies so some titles are more popular
    FOR i IN 1..5 LOOP
      m_tmdb := movie_tmdb[(i % array_length(movie_tmdb,1)) + 1];
      SELECT id INTO cr_id FROM content_references WHERE tmdb_id = m_tmdb AND content_type = 'MOVIE';
      IF cr_id IS NOT NULL THEN
        INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
        VALUES (gen_random_uuid(), u.id, cr_id, NOW() - (random() * INTERVAL '7 days'))
        ON CONFLICT DO NOTHING;
      END IF;
    END LOOP;
    -- Assign 12 shows per user
    FOR i IN 1..12 LOOP
      s_tmdb := show_tmdb[((u.id + i - 1) % array_length(show_tmdb,1)) + 1];
      SELECT id INTO cr_id FROM content_references WHERE tmdb_id = s_tmdb AND content_type = 'TV';
      IF cr_id IS NOT NULL THEN
        watch_time := NOW() - (random() * INTERVAL '60 days');
        INSERT INTO user_watched (id, user_id, content_reference_id, watched_at)
        VALUES (gen_random_uuid(), u.id, cr_id, watch_time)
        ON CONFLICT DO NOTHING;
      END IF;
    END LOOP;
  END LOOP;
END $$;

-- ── 6. RATINGS (every user rates ~10 items) ───────────────────────────────
DO $$
DECLARE
  u RECORD;
  cr RECORD;
  ratings INT[] := ARRAY[6,7,7,8,8,8,9,9,10,10];
  i INT := 0;
BEGIN
  FOR u IN SELECT id FROM users WHERE role = 'USER' LOOP
    i := 0;
    FOR cr IN SELECT id FROM content_references ORDER BY tmdb_id LOOP
      IF i >= 10 THEN EXIT; END IF;
      INSERT INTO user_ratings (user_id, content_id, rating, created_at, updated_at)
      VALUES (u.id, cr.id, ratings[(i % 10) + 1], NOW() - (random() * INTERVAL '30 days'), NOW())
      ON CONFLICT DO NOTHING;
      i := i + 1;
    END LOOP;
  END LOOP;
END $$;

-- ── 7. FAVORITES ──────────────────────────────────────────────────────────
DO $$
DECLARE
  u RECORD;
  cr RECORD;
  i INT := 0;
BEGIN
  FOR u IN SELECT id FROM users WHERE role = 'USER' LOOP
    i := 0;
    FOR cr IN SELECT id FROM content_references WHERE content_type = 'MOVIE' LIMIT 5 LOOP
      INSERT INTO user_favorites (id, user_id, content_reference_id, created_at)
      VALUES (gen_random_uuid(), u.id, cr.id, NOW() - (random() * INTERVAL '20 days'))
      ON CONFLICT DO NOTHING;
    END LOOP;
  END LOOP;
END $$;

-- ── 8. COMMUNITY TOPICS ───────────────────────────────────────────────────
DO $$
DECLARE
  u_ids BIGINT[];
  uid BIGINT;
  topics TEXT[][] := ARRAY[
    ['Best sci-fi movies of the decade?','MOVIES'],
    ['Breaking Bad vs Sopranos: who wins?','TV_SHOWS'],
    ['Hidden gems you discovered recently','RECOMMENDATIONS'],
    ['Most rewatchable movies ever','MOVIES'],
    ['Best TV show endings of all time','TV_SHOWS'],
    ['Underrated films that deserve more love','RECOMMENDATIONS'],
    ['What are you watching this weekend?','RECOMMENDATIONS'],
    ['Movies that made you cry the most','MOVIES'],
    ['Is Game of Thrones worth watching after S8?','TV_SHOWS'],
    ['Best documentary you have ever seen','MOVIES'],
    ['Dark series recommendations','TV_SHOWS'],
    ['Inception explained — did you understand?','MOVIES'],
    ['Suggest a feature for this platform','SUGGESTIONS'],
    ['Favorite movie soundtrack of all time','MOVIES'],
    ['Most disappointing sequels ever made','MOVIES'],
    ['Shows cancelled too early','TV_SHOWS'],
    ['Best performance by an actor this year','MOVIES'],
    ['Platform bugs and feedback thread','SUPPORT'],
    ['International movies to add to your list','RECOMMENDATIONS'],
    ['Streaming platform wars — who is winning?','ANNOUNCEMENTS']
  ];
  t TEXT[];
  i INT;
BEGIN
  SELECT ARRAY(SELECT id FROM users WHERE role='USER' ORDER BY id LIMIT 20) INTO u_ids;
  FOR i IN 1..20 LOOP
    uid := u_ids[((i-1) % array_length(u_ids,1)) + 1];
    INSERT INTO community_topics (id, title, content, category, author_id, is_pinned, is_locked, upvote_count, reply_count, view_count, created_at, last_activity_at)
    VALUES (
      gen_random_uuid(), topics[i][1],
      'This is a community discussion about: ' || topics[i][1] || '. Share your thoughts, recommendations and experiences below!',
      topics[i][2], uid, (i <= 2), FALSE,
      (random() * 50)::INT, (random() * 30 + 2)::INT, (random() * 500 + 10)::INT,
      NOW() - ((21-i) * INTERVAL '3 days'),
      NOW() - ((21-i) * INTERVAL '3 days') + INTERVAL '2 hours'
    );
  END LOOP;
END $$;

-- ── 9. COMMUNITY REPLIES ─────────────────────────────────────────────────
DO $$
DECLARE
  t RECORD;
  u_ids BIGINT[];
  uid BIGINT;
  replies TEXT[] := ARRAY[
    'Great topic! I totally agree with this.',
    'I would add Inception to this list for sure.',
    'Breaking Bad is in a league of its own honestly.',
    'Have you tried watching Parasite? It changed my life.',
    'The cinematography alone makes it worth watching.',
    'I disagree, I think the ending was perfect.',
    'This platform needs a better search feature.',
    'Rewatched it three times and still find new details.',
    'The soundtrack is absolutely incredible in that one.',
    'Anyone else cannot stop thinking about the finale?',
    'Hidden gem alert: check out The Raid (2011).',
    'Money Heist season 2 is where it really picks up.',
    'Interstellar hits differently at 2am alone.',
    'The acting in Chernobyl is genuinely terrifying.',
    'Stranger Things season 4 brought me back completely.'
  ];
  i INT;
  r_idx INT;
  topic_idx INT := 0;
BEGIN
  SELECT ARRAY(SELECT id FROM users WHERE role='USER' ORDER BY id LIMIT 15) INTO u_ids;
  FOR t IN SELECT id FROM community_topics LOOP
    topic_idx := topic_idx + 1;
    FOR i IN 1..5 LOOP
      uid := u_ids[((i-1) % array_length(u_ids,1)) + 1];
      r_idx := ((i + topic_idx % 15) % 15) + 1;
      INSERT INTO community_replies (id, topic_id, author_id, content, is_deleted, created_at)
      VALUES (gen_random_uuid(), t.id, uid, replies[r_idx], FALSE, NOW() - (random() * INTERVAL '5 days'));
    END LOOP;
  END LOOP;
END $$;

-- ── 10. COMMUNITY UPVOTES ────────────────────────────────────────────────
DO $$
DECLARE
  t RECORD;
  u RECORD;
  i INT;
BEGIN
  FOR t IN SELECT id FROM community_topics LOOP
    i := 0;
    FOR u IN SELECT id FROM users WHERE role='USER' ORDER BY random() LIMIT 15 LOOP
      INSERT INTO community_upvotes (id, topic_id, user_id, created_at)
      VALUES (gen_random_uuid(), t.id, u.id, NOW() - (random() * INTERVAL '10 days'))
      ON CONFLICT DO NOTHING;
      i := i + 1;
      IF i >= 15 THEN EXIT; END IF;
    END LOOP;
  END LOOP;
END $$;

-- ── 11. USER LISTS & ITEMS ───────────────────────────────────────────────
DO $$
DECLARE
  u RECORD;
  cr RECORD;
  list_id UUID;
  i INT;
BEGIN
  FOR u IN SELECT id FROM users WHERE role='USER' LIMIT 30 LOOP
    list_id := gen_random_uuid();
    INSERT INTO user_lists (id, user_id, name, description, is_default, is_public, created_at, updated_at)
    VALUES (list_id, u.id, 'My Favorites', 'My personal curated list', FALSE, TRUE, NOW(), NOW());
    i := 0;
    FOR cr IN SELECT id FROM content_references ORDER BY random() LIMIT 8 LOOP
      INSERT INTO list_items (id, list_id, content_reference_id, added_at)
      VALUES (gen_random_uuid(), list_id, cr.id, NOW() - (random() * INTERVAL '30 days'))
      ON CONFLICT DO NOTHING;
      i := i + 1;
    END LOOP;
  END LOOP;
END $$;

-- ── 12. USER REVIEWS ─────────────────────────────────────────────────────
-- Each of the first 50 users reviews 4 movies and 3 TV shows with realistic text
DO $$
DECLARE
  u RECORD;
  cr_id UUID;
  movie_tmdb BIGINT[] := ARRAY[550,278,238,424,27205,157336,155,680,13,122,274,11216,497,372058,19404,120,121,807,769,598];
  show_tmdb  BIGINT[] := ARRAY[1396,1399,66732,1418,1668,60735,1402,63174,87108,1408,44217,76479,71446,1622,456];
  movie_reviews TEXT[] := ARRAY[
    'Absolutely phenomenal! The storytelling keeps you on the edge of your seat from start to finish. A true masterpiece of cinema.',
    'One of the best movies I have ever seen. The cinematography is breathtaking and the performances are Oscar-worthy.',
    'I was not expecting much but this completely blew me away. Every scene is crafted with such precision and care.',
    'A cinematic gem that deserves all the praise it gets. The director really outdid themselves with this one.',
    'Watched it three times already and I discover something new each viewing. Layers upon layers of meaning.',
    'The pacing is perfect and the ending left me speechless. This is what great filmmaking looks like.',
    'Incredible acting, stunning visuals, and a story that stays with you long after the credits roll.',
    'This movie changed how I look at cinema. Its emotional depth is unmatched by anything I have seen recently.',
    'A beautiful and powerful film that tackles complex themes with grace. Highly recommend to everyone.',
    'Not my usual genre but I was completely hooked. The character development is top tier throughout.',
    'Rewatched this classic and it still holds up perfectly. Timeless storytelling at its finest.',
    'The plot twists caught me off guard every time. Brilliantly written and expertly directed.',
    'A bit slow in the middle but the payoff is absolutely worth it. The finale is spectacular.',
    'The soundtrack alone makes this worth watching. Combined with the visuals it is a sensory experience.',
    'One of those rare films where everything comes together perfectly — acting, writing, direction, music.',
    'This one hit me emotionally way harder than I expected. Had to take a moment after it ended.',
    'Great performances all around. The lead actor gives the performance of a lifetime in this.',
    'I understand why this is considered an all-time classic. It sets the standard for the genre.',
    'A thought-provoking film that challenges you intellectually while keeping you entertained.',
    'Solid movie with some truly memorable scenes. The dialogue is sharp and quotable.'
  ];
  show_reviews TEXT[] := ARRAY[
    'Best show I have ever binge-watched. Every season gets better and the writing is consistently brilliant.',
    'This series redefines what television can be. The production quality rivals Hollywood blockbusters.',
    'Got hooked from episode one and could not stop watching. Characters feel like real people you care about.',
    'Impressive how they maintain the quality across all seasons. Every episode feels like a mini movie.',
    'The character arcs in this show are some of the best on television. You see genuine growth and change.',
    'Perfect mix of drama and entertainment. Never a dull moment and always keeps you guessing.',
    'Stayed up until 3am finishing this series. That is how gripping it is once it gets going.',
    'The world-building in this show is insane. You feel completely immersed in every episode.',
    'Started watching for fun but the depth of the storylines surprised me. Much smarter than expected.',
    'Incredible ensemble cast that brings every scene to life. The chemistry between actors is palpable.',
    'One of those shows that gets better on a rewatch — you catch so many details you missed before.',
    'The cinematography of this series is movie quality. Some shots are genuinely breathtaking.',
    'A masterclass in serialized storytelling. Each episode builds perfectly on the last.',
    'This show consumed my entire weekend and I regret nothing. Absolutely addictive television.',
    'The writing is so sharp and clever. Every line of dialogue matters and moves the story forward.'
  ];
  m_tmdb BIGINT;
  s_tmdb BIGINT;
  user_count INT := 0;
  i INT;
BEGIN
  FOR u IN SELECT id FROM users WHERE role = 'USER' LOOP
    user_count := user_count + 1;
    -- Each user reviews 10 movies (rotating through the pool)
    FOR i IN 1..10 LOOP
      m_tmdb := movie_tmdb[((user_count * 3 + i * 5 - 1) % array_length(movie_tmdb,1)) + 1];
      SELECT id INTO cr_id FROM content_references WHERE tmdb_id = m_tmdb AND content_type = 'MOVIE';
      IF cr_id IS NOT NULL THEN
        INSERT INTO reviews (id, user_id, content_reference_id, rating, review_text, likes_count, created_at, updated_at)
        VALUES (
          gen_random_uuid(), u.id, cr_id, 5,
          movie_reviews[((user_count + i - 1) % array_length(movie_reviews,1)) + 1],
          (random() * 25)::INT,
          NOW() - (random() * INTERVAL '60 days'),
          NOW() - (random() * INTERVAL '15 days')
        )
        ON CONFLICT ON CONSTRAINT uk_user_content_review DO NOTHING;
      END IF;
    END LOOP;
    -- Each user reviews 9 TV shows (rotating through the pool)
    FOR i IN 1..9 LOOP
      s_tmdb := show_tmdb[((user_count * 2 + i * 4 - 1) % array_length(show_tmdb,1)) + 1];
      SELECT id INTO cr_id FROM content_references WHERE tmdb_id = s_tmdb AND content_type = 'TV';
      IF cr_id IS NOT NULL THEN
        INSERT INTO reviews (id, user_id, content_reference_id, rating, review_text, likes_count, created_at, updated_at)
        VALUES (
          gen_random_uuid(), u.id, cr_id, 5,
          show_reviews[((user_count + i - 1) % array_length(show_reviews,1)) + 1],
          (random() * 18)::INT,
          NOW() - (random() * INTERVAL '50 days'),
          NOW() - (random() * INTERVAL '12 days')
        )
        ON CONFLICT ON CONSTRAINT uk_user_content_review DO NOTHING;
      END IF;
    END LOOP;
  END LOOP;
END $$;

-- ── VERIFICATION ─────────────────────────────────────────────────────────
SELECT 'users'             , COUNT(*) FROM users
UNION ALL SELECT 'content_references', COUNT(*) FROM content_references
UNION ALL SELECT 'user_watched'      , COUNT(*) FROM user_watched
UNION ALL SELECT 'user_ratings'      , COUNT(*) FROM user_ratings
UNION ALL SELECT 'reviews'           , COUNT(*) FROM reviews
UNION ALL SELECT 'community_topics'  , COUNT(*) FROM community_topics
UNION ALL SELECT 'community_replies' , COUNT(*) FROM community_replies;
