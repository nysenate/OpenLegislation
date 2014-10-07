-- Use this patch to fix sobi data issues that were resolved manually
-- Will only update existing sobi data, will not insert

UPDATE master.sobi_fragment
SET text = '2013A00083 1Wright (MS)         00000                                   00000
2013A00083 2Labor Law
2013A00083 3Relates to the minimum wage and makes technical changes to the labor law relating thereto
2013A00083 401/09/13 referred to labor
2013A00083 1DELETE              00000                                   00000
2013A00038 1Wright (MS)         00000 Lab. min wage                     00000
2013A00038 2Labor Law
2013A00038 3Relates to the minimum wage and makes technical changes to the labor law relating thereto
2013A00038 401/09/13 referred to labor
2013A00038 1                    00000 Lab. min wage                     00000
2013A00038 6Wright
2013A00038 7Silver, Farrell, Hooper, Rivera, Peoples-Stokes, Jacobs, Markey, Gibson, Miller, Abinanti
2013A00038 8Abbate, Arroyo, Aubry, Barron, Benedetto, Boyland, Braunstein, Brennan, Bronson, Brook-Krasny,
2013A00038 8Buchwald, Cahill, Camara, Castro, Clark, Colton, Cook, Crespo, Cymbrowitz, DenDekker, Dinowitz,
2013A00038 8Englebright, Espinal, Fahy, Galef, Gantt, Glick, Gottfried, Heastie, Hikind, Jaffee, Kavanagh,
2013A00038 8Kim, Lavine, Lentol, Lifton, Lopez V, Magnarelli, Maisel, McDonald, Millman, Mosley, Moya, Nolan,
2013A00038 8Otis, Paulin, Perry, Pretlow, Ramos, Roberts, Robinson, Rodriguez, Rosa, Rosenthal, Rozic,
2013A00038 8Russell, Ryan, Santabarbara, Scarborough, Schimel, Sepulveda, Simanowitz, Simotas, Skoufis,
2013A00038 8Solages, Steck, Stevenson, Stirpe, Sweeney, Thiele, Titone, Titus, Weinstein, Weisenberg, Weprin
2013A00038 1                    00000 Lab. min wage                     00000
2013A00038 6Wright
2013A00038 7Silver, Farrell, Hooper, Rivera, Peoples-Stokes, Jacobs, Markey, Gibson, Miller, Abinanti
2013A00038 8Abbate, Arroyo, Aubry, Barron, Benedetto, Boyland, Braunstein, Brennan, Bronson, Brook-Krasny,
2013A00038 8Buchwald, Cahill, Camara, Castro, Clark, Colton, Cook, Crespo, Cymbrowitz, DenDekker, Dinowitz,
2013A00038 8Englebright, Espinal, Fahy, Galef, Gantt, Glick, Gottfried, Heastie, Hikind, Jaffee, Kavanagh,
2013A00038 8Kim, Lavine, Lentol, Lifton, Lopez V, Magnarelli, Maisel, McDonald, Millman, Mosley, Moya, Nolan,
2013A00038 8Otis, Paulin, Perry, Pretlow, Ramos, Roberts, Robinson, Rodriguez, Rosa, Rosenthal, Rozic,
2013A00038 8Russell, Ryan, Santabarbara, Scarborough, Schimel, Sepulveda, Simanowitz, Simotas, Skoufis,
2013A00038 8Solages, Steck, Stevenson, Stirpe, Sweeney, Thiele, Titone, Titus, Weinstein, Weisenberg, Weprin
2013A00038 1                    00000 Lab. min wage                     00000
2013A00038 6Wright
2013A00038 7Silver, Farrell, Hooper, Rivera, Peoples-Stokes, Jacobs, Markey, Gibson, Miller, Abinanti
2013A00038 8Abbate, Arroyo, Aubry, Barron, Benedetto, Boyland, Braunstein, Brennan, Bronson, Brook-Krasny,
2013A00038 8Buchwald, Cahill, Camara, Castro, Clark, Colton, Cook, Crespo, Cymbrowitz, DenDekker, Dinowitz,
2013A00038 8Englebright, Espinal, Fahy, Galef, Gantt, Glick, Gottfried, Heastie, Hikind, Jaffee, Kavanagh,
2013A00038 8Kim, Lavine, Lentol, Lifton, Lopez V, Magnarelli, Maisel, McDonald, Millman, Mosley, Moya, Nolan,
2013A00038 8Otis, Paulin, Perry, Pretlow, Ramos, Roberts, Robinson, Rodriguez, Rosa, Rosenthal, Rozic,
2013A00038 8Russell, Ryan, Santabarbara, Scarborough, Schimel, Sepulveda, Simanowitz, Simotas, Skoufis,
2013A00038 8Solages, Steck, Stevenson, Stirpe, Sweeney, Thiele, Titone, Titus, Weinstein, Weisenberg, Weprin
2013A00038 1                    00000 Lab. min wage                     00000
2013A00038 6Wright
2013A00038 7Silver, Farrell, Hooper, Rivera, Peoples-Stokes, Jacobs, Markey, Gibson, Miller, Abinanti
2013A00038 8Abbate, Arroyo, Aubry, Barron, Benedetto, Boyland, Braunstein, Brennan, Bronson, Brook-Krasny,
2013A00038 8Buchwald, Cahill, Camara, Castro, Clark, Colton, Cook, Crespo, Cymbrowitz, DenDekker, Dinowitz,
2013A00038 8Englebright, Espinal, Fahy, Galef, Gantt, Glick, Gottfried, Heastie, Hikind, Jaffee, Kavanagh,
2013A00038 8Kim, Lavine, Lentol, Lifton, Lopez V, Magnarelli, Maisel, McDonald, Millman, Mosley, Moya, Nolan,
2013A00038 8Ortiz, Otis, Paulin, Perry, Pretlow, Ramos, Roberts, Robinson, Rodriguez, Rosa, Rosenthal, Rozic,
2013A00038 8Russell, Ryan, Santabarbara, Scarborough, Schimel, Sepulveda, Simanowitz, Simotas, Skoufis,
2013A00038 8Solages, Steck, Stevenson, Stirpe, Sweeney, Thiele, Titone, Titus, Weinstein, Weisenberg, Weprin',
manual_fix = true,
manual_fix_notes = 'Replaced Stripe -> Stirpe (2) occurences'
WHERE fragment_id = 'SOBI.D121206.T165949.TXT-0-BILL';

