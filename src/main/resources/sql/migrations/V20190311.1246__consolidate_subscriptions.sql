SET SEARCH_PATH = master;

-- Digest subscription table will no longer be used, their functionality will be merged with standard notifications
DROP TABLE IF EXISTS notification_digest_subscription;

-- Drop unique constraint for subs
ALTER TABLE notification_subscription
  DROP CONSTRAINT IF EXISTS notification_subscription_user_type_target_address_key
;

-- Add new columns to subscription table
ALTER TABLE notification_subscription
  ADD COLUMN IF NOT EXISTS detail BOOLEAN DEFAULT TRUE
;
COMMENT ON COLUMN notification_subscription.detail
  IS 'Get detailed notifications if true, otherwise just the subject.'
;
ALTER TABLE notification_subscription
  ADD COLUMN IF NOT EXISTS last_sent TIMESTAMP WITHOUT TIME ZONE
;
COMMENT ON COLUMN notification_subscription.last_sent
  IS 'The last time a notification was sent for this subscription.'
;
ALTER TABLE notification_subscription
  ADD COLUMN IF NOT EXISTS active BOOLEAN DEFAULT TRUE
;
COMMENT ON COLUMN notification_subscription.active
  IS 'Notifications will not be sent if this is false.'
;

ALTER TABLE notification_subscription
  RENAME COLUMN target TO medium
;

-- Add subscription type column and rename notification type column to remove ambiguity.

CREATE TYPE notification_subscription_type AS ENUM ('scheduled', 'instant');

ALTER TABLE notification_subscription
  RENAME COLUMN type TO notification_type
;

ALTER TABLE notification_subscription
  ADD COLUMN IF NOT EXISTS subscription_type notification_subscription_type
;
COMMENT ON COLUMN notification_subscription.subscription_type
  IS 'Determines how the subscription is handled.'
;

-- Add rate limit table for instant notifications

CREATE TABLE master.notification_rate_limit
(
  subscription_id INTEGER PRIMARY KEY NOT NULL
    REFERENCES master.notification_subscription (id) ON DELETE CASCADE,
  rate_limit      INTERVAL            NOT NULL
);

COMMENT ON TABLE notification_rate_limit
  IS 'Rate limits for instant notifications.'
;

COMMENT ON COLUMN notification_rate_limit.subscription_id
  IS 'Links this rate limit to the notification table.'
;
COMMENT ON COLUMN notification_rate_limit.rate_limit
  IS 'Subscriber will receive no more than 1 notification digest for any period this length of time.'
;

-- Set all existing subscriptions as periodic with period 0, which matches current functionality.
-- Also set not null on the subscription type after setting it for the current subs.

UPDATE notification_subscription
SET subscription_type = 'instant'::notification_subscription_type
WHERE subscription_type IS NULL
;

ALTER TABLE notification_subscription
  ALTER COLUMN subscription_type SET NOT NULL
;

INSERT INTO notification_rate_limit (subscription_id, rate_limit)
SELECT id, '0'::INTERVAL
FROM notification_subscription
;

-- Add scheduled notification detail table

CREATE TABLE master.notification_schedule
(
  subscription_id INTEGER PRIMARY KEY NOT NULL
    REFERENCES master.notification_subscription (id) ON DELETE CASCADE,
  days_of_week    SMALLINT[],
  time_of_day     TIME                NOT NULL,
  send_empty      BOOLEAN             NOT NULL
);

COMMENT ON TABLE notification_schedule
  IS 'Schedules for scheduled notifications.'
;

COMMENT ON COLUMN notification_schedule.subscription_id
  IS 'Links this schedule to the notification table.'
;
COMMENT ON COLUMN notification_schedule.days_of_week
  IS 'List of days of the week when notifications should be sent.  Interpreted as every day if null/empty.'
;
COMMENT ON COLUMN notification_schedule.time_of_day
  IS 'Time of day when the notifications should be sent.'
;
COMMENT ON COLUMN notification_schedule.send_empty
  IS 'Whether or not to send a notification email if nothing occurred in the scheduled period.'
;
