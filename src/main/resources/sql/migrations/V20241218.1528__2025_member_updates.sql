-- Updates for SENATE district 1.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1489, 'PALUMBO', 2025, 1);

-- Updates for SENATE district 2.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1492, 'MATTERA', 2025, 2);

-- Updates for SENATE district 3.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1521, 'MURRAY', 2025, 3);

-- Updates for SENATE district 4.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1221, 'MARTINEZ', 2025, 4);

-- Updates for SENATE district 5.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1516, 'RHOADS', 2025, 5);

-- Updates for SENATE district 6.
UPDATE public.member
SET incumbent = false
WHERE id = 1223;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Siela', '', 'Bynoe', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BYNOE', 2025, 6);

-- Updates for SENATE district 7.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (397, 'MARTINS', 2025, 7);

-- Updates for SENATE district 8.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1493, 'WEIK', 2025, 8);

-- Updates for SENATE district 9.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1517, 'CANZONERI-FITZPATRICK', 2025, 9);

-- Updates for SENATE district 10.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (432, 'SANDERS', 2025, 10);

-- Updates for SENATE district 11.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (400, 'STAVISKY', 2025, 11);

-- Updates for SENATE district 12.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (383, 'GIANARIS', 2025, 12);

-- Updates for SENATE district 13.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1226, 'RAMOS', 2025, 13);

-- Updates for SENATE district 14.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (888, 'COMRIE', 2025, 14);

-- Updates for SENATE district 15.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (384, 'ADDABBO', 2025, 15);

-- Updates for SENATE district 16.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1225, 'LIU', 2025, 16);

-- Updates for SENATE district 17.
UPDATE public.member
SET incumbent = false
WHERE id = 1514;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Stephen', 'T.', 'Chan', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'CHAN', 2025, 17);

-- Updates for SENATE district 18.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1227, 'SALAZAR', 2025, 18);

-- Updates for SENATE district 19.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (914, 'PERSAUD', 2025, 19);

-- Updates for SENATE district 20.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1228, 'MYRIE', 2025, 20);

-- Updates for SENATE district 21.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (416, 'PARKER', 2025, 21);

-- Updates for SENATE district 22.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (439, 'FELDER', 2025, 22);

-- Updates for SENATE district 23.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1512, 'SCARCELLA-SPANTON', 2025, 23);

-- Updates for SENATE district 24.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (409, 'LANZA', 2025, 24);

-- Updates for SENATE district 25.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1494, 'BRISPORT', 2025, 25);

-- Updates for SENATE district 26.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1229, 'GOUNARDES', 2025, 26);

-- Updates for SENATE district 27.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1140, 'KAVANAGH', 2025, 27);

-- Updates for SENATE district 28.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (401, 'KRUEGER', 2025, 28);

-- Updates for SENATE district 29.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (373, 'SERRANO', 2025, 29);

-- Updates for SENATE district 30.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1504, 'CLEARE', 2025, 30);

-- Updates for SENATE district 31.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1230, 'JACKSON', 2025, 31);

-- Updates for SENATE district 32.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1142, 'SEPULVEDA', 2025, 32);

-- Updates for SENATE district 33.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (377, 'RIVERA', 2025, 33);

-- Updates for SENATE district 34.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1520, 'FERNANDEZ', 2025, 34);

-- Updates for SENATE district 35.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (396, 'STEWART-COUSINS', 2025, 35);

-- Updates for SENATE district 36.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1102, 'BAILEY', 2025, 36);

-- Updates for SENATE district 37.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1143, 'MAYER', 2025, 37);

-- Updates for SENATE district 38.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1518, 'WEBER', 2025, 38);

-- Updates for SENATE district 39.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1519, 'ROLISON', 2025, 39);

-- Updates for SENATE district 40.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1233, 'HARCKHAM', 2025, 40);

-- Updates for SENATE district 41.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1497, 'HINCHEY', 2025, 41);

-- Updates for SENATE district 42.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1232, 'SKOUFIS', 2025, 42);

-- Updates for SENATE district 43.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1522, 'ASHBY', 2025, 43);

-- Updates for SENATE district 44.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1103, 'TEDISCO', 2025, 44);

-- Updates for SENATE district 45.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1490, 'STEC', 2025, 45);

-- Updates for SENATE district 46.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (372, 'BRESLIN', 2025, 46);

-- Updates for SENATE district 47.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (438, 'HOYLMAN-SIGAL', 2025, 47);

-- Updates for SENATE district 48.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1237, 'MAY', 2025, 48);

