--Marks people who lost races, retired, or switched houses as no longer being incumbents.

UPDATE public.member AS m
SET incumbent = false
FROM public.session_member sm
WHERE m.id = sm.member_id AND
      sm.session_year = 2021 AND
      sm.lbdc_short_name SIMILAR TO 'AKSHAR|ASHBY|BIAGGI|BOYLE|BROOKS|GAUGHRAN|FERNANDEZ|JORDAN|KAMINSKY|KAPLAN|MARTUCCI|RATH|REICHLIN-MELNICK|RITCHIE|SAVINO|SERINO|WALCZYK';
