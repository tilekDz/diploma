CREATE TABLE role_diploma (
  role_id INT(11) auto_INCREMENT PRIMARY KEY,
  role VARCHAR(255)
);

CREATE TABLE user_diploma (
  id BIGINT(20) AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(255),
  user_active BIT(1),
  user_email VARCHAR(255),
  user_password VARCHAR(255),
  created_date DATETIME,
  user_last_name VARCHAR(255)
);

CREATE TABLE user_role_diploma (
  user_id BIGINT(20),
  role_id INT(11)
);