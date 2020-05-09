CREATE TABLE role_diploma (
  id INT AUTO_INCREMENT PRIMARY KEY,
  role_id INT,
  role VARCHAR(255) NOT NULL
)  ENGINE=INNODB;

CREATE TABLE user_diploma (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL,
  user_active BOOLEAN,
  user_email VARCHAR(255) NOT NULL,
  user_password VARCHAR(1000) NOT NULL,
  created_date DATE,
  user_last_name VARCHAR(255) NOT NULL,

)  ENGINE=INNODB;

CREATE TABLE user_role_diploma (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  role_id INT
)  ENGINE=INNODB;