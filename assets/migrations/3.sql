CREATE TABLE tmpPlayer (_id INTEGER PRIMARY KEY AUTOINCREMENT, Uuid TEXT UNIQUE ON CONFLICT FAIL, Name TEXT, NickName TEXT, Sex TEXT, Sign TEXT, Icon TEXT, CharacterId INTEGER)
INSERT INTO tmpPlayer (Uuid, Name, NickName, Sex, Sign, Icon) SELECT Uuid, Name, NickName, Sex, Sign, Icon FROM Player
UPDATE tmpPlayer SET CharacterId = -1
DROP TABLE Player
ALTER TABLE tmpPlayer RENAME TO Player