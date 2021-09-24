DROP TABLE IF EXISTS venue;

CREATE TABLE venue (
  id SERIAL PRIMARY KEY,
  tribunal_office VARCHAR(100),
  code VARCHAR(100),
  name VARCHAR(100)
);

DROP TABLE IF EXISTS room;

CREATE TABLE room (
   id SERIAL PRIMARY KEY,
   code VARCHAR(100),
   name VARCHAR(100),
   venue_code VARCHAR(100)
);

DROP TABLE IF EXISTS court_worker;

CREATE TABLE court_worker (
  id SERIAL PRIMARY KEY,
  tribunal_office VARCHAR(100),
  type VARCHAR(20),
  code VARCHAR(100),
  name VARCHAR(100),
  venue_code VARCHAR(100)
);
