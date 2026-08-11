-- Senator Gianaris retired effective August 7th, 2026, at 11:59 PM
UPDATE member
SET incumbent = false
WHERE id = 383
  AND person_id = 202;