-- Updates for SENATE district 49.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1523, 'WALCZYK', 2025, 49);

-- Updates for SENATE district 50.
UPDATE public.member
SET incumbent = false
WHERE id = 1498;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Christopher', '', 'Ryan', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'RYAN C', 2025, 50);

-- Updates for SENATE district 51.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1499, 'OBERACKER', 2025, 51);

-- Updates for SENATE district 52.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1515, 'WEBB', 2025, 52);

-- Updates for SENATE district 53.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (393, 'GRIFFO', 2025, 53);

-- Updates for SENATE district 54.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (951, 'HELMING', 2025, 54);

-- Updates for SENATE district 55.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1500, 'BROUK', 2025, 55);

-- Updates for SENATE district 56.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1501, 'COONEY', 2025, 56);

-- Updates for SENATE district 57.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1370, 'BORRELLO', 2025, 57);

-- Updates for SENATE district 58.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (415, 'O''MARA', 2025, 58);

-- Updates for SENATE district 59.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1513, 'GONZALEZ', 2025, 59);

-- Updates for SENATE district 60.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (427, 'GALLIVAN', 2025, 60);

-- Updates for SENATE district 61.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1491, 'RYAN S', 2025, 61);

-- Updates for SENATE district 62.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (885, 'ORTT', 2025, 62);

-- Updates for SENATE district 63.
WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('April', '', 'Baskin', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BASKIN', 2025, 63);




-- Updates for ASSEMBLY district 1.
UPDATE public.member
SET incumbent = false
WHERE id = 464;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Tommy', '', 'Schiavoni', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'SCHIAVONI', 2025, 1);

-- Updates for ASSEMBLY district 2.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1464, 'GIGLIO', 2025, 2);

-- Updates for ASSEMBLY district 3.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1238, 'DESTEFANO', 2025, 3);

-- Updates for ASSEMBLY district 4.
UPDATE public.member
SET incumbent = false
WHERE id = 1524;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Rebecca', '', 'Kassay', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'KASSAY', 2025, 4);

-- Updates for ASSEMBLY district 5.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1148, 'SMITH', 2025, 5);

-- Updates for ASSEMBLY district 6.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (468, 'RAMOS', 2025, 6);

-- Updates for ASSEMBLY district 7.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1465, 'GANDOLFO', 2025, 7);

-- Updates for ASSEMBLY district 8.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (469, 'FITZPATRICK', 2025, 8);

-- Updates for ASSEMBLY district 9.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1466, 'DURSO', 2025, 9);

-- Updates for ASSEMBLY district 10.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1147, 'STERN', 2025, 10);

-- Updates for ASSEMBLY district 11.
UPDATE public.member
SET incumbent = false
WHERE id = 901;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Kwani', '', 'O''Pharrow', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'O''PHARROW', 2025, 11);

-- Updates for ASSEMBLY district 12.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1461, 'BROWN K', 2025, 12);

-- Updates for ASSEMBLY district 13.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (474, 'LAVINE', 2025, 13);

-- Updates for ASSEMBLY district 14.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (480, 'MCDONOUGH', 2025, 14);

-- Updates for ASSEMBLY district 15.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1511, 'BLUMENCRANZ', 2025, 15);

-- Updates for ASSEMBLY district 16.
UPDATE public.member
SET incumbent = false
WHERE id = 1467;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Daniel', '', 'Norber', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'NORBER', 2025, 16);

-- Updates for ASSEMBLY district 17.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1150, 'MIKULIN', 2025, 17);

-- Updates for ASSEMBLY district 18.
UPDATE public.member
SET incumbent = false
WHERE id = 1240;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Noah', '', 'Burroughs', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BURROUGHS', 2025, 18);

-- Updates for ASSEMBLY district 19.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (608, 'RA', 2025, 19);

-- Updates for ASSEMBLY district 20.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1509, 'BROWN E', 2025, 20);

-- Updates for ASSEMBLY district 21.
UPDATE public.member
SET incumbent = false
WHERE id = 607;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1241, 'GRIFFIN', 2025, 21);

-- Updates for ASSEMBLY district 22.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (673, 'SOLAGES', 2025, 22);

-- Updates for ASSEMBLY district 23.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1131, 'PHEFFER AMATO', 2025, 23);

-- Updates for ASSEMBLY district 24.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (484, 'WEPRIN', 2025, 24);

-- Updates for ASSEMBLY district 25.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (669, 'ROZIC', 2025, 25);

-- Updates for ASSEMBLY district 26.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (610, 'BRAUNSTEIN', 2025, 26);

