-- Members Brian Benjamin, Victor Pichardo, and Robert Rodriguez have left office.
UPDATE public.member
SET incumbent = false
WHERE id IN (1136, 667, 616);
