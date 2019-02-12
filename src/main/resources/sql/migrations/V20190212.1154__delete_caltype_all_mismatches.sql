-- Delete bad Calendar mismatches. CalendarType.ALL was removed for #12223, sequenceNo's should not be Integer.MAX_VALUE.

DELETE FROM master.spotcheck_mismatch
WHERE (key->'type' = 'ALL') OR (key->'sequenceNo' = '2147483647');