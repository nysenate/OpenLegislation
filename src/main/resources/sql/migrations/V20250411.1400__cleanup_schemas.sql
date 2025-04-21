ALTER TABLE public.adminuser
RENAME TO admin_user;

ALTER TABLE public.apiuser
RENAME TO api_user;

ALTER TABLE public.apiuser_roles
RENAME TO api_user_role;

ALTER TABLE public.apiuser_subscription
RENAME TO api_user_subscription;

ALTER TABLE master.alert_calendar_reference
RENAME TO alert_calendar;

ALTER TABLE master.alert_active_list_reference
RENAME TO alert_active_list;

ALTER TABLE master.alert_active_list_entry_reference
RENAME TO alert_active_list_entry;

ALTER TABLE master.alert_supplemental_reference
RENAME TO alert_supplemental;

ALTER TABLE master.alert_supplemental_entry_reference
RENAME TO alert_supplemental_entry;
