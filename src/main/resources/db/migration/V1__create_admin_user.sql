INSERT INTO _user (email, password, nome, role, bloqueado)
SELECT 'admin@caps.com', 'Caps@2025', 
       'Administrador do Sistema', 'ADMIN', false
WHERE NOT EXISTS (SELECT 1 FROM _user WHERE email = 'admin@caps.com');