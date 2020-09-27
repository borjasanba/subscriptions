/*! SET storage_engine=INNODB */;
/*! SET 'utf8' DEFAULT */;

CREATE TABLE subscription (
  id VARCHAR(36) NOT NULL,
  email varchar(254) NOT NULL,
  first_name VARCHAR(50),
  gender VARCHAR(18),
  date_of_birth DATE NOT NULL,
  consent BOOLEAN NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id)
);