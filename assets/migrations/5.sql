ALTER TABLE AudioItem ADD SoundBoxId INTEGER
DELETE FROM AudioItem
UPDATE sqlite_sequence SET seq = 0 WHERE name = 'AudioItem'