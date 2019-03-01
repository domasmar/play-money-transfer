-- Initial schema

-- !Ups

CREATE TABLE account (
  id            UUID         NOT NULL,
  `name`        VARCHAR(255) NOT NULL,
  last_transfer UUID,
  PRIMARY KEY (id)
);

CREATE TABLE transfer (
  id           UUID    NOT NULL,
  from_account UUID,
  to_account   UUID    NOT NULL,
  amount       INT(11) not null,
  PRIMARY KEY (id),
  FOREIGN KEY (from_account) references account (id),
  FOREIGN KEY (to_account) references account (id)
);

-- !Downs

DROP TABLE IF EXISTS transfer;
DROP TABLE IF EXISTS account;