-- Updates for ASSEMBLY district 27.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1545, 'BERGER', 2025, 27);

-- Updates for ASSEMBLY district 28.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (488, 'HEVESI', 2025, 28);

-- Updates for ASSEMBLY district 29.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1089, 'HYNDMAN', 2025, 29);

-- Updates for ASSEMBLY district 30.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1525, 'RAGA', 2025, 30);

-- Updates for ASSEMBLY district 31.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1459, 'ANDERSON', 2025, 31);

-- Updates for ASSEMBLY district 32.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (457, 'COOK', 2025, 32);

-- Updates for ASSEMBLY district 33.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1118, 'VANEL', 2025, 33);

-- Updates for ASSEMBLY district 34.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1468, 'GONZALEZ-ROJAS', 2025, 34);

-- Updates for ASSEMBLY district 35.
UPDATE public.member
SET incumbent = false
WHERE id = 460;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Larinda', '', 'Hooks', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'HOOKS', 2025, 35);

-- Updates for ASSEMBLY district 36.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1469, 'MAMDANI', 2025, 36);

-- Updates for ASSEMBLY district 37.
UPDATE public.member
SET incumbent = false
WHERE id = 1526;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Claire', '', 'Valdez', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'VALDEZ', 2025, 37);

-- Updates for ASSEMBLY district 38.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1470, 'RAJKUMAR', 2025, 38);

-- Updates for ASSEMBLY district 39.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1242, 'CRUZ', 2025, 39);

-- Updates for ASSEMBLY district 40.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (659, 'KIM', 2025, 40);

-- Updates for ASSEMBLY district 41.
UPDATE public.member
SET incumbent = false
WHERE id = 494;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Kalman', '', 'Yeger', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'YEGER', 2025, 41);

-- Updates for ASSEMBLY district 42.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (898, 'BICHOTTE HERMELYN', 2025, 42);

-- Updates for ASSEMBLY district 43.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1508, 'CUNNINGHAM', 2025, 43);

-- Updates for ASSEMBLY district 44.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1122, 'CARROLL R', 2025, 44);

-- Updates for ASSEMBLY district 45.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1527, 'NOVAKHOV', 2025, 45);

-- Updates for ASSEMBLY district 46.
UPDATE public.member
SET incumbent = false
WHERE id = 499;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (657, 'FAHY', 2025, 46);

-- Updates for ASSEMBLY district 47.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (500, 'COLTON', 2025, 47);

-- Updates for ASSEMBLY district 48.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1243, 'EICHENSTEIN', 2025, 48);

-- Updates for ASSEMBLY district 49.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1528, 'CHANG', 2025, 49);

-- Updates for ASSEMBLY district 50.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1471, 'GALLAGHER', 2025, 50);

-- Updates for ASSEMBLY district 51.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1472, 'MITAYNES', 2025, 51);

-- Updates for ASSEMBLY district 52.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (907, 'SIMON', 2025, 52);

-- Updates for ASSEMBLY district 53.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (655, 'DAVILA', 2025, 53);

-- Updates for ASSEMBLY district 54.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (900, 'DILAN', 2025, 54);

-- Updates for ASSEMBLY district 55.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (896, 'WALKER', 2025, 55);

-- Updates for ASSEMBLY district 56.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1473, 'ZINERMAN', 2025, 56);

-- Updates for ASSEMBLY district 57.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1474, 'FORREST', 2025, 57);

-- Updates for ASSEMBLY district 58.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1510, 'CHANDLER-WATERMAN', 2025, 58);

-- Updates for ASSEMBLY district 59.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1116, 'WILLIAMS', 2025, 59);

-- Updates for ASSEMBLY district 60.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1507, 'LUCAS', 2025, 60);

-- Updates for ASSEMBLY district 61.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1244, 'FALL', 2025, 61);

-- Updates for ASSEMBLY district 62.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1245, 'REILLY', 2025, 62);

-- Updates for ASSEMBLY district 63.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1529, 'PIROZZOLO', 2025, 63);

-- Updates for ASSEMBLY district 64.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1475, 'TANNOUSIS', 2025, 64);

-- Updates for ASSEMBLY district 65.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1530, 'LEE', 2025, 65);

-- Updates for ASSEMBLY district 66.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (519, 'GLICK', 2025, 66);

-- Updates for ASSEMBLY district 67.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (520, 'ROSENTHAL', 2025, 67);

-- Updates for ASSEMBLY district 68.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1505, 'GIBBS', 2025, 68);

