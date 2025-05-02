INSERT INTO codigo_booking_db.packages (name, country_code, credits, price, valid_days, created_date)
VALUES ('Basic Package SG', 'SG', 5, 19.99, 30, NOW()),
       ('Standard Package SG', 'SG', 10, 34.99, 60, NOW()),
       ('Premium Package SG', 'SG', 20, 59.99, 90, NOW()),
       ('Basic Package MM', 'MM', 5, 14.99, 30, NOW()),
       ('Premium Package MM', 'MM', 20, 44.99, 90, NOW());

INSERT INTO codigo_booking_db.classes (name, country_code, start_time, end_time, credit_required, max_capacity,
                                       created_date)
VALUES ('Yoga Morning SG', 'SG', '2025-05-03T07:00:00', '2025-05-03T08:00:00', 2, 20, '2025-05-01T09:00:00'),
       ('HIIT Evening MM', 'MM', '2025-05-03T18:00:00', '2025-05-03T19:00:00', 2, 25, '2025-05-01T09:30:00'),
       ('Pilates Noon SG', 'SG', '2025-05-04T12:00:00', '2025-05-04T13:00:00', 2, 15, '2025-05-01T10:00:00'),
       ('Zumba Night MM', 'MM', '2025-05-05T20:00:00', '2025-05-05T21:00:00', 2, 30, '2025-05-01T11:00:00'),
       ('Meditation SG', 'SG', '2025-05-06T06:30:00', '2025-05-06T07:15:00', 1, 10, '2025-05-01T12:00:00'),
       ('AMH Morning SG', 'SG', '2025-05-03T04:00:00', '2025-05-03T06:00:00', 2, 3, '2025-05-01T09:00:00'),
       ('AMH Morning MM', 'MM', '2025-05-03T05:00:00', '2025-05-03T07:00:00', 2, 3, '2025-05-01T09:30:00');