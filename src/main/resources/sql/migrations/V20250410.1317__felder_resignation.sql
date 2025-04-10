-- Senator Felder resigned.
UPDATE member
SET incumbent = false
WHERE id = 439
  AND person_id = 261;