-- Updates for ASSEMBLY district 69.
UPDATE public.member
SET incumbent = false
WHERE id = 522;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Micah', '', 'Lasher', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'LASHER', 2025, 69);

-- Updates for ASSEMBLY district 70.
UPDATE public.member
SET incumbent = false
WHERE id = 1128;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Jordan', '', 'Wright', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'WRIGHT', 2025, 70);

-- Updates for ASSEMBLY district 71.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1141, 'TAYLOR', 2025, 71);

-- Updates for ASSEMBLY district 72.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1506, 'DE LOS SANTOS', 2025, 72);

-- Updates for ASSEMBLY district 73.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1531, 'BORES', 2025, 73);

-- Updates for ASSEMBLY district 74.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1144, 'EPSTEIN', 2025, 74);

-- Updates for ASSEMBLY district 75.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1532, 'SIMONE', 2025, 75);

-- Updates for ASSEMBLY district 76.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (906, 'SEAWRIGHT', 2025, 76);

-- Updates for ASSEMBLY district 77.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1546, 'DAIS', 2025, 77);

-- Updates for ASSEMBLY district 78.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1533, 'ALVAREZ', 2025, 78);

-- Updates for ASSEMBLY district 79.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1476, 'JACKSON', 2025, 79);

-- Updates for ASSEMBLY district 80.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1534, 'ZACCARO', 2025, 80);

-- Updates for ASSEMBLY district 81.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (533, 'DINOWITZ', 2025, 81);

-- Updates for ASSEMBLY district 82.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (534, 'BENEDETTO', 2025, 82);

-- Updates for ASSEMBLY district 83.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (535, 'HEASTIE', 2025, 83);

-- Updates for ASSEMBLY district 84.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1477, 'SEPTIMO', 2025, 84);

-- Updates for ASSEMBLY district 85.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1548, 'TORRES', 2025, 85);

-- Updates for ASSEMBLY district 86.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1503, 'TAPIA', 2025, 86);

-- Updates for ASSEMBLY district 87.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1257, 'REYES', 2025, 87);

-- Updates for ASSEMBLY district 88.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (540, 'PAULIN', 2025, 88);

-- Updates for ASSEMBLY district 89.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (539, 'PRETLOW', 2025, 89);

-- Updates for ASSEMBLY district 90.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1258, 'SAYEGH', 2025, 90);

-- Updates for ASSEMBLY district 91.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (665, 'OTIS', 2025, 91);

-- Updates for ASSEMBLY district 92.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1535, 'SHIMSKY', 2025, 92);

-- Updates for ASSEMBLY district 93.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1478, 'BURDICK', 2025, 93);

-- Updates for ASSEMBLY district 94.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1536, 'SLATER', 2025, 94);

-- Updates for ASSEMBLY district 95.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1537, 'LEVENBERG', 2025, 95);

-- Updates for ASSEMBLY district 96.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1547, 'CARROLL P', 2025, 96);

-- Updates for ASSEMBLY district 97.
UPDATE public.member
SET incumbent = false
WHERE id = 1538;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Aron', '', 'Wieder', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'WIEDER', 2025, 97);

-- Updates for ASSEMBLY district 98.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (908, 'BRABENEC', 2025, 98);

-- Updates for ASSEMBLY district 99.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1539, 'EACHUS', 2025, 99);

-- Updates for ASSEMBLY district 100.
UPDATE public.member
SET incumbent = false
WHERE id = 549;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Paula', '', 'Kay', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'KAY', 2025, 100);

-- Updates for ASSEMBLY district 101.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1540, 'MAHER', 2025, 101);

-- Updates for ASSEMBLY district 102.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1152, 'TAGUE', 2025, 102);

-- Updates for ASSEMBLY district 103.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1541, 'SHRESTHA', 2025, 103);

-- Updates for ASSEMBLY district 104.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1259, 'JACOBSON', 2025, 104);

-- Updates for ASSEMBLY district 105.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1542, 'BEEPHAN', 2025, 105);

-- Updates for ASSEMBLY district 106.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (625, 'BARRETT', 2025, 106);

-- Updates for ASSEMBLY district 107.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1543, 'BENDETT', 2025, 107);

-- Updates for ASSEMBLY district 108.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (662, 'MCDONALD', 2025, 108);

-- Updates for ASSEMBLY district 109.
UPDATE public.member
SET incumbent = false
WHERE id = 657;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Gabriella', '', 'Romero', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'ROMERO', 2025, 109);

-- Updates for ASSEMBLY district 110.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (675, 'STECK', 2025, 110);

