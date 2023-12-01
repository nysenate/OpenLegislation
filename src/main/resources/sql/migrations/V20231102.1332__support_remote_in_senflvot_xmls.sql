ALTER TABLE master.bill_amendment_vote_roll
ADD COLUMN IF NOT EXISTS is_remote boolean NOT NULL DEFAULT false;
