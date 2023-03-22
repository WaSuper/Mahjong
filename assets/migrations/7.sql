ALTER TABLE MjResult ADD MemberCount INTEGER
ALTER TABLE MjResult ADD MainType INTEGER
ALTER TABLE MjResult ADD ExtraData INTEGER
UPDATE MjResult SET MemberCount  = 4
UPDATE MjResult SET MainType  = 0
UPDATE MjResult SET ExtraData = 0
CREATE TABLE tmpRankItem (_id INTEGER PRIMARY KEY AUTOINCREMENT, PlayerId TEXT, Spectrum TEXT, Fan INTEGER, Fu INTEGER, StartTime INTEGER, LogTime INTEGER, RecentRanks TEXT, RecentFlys TEXT, RecentChickens TEXT, BattleCount INTEGER, Rank1Count INTEGER, Rank2Count INTEGER, Rank3Count INTEGER, Rank4Count INTEGER, MaxBanker INTEGER, TotalPoint REAL, RoundCount INTEGER, LizhiCount INTEGER, HepaiCount INTEGER, ZimoCount INTEGER, BombCount INTEGER, FlyCount INTEGER, ChickenCount INTEGER, MainType INTEGER)
INSERT INTO tmpRankItem (PlayerId, Spectrum, Fan, Fu, StartTime, LogTime, RecentRanks, RecentFlys, RecentChickens, BattleCount, Rank1Count, Rank2Count, Rank3Count, Rank4Count, MaxBanker, TotalPoint, RoundCount, LizhiCount, HepaiCount, ZimoCount, BombCount, FlyCount, ChickenCount) SELECT PlayerId, Spectrum, Fan, Fu, StartTime, LogTime, RecentRanks, RecentFlys, RecentChickens, BattleCount, Rank1Count, Rank2Count, Rank3Count, Rank4Count, MaxBanker, TotalPoint, RoundCount, LizhiCount, HepaiCount, ZimoCount, BombCount, FlyCount, ChickenCount FROM RankItem
UPDATE tmpRankItem SET MainType = 0
DROP TABLE RankItem
ALTER TABLE tmpRankItem RENAME TO RankItem