ALTER TABLE public.apiuser
ADD COLUMN create_date_time timestamp without time zone DEFAULT now();