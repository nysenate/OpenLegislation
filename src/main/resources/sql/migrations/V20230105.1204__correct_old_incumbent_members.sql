UPDATE public.member
SET incumbent = false
WHERE id NOT IN (
    SELECT member_id
    FROM session_member
    WHERE session_year = 2021
);
