DROP TABLE IF EXISTS master.notification_rate_limit;
DROP TABLE IF EXISTS master.notification_schedule;

ALTER TABLE IF EXISTS master.notification_subscription
DROP COLUMN IF EXISTS last_sent;

ALTER TABLE IF EXISTS master.notification_subscription
DROP COLUMN IF EXISTS subscription_type;