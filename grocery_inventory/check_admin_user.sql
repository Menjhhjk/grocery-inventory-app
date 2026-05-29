-- Confirm the seeded admin user exists and whether the stored hash matches
-- the expected BCrypt hash for password: adminpassword
SELECT
    username,
    role,
    password_hash = '$2a$12$2cHOnE.TGDRHpPpsusNXBuhGbVO5FlEjvdAIt1XfD8E7jFXSidthO' AS has_expected_adminpassword_hash,
    LENGTH(password_hash) AS hash_length,
    created_at
FROM users
WHERE username = 'admin123';
