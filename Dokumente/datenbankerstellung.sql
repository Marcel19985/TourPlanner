--Datenbank Tabellen dienen derzeit nur als Test, wird noch nicht aktiv verwendet
DROP TABLE IF EXISTS tour;
DROP TABLE IF EXISTS tour_log;

CREATE TABLE tour (
                      tour_id SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      description TEXT,
                      start_date DATE,
                      end_date DATE,
                      distance DOUBLE PRECISION,
                      duration INTERVAL
);

CREATE TABLE tour_log (
                          log_id SERIAL PRIMARY KEY,
                          tour_id INTEGER REFERENCES tour(tour_id) ON DELETE CASCADE,
                          log_date DATE NOT NULL,
                          log_entry TEXT
);

