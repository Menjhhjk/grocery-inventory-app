-- Add account roles to an existing database and make admin123 an admin.
ALTER TABLE users
ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'VIEWER';

UPDATE users
SET role = 'VIEWER'
WHERE role IS NULL OR role NOT IN ('ADMIN', 'OPERATOR', 'VIEWER');

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'ck_users_role'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'OPERATOR', 'VIEWER'));
    END IF;
END;
$$;

INSERT INTO users (username, password_hash, role)
VALUES (
    'admin123',
    '$2a$12$2cHOnE.TGDRHpPpsusNXBuhGbVO5FlEjvdAIt1XfD8E7jFXSidthO',
    'ADMIN'
)
ON CONFLICT (username) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    role = 'ADMIN';

INSERT INTO users (username, password_hash, role)
VALUES (
    'Employee',
    '$2a$12$GL4GFBFnbA4wOePBIE1MVeqvMMNr.uX8aS4LfBrJw1Wl3rQe0pNGG',
    'OPERATOR'
)
ON CONFLICT (username) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    role = 'OPERATOR';

INSERT INTO users (username, password_hash, role)
VALUES (
    'Vieweronly',
    '$2a$12$4okePN7C/Y6yfEjw6OGWweD80MRoxX20xB6BjA9iT/DZu6Wl1BFyu',
    'VIEWER'
)
ON CONFLICT (username) DO UPDATE
SET password_hash = EXCLUDED.password_hash,
    role = 'VIEWER';
