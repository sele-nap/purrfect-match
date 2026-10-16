-- Demo data, loaded only with the "dev" profile.
DELETE FROM adoption_request;
DELETE FROM cat;
INSERT INTO cat (name, sex, birth_date, description, status, version, arrived_at) VALUES
    ('Miso',    'FEMALE', DATE '2023-04-12', 'Calm, loves laps.',             'AVAILABLE', 0, CURRENT_TIMESTAMP),
    ('Pistache','MALE',   DATE '2024-08-01', 'Young and playful, gets along with dogs.', 'AVAILABLE', 0, CURRENT_TIMESTAMP),
    ('Licorice','FEMALE', DATE '2015-02-20', 'Quiet senior looking for a cozy retirement.', 'AVAILABLE', 0, CURRENT_TIMESTAMP);
