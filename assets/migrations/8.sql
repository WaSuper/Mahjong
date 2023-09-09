ALTER TABLE RankItem ADD AverageHepai INTEGER
ALTER TABLE RankItem ADD AverageBomb INTEGER
UPDATE RankItem SET AverageHepai = 0
UPDATE RankItem SET AverageBomb = 0