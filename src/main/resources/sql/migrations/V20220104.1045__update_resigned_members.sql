-- Assembly members Barron and De la Rosa resigned from the Assembly effective December 31, 2021 at 11:59pm
UPDATE member
SET incumbent = false
WHERE id IN (1129, 493);
