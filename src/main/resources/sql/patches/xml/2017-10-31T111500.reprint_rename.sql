
--- Author: Sam Stouffer
--- Renames the bill reprint column and gives it a comment
--- Also puts a comment on the bill blurb column

ALTER TABLE master.bill RENAME reprint_of_bill TO reprint_no;

COMMENT ON COLUMN master.bill.reprint_no IS 'Points to a reprint of the bill in the same session (if applicable)';

COMMENT ON COLUMN master.bill.blurb IS 'Brief summary of pertinent bill info';
