-- Assemblyman Harvey Epstein resigned.
UPDATE member
SET incumbent = false
WHERE id = 1144
  AND person_id = 964;
