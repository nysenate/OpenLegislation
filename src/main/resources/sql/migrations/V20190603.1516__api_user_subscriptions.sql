create type public.apiuser_subscription_type as enum (
   'BREAKING_CHANGES',
   'NEW_FEATURES'
);

CREATE TABLE public.apiuser_subscription(
    apikey varchar(32) not null references public.apiuser(apikey),
    subscription_type public.apiuser_subscription_type not null,
    UNIQUE(apikey, subscription_type)
);

COMMENT ON TABLE public.apiuser_subscription IS 'E-mail subscriptions for API users';
COMMENT ON COLUMN public.apiuser_subscription.apikey IS 'The user''s API Key, 32 Characters in length';
COMMENT ON COLUMN public.apiuser_subscription.subscription_type IS 'Type of message sent with subscription';