-- Updates for ASSEMBLY district 111.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (670, 'SANTABARBARA', 2025, 111);

-- Updates for ASSEMBLY district 112.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1127, 'WALSH', 2025, 112);

-- Updates for ASSEMBLY district 113.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (886, 'WOERNER', 2025, 113);

-- Updates for ASSEMBLY district 114.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1480, 'SIMPSON', 2025, 114);

-- Updates for ASSEMBLY district 115.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1120, 'JONES', 2025, 115);

-- Updates for ASSEMBLY district 116.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1544, 'GRAY', 2025, 116);

-- Updates for ASSEMBLY district 117.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (631, 'BLANKENBUSH', 2025, 117);

-- Updates for ASSEMBLY district 118.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1248, 'SMULLEN', 2025, 118);

-- Updates for ASSEMBLY district 119.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1249, 'BUTTENSCHON', 2025, 119);

-- Updates for ASSEMBLY district 120.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (574, 'BARCLAY', 2025, 120);

-- Updates for ASSEMBLY district 121.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1481, 'ANGELINO', 2025, 121);

-- Updates for ASSEMBLY district 122.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1125, 'MILLER', 2025, 122);

-- Updates for ASSEMBLY district 123.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (576, 'LUPARDO', 2025, 123);

-- Updates for ASSEMBLY district 124.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (636, 'FRIEND', 2025, 124);

-- Updates for ASSEMBLY district 125.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1482, 'KELLES', 2025, 125);

-- Updates for ASSEMBLY district 126.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1483, 'LEMONDES', 2025, 126);

-- Updates for ASSEMBLY district 127.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (571, 'STIRPE', 2025, 127);

-- Updates for ASSEMBLY district 128.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1105, 'HUNTER', 2025, 128);

-- Updates for ASSEMBLY district 129.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (570, 'MAGNARELLI', 2025, 129);

-- Updates for ASSEMBLY district 130.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1251, 'MANKTELOW', 2025, 130);

-- Updates for ASSEMBLY district 131.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1484, 'GALLAHAN', 2025, 131);

-- Updates for ASSEMBLY district 132.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (635, 'PALMESANO', 2025, 132);

-- Updates for ASSEMBLY district 133.
UPDATE public.member
SET incumbent = false
WHERE id = 1252;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Andrea', '', 'Bailey', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BAILEY', 2025, 133);

-- Updates for ASSEMBLY district 134.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1485, 'JENSEN', 2025, 134);

-- Updates for ASSEMBLY district 135.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1486, 'LUNSFORD', 2025, 135);

-- Updates for ASSEMBLY district 136.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1460, 'CLARK', 2025, 136);

-- Updates for ASSEMBLY district 137.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1462, 'MEEKS', 2025, 137);

-- Updates for ASSEMBLY district 138.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (633, 'BRONSON', 2025, 138);

-- Updates for ASSEMBLY district 139.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (589, 'HAWLEY', 2025, 139);

-- Updates for ASSEMBLY district 140.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1487, 'CONRAD', 2025, 140);

-- Updates for ASSEMBLY district 141.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (591, 'PEOPLES-STOKES', 2025, 141);

-- Updates for ASSEMBLY district 142.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1254, 'BURKE', 2025, 142);

-- Updates for ASSEMBLY district 143.
UPDATE public.member
SET incumbent = false
WHERE id = 1133;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Patrick', '', 'Chludzinski', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'CHLUDZINSKI', 2025, 143);

-- Updates for ASSEMBLY district 144.
UPDATE public.member
SET incumbent = false
WHERE id = 1126;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Paula', '', 'Bologna', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BOLOGNA', 2025, 144);

-- Updates for ASSEMBLY district 145.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1135, 'MORINELLO', 2025, 145);

-- Updates for ASSEMBLY district 146.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1255, 'MCMAHON', 2025, 146);

-- Updates for ASSEMBLY district 147.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (656, 'DIPIETRO', 2025, 147);

-- Updates for ASSEMBLY district 148.
UPDATE public.member
SET incumbent = false
WHERE id = 599;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Joseph', '', 'Sempolinski', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'SEMPOLINSKI', 2025, 148);

-- Updates for ASSEMBLY district 149.
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1488, 'RIVERA', 2025, 149);

-- Updates for ASSEMBLY district 150.
UPDATE public.member
SET incumbent = false
WHERE id = 642;

WITH p AS (
INSERT into public.person(first_name, middle_name, last_name, email, suffix, img_name)
VALUES ('Andrew', '', 'Molitor', '', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'MOLITOR', 2025, 150);

