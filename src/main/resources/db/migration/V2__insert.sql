insert into role_diploma (role_id, role) VALUES (1, 'MANAGER');

insert into user_diploma (user_name, user_active, user_email,
                          user_password, created_date, user_last_name)
VALUES ('Mederbek', TRUE , 'meder@gmail.com', '$2a$10$XyH0U3VX2Us.IW.g6wtf7uVqkkIKhZWXO4zLUeedW18YkryKLdMu2', current_date, 'Abdyldaev');

INSERT INTO user_role_diploma (user_id, role_id) VALUES (1,1);