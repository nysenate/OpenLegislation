-- Corrects some image names.
UPDATE public.person
SET img_name = '550_frank_skartados.jpg'
WHERE img_name = '550_frank_skartados_.jpg';

UPDATE public.person
SET img_name = '545_kenneth_zebrowski.jpg'
WHERE img_name = '545_"kenneth_zebrowski.jpg';

UPDATE public.person
SET img_name = '530_jose_rivera.jpg'
WHERE full_name = 'Jose Rivera';

UPDATE public.person
SET img_name = 'Monique Chandler-Waterman.jpg'
WHERE img_name = 'Monique Chandler-Waterman.jpeg';

-- Image names can't contain accented characters, or else they won't load properly.
UPDATE public.person
SET img_name = '428_jose_peralta.jpg'
WHERE img_name = '428_josé_peralta.jpg';

UPDATE public.person
SET img_name = '493_Luis_Sepulveda.jpg'
WHERE img_name = '493_Luis_Sepúlveda.jpg';

--Adds in some images that weren't assigned before.
UPDATE public.person
SET img_name = full_name || '.jpg'
WHERE full_name SIMILAR TO 'Greg Ball|Brian X. Foley|William T. Stachowski|Cecilia Tkaczyk|' ||
                           'Stephen M. Saland|Carl Kruger|Vincent L. Leibell|George D. Maziarz|' ||
                           'James S. Alesi|Ted O''Brien|Darrel J. Aubertine|Eric Adams|' ||
                           'Thomas K. Duane|Suzi Oppenheimer|George Winner|Shirley L. Huntley|' ||
                           'Roy J. McDonald|Frank Padavan|Terry Gipson|Owen H. Johnson|' ||
                           'Antoine M. Thompson|Mark Grisanti|Dale M. Volker|Thomas P. Morahan|' ||
                           'Lee M. Zeldin|Eric T. Schneiderman|George Onorato|Craig M. Johnson|' ||
                           'Malcolm A. Smith|Charles J. Fuschillo Jr.|Pedro Espada Jr.';
-- Cleans up image names ending in "Jr..jpg".
UPDATE public.person
SET img_name = replace(img_name, '..', '.');

UPDATE public.person
SET img_name = 'no_image.jpg'
WHERE img_name IS NULL OR img_name = '';

ALTER TABLE public.person
ALTER img_name SET DEFAULT 'no_image.jpg';

ALTER TABLE public.person
ADD CONSTRAINT simple_img_name CHECK (person.img_name SIMILAR TO '[a-zA-Z_0-9 -.\'']+.jpg')
