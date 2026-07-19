
INSERT INTO MPA (name)
SELECT name FROM (VALUES
                      ('G'),
                      ('PG'),
                      ('PG-13'),
                      ('R'),
                      ('NC-17')
                     ) AS m(name)
WHERE NOT EXISTS (SELECT 1 FROM MPA);


INSERT INTO GENRES (name)
SELECT name FROM (VALUES
                      ('Комедия'),        -- id=1
                      ('Драма'),          -- id=2
                      ('Мультфильм'),     -- id=3
                      ('Триллер'),        -- id=4
                      ('Документальный'), -- id=5
                      ('Боевик')          -- id=6
                     ) AS g(name)
WHERE NOT EXISTS (SELECT 1 FROM GENRES);


INSERT INTO FRIEND_STATUS (status_type)
SELECT FALSE
WHERE NOT EXISTS (SELECT 1 FROM FRIEND_STATUS WHERE status_type = FALSE);

INSERT INTO FRIEND_STATUS (status_type)
SELECT TRUE
WHERE NOT EXISTS (SELECT 1 FROM FRIEND_STATUS WHERE status_type = TRUE);

INSERT INTO Friend_Status (status_type)
SELECT FALSE
WHERE NOT EXISTS (SELECT 1 FROM Friend_Status WHERE status_type = FALSE);

INSERT INTO Friend_Status (status_type)
SELECT TRUE
WHERE NOT EXISTS (SELECT 1 FROM Friend_Status WHERE status_type = TRUE);