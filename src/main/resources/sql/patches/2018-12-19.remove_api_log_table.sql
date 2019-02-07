
-- Removes the request and response tables in the public schema
-- Performs a TRUNCATE first to reclaim space.

TRUNCATE public.response;
DROP TABLE public.response;
TRUNCATE public.request;
DROP TABLE public.request;
