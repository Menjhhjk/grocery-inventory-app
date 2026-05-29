-- Reset seeded admin account to password: adminpassword.
-- This works whether the admin row already exists or not.
INSERT INTO users (username, password_hash, role)
VALUES (
    'admin123',
    '$2a$12$2cHOnE.TGDRHpPpsusNXBuhGbVO5FlEjvdAIt1XfD8E7jFXSidthO',
    'ADMIN'
)
ON CONFLICT (username) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    role = 'ADMIN';