UPDATE master.sobi_fragment
SET text = '2013K00606 1                    00000 Daughters of Africa-visit         00000
2013K00606 6Titone
2013K00606 7
2013K00606 8Borelli, Cusick, Malliotakis
2013K00607 1                    00000 Onondaga ComCollegeMen''sLacrosse  00000
2013K00607 6Roberts
2013K00607 7
2013K00607 8Arroyo, Barclay, Blankenbush, Cusick, DenDekker, Fitzpatrick, Glick, Oaks, Raia, Scarborough,
2013K00607 8Weprin
2013S04101A1KLEIN               00000 ABC. underage purchs: incrs penlt 00000
2013S04101A2Alcoholic Beverage Control Law
2013S04101A3Increases the community service requirements for underage purchase of alcoholic beverages
2013S04101A403/08/13 REFERRED TO ALCOHOLISM AND DRUG ABUSE
2013S04101A404/23/13 1ST REPORT CAL.401
2013S04101A404/24/13 2ND REPORT CAL.
2013S04101A404/29/13 ADVANCED TO THIRD READING
2013S04101A405/07/13 PASSED SENATE
2013S04101A405/07/13 DELIVERED TO ASSEMBLY
2013S04101A405/07/13 referred to economic development
2013S04101A406/12/13 RECALLED FROM ASSEMBLY
2013S04101A406/12/13 returned to senate
2013K00608 1                    00000 Long Term Care Employees of Disti 00000
2013K00608 6Gottfried
2013K00608 7Abinanti, Gantt, Kim, Lupardo, Mayer, McDonald, Oaks, Quart, Schimel
2013K00608 8
2013K00608 1                    00000 Long Term Care Employees of Disti 00000
2013K00608 6Gottfried
2013K00608 7Abinanti, Gantt, Kim, Lupardo, Mayer, McDonald, Oaks, Quart, Schimel, Skoufis
2013K00608 8',
manual_fix = true,
manual_fix_notes = 'removed bad data'
WHERE fragment_id = 'SOBI.D130612.T101644.TXT-0-BILL';