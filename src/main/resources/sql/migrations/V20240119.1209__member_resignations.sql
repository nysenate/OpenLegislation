-- Assembly member Joyner resigned.
UPDATE member
SET incumbent = false
WHERE id = 902
AND person_id = 724;