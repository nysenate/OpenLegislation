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

UPDATE master.sobi_fragment
SET text = '
2013A07638 1                    00000                                   00000              0000
2013A07638 BAmd §185, Gen Bus L
2013A07638 CRelates to fees charged by employment agencies for class 3A-14 employment; provides that
2013A07638 Cemployment agencies shall not charge fees for class "A" and "A-1" employment and that certain
2013A07638 Cfees charged shall be refunded.
2013A07633 1                    00000                                   00000              0000
2013A07633 BAdd §711-a, Exec L
2013A07633 CRequires the division of homeland security and emergency services to provide recommendations on
2013A07633 Cthe implementation of tornado warning systems in the state; installation of tornado sirens; use
2013A07633 Cof firehouse sirens; other technology available for notification of impeding tornado.
2013A07636 1                    00000                                  S03912A             2013
2013A07631 1                    00000                                   00000              0000
2013A07631 BAmd §§85-a, 85-b & 85-c, Civ Rts L
2013A07631 CProvides additional credits allowed to the children of police, firefighters, emergency medical
2013A07631 Ctechnicians and paramedics having died in the performance of duty as the natural and proximate
2013A07631 Cresult of the World Trade Center attack on September eleventh, two thousand one or as the natural
2013A07631 Cand proximate result of participation in the rescue effort that was conducted in response to such
2013A07631 Cattack.
2013A07636 1                    00000                                  S03912A             2013
2013S05585 1                    00000                                   00000              0000
2013S05585 BAmd §§85-a, 85-b & 85-c, Civ Rts L
2013S05585 CProvides additional credits allowed to the children of police, firefighters, emergency medical
2013S05585 Ctechnicians and paramedics having died in the performance of duty as the natural and proximate
2013S05585 Cresult of the World Trade Center attack on September eleventh, two thousand one or as the natural
2013S05585 Cand proximate result of participation in the rescue effort that was conducted in response to such
2013S05585 Cattack.
2013S03137A1KRUEGER             00000 Pub Heal. perinatal depression    00000
2013S03137A2Public Health Law
2013S03137A3Defines perinatal depression, requires the provision of perinatal depression education, and the
2013S03137A3provision of a screening and data reporting plan for the state
2013S03137A401/30/13 REFERRED TO HEALTH
2013S03137A405/29/13 AMEND (T) AND RECOMMIT TO HEALTH
2013S03137A405/29/13 PRINT NUMBER 3137A
2013A07636 BDELETE
2013A07636 1                    00000                                  S07773              2012
2013A07636 BAmd §4403-f, Pub Health L
2013A07636 CDirects the department of health to provide oversight of the transitioning of individuals to
2013A07636 Cmanaged long term care plans operated by health maintenance organizations.
2013A07636 1                    00000                                   00000              0000
2013A07636 BAmd §4403-f, Pub Health L
2013A07636 CDirects the department of health to provide oversight of the transitioning of individuals to
2013A07636 Cmanaged long term care plans operated by health maintenance organizations.
2013J01708 5DELETE
2013J01708 ADELETE
2013J01708 5DELETE
2013J01708 ALEGISLATIVE RESOLUTION congratulating the Saratoga Rowing Association''s Girls Eight upon the
2013J01708 Aoccasion of capturing a gold medal in the Australian World Open Rowing Championships
2013A07641 1                    00000                                   00000              0000
2013A07641 BAdd §231-a, RP L
2013A07641 CRelates to sprinkler system notice in residential leases.',
manual_fix = true,
manual_fix_notes = 'removed bad data'
WHERE fragment_id = 'SOBI.D130529.T110958.TXT-0-BILL';

UPDATE master.sobi_fragment
SET text = '2014A02189 O00000.SO DOC VETO0485                                 VETO                 2014
2014A02189 O00001
2014A02189 O00002                         VETO MESSAGE - No. 485
2014A02189 O00003
2014A02189 O00004TO THE ASSEMBLY:
2014A02189 O00005
2014A02189 O00006I am returning herewith, without my approval, the following bill:
2014A02189 O00007
2014A02189 O00008Assembly Bill Number 2189-A, entitled:
2014A02189 O00009
2014A02189 O00010    "AN  ACT  to  amend  the public health law, in relation to providing
2014A02189 O00011      certain benefits to veterans; and to amend the correction law,  in
2014A02189 O00012      relation to requiring certain reports relating to veterans"
2014A02189 O00013
2014A02189 O00014    NOT APPROVED
2014A02189 O00015
2014A02189 O00016  This  bill  would,  among  other  things,  direct  the  Department  of
2014A02189 O00017Corrections and Community Supervision (DOCCS) to annually report specif-
2014A02189 O00018ic data on inmates in state and local correctional facilities  who  have
2014A02189 O00019served in the Armed Forces.
2014A02189 O00020
2014A02189 O00021  Currently,  DOCCS  works  closely  with  the federal Veterans Adminis-
2014A02189 O00022tration to verify whether any one of its inmates or parolees is a veter-
2014A02189 O00023an.
2014A02189 O00024
2014A02189 O00025  This bill would also require DOCCS to report data on inmates  held  in
2014A02189 O00026local  jails,  a  population over which DOCCS has no jurisdiction. DOCCS
2014A02189 O00027has no mechanism for obtaining this information, and does not  have  the
2014A02189 O00028authority  to  require  localities  to  provide  it.  Further,  the bill
2014A02189 O00029provides neither a mechanism nor funding for local jails to provide such
2014A02189 O00030data to DOCCS. Given the general transient  nature  of  the  jail  popu-
2014A02189 O00031lation,  the bill''s annual data snapshot also would not provide substan-
2014A02189 O00032tive data.  For these reaons, I cannot approve this bill.
2014A02189 O00033
2014A02189 O00034  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A02189 O00035                              __________
2014A02189 O00000.SO DOC VETO0485        *END*    A2189           VETO                 2014
2014S00847 O00000.SO DOC VETO0512                                 VETO                 2014
2014S00847 O00001
2014S00847 O00002                         VETO MESSAGE - No. 512
2014S00847 O00003
2014S00847 O00004TO THE SENATE:
2014S00847 O00005
2014S00847 O00006I am returning herewith, without my approval, the following bill:
2014S00847 O00007
2014S00847 O00008Senate Bill Number 847, entitled:
2014S00847 O00009
2014S00847 O00010    "AN ACT to amend the economic development law, in relation to creat-
2014S00847 O00011      ing the Empire State baseball trails program"
2014S00847 O00012
2014S00847 O00013    NOT APPROVED
2014S00847 O00014
2014S00847 O00015  This  bill  would  establish  an  Empire State Baseball Trails Program
2014S00847 O00016("Program") which is intended to promote professional  minor  and  inde-
2014S00847 O00017pendent  league  teams  in  New  York as tourist attractions. The Empire
2014S00847 O00018State Development Corporation (ESDC) recognizes the benefits of  promot-
2014S00847 O00019ing  baseball  tourism  in  the State and, during my Administration, has
2014S00847 O00020taken significant measures to accomplish the same  goals  envisioned  in
2014S00847 O00021this bill. I am constrained to veto this bill because no funds have been
2014S00847 O00022appropriated  to  implement  the  specific  costs  associated  with  the
2014S00847 O00023program, which otherwise duplicates efforts undertaken by ESDC.
2014S00847 O00024
2014S00847 O00025  The development and funding of new tourism  initiatives  is  a  matter
2014S00847 O00026that ought to be taken up in the context of the State Budget process.
2014S00847 O00027
2014S00847 O00028  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S00847 O00029                              __________
2014S00847 O00000.SO DOC VETO0512        *END*    S847            VETO                 2014
2014S02049 O00000.SO DOC VETO0513                                 VETO                 2014
2014S02049 O00001
2014S02049 O00002                         VETO MESSAGE - No. 513
2014S02049 O00003
2014S02049 O00004TO THE SENATE:
2014S02049 O00005
2014S02049 O00006I am returning herewith, without my approval, the following bill:
2014S02049 O00007
2014S02049 O00008Senate Bill Number 2049-B, entitled:
2014S02049 O00009
2014S02049 O00010    "AN ACT to amend the agriculture and markets law, in relation to the
2014S02049 O00011      delivery of liquefied petroleum gas"
2014S02049 O00012
2014S02049 O00013    NOT APPROVED
2014S02049 O00014
2014S02049 O00015  The  bill  would  prohibit any dealer of liquefied petroleum gas (LPG)
2014S02049 O00016from selling LPG to fill a fuel tank with a capacity of more than twenty
2014S02049 O00017gallons, unless the tank owner authorizes the sale in writing. I did not
2014S02049 O00018approve a nearly identical bill in 2012 (Veto No.  136),  which  covered
2014S02049 O00019LPG tanks with a capacity of more than ten gallons.
2014S02049 O00020
2014S02049 O00021  In many instances, the owner of an LPG tank is another LPG dealer; the
2014S02049 O00022unintended consequence of this bill would be to limit consumers'' ability
2014S02049 O00023to choose from whom they can purchase LPG. Moreover, the bill will shift
2014S02049 O00024the  cost of enforcing the private contract rights of LPG tank owners to
2014S02049 O00025the taxpayers of New York.
2014S02049 O00026
2014S02049 O00027  Therefore, in order to protect consumer choice for the many  users  of
2014S02049 O00028propane  tanks,  including  homeowners,  businesses  and  farmers,  I am
2014S02049 O00029compelled to veto this bill.
2014S02049 O00030
2014S02049 O00031  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S02049 O00032                              __________
2014S02049 O00000.SO DOC VETO0513        *END*    S2049           VETO                 2014
2014A02350 O00000.SO DOC VETO0486                                 VETO                 2014
2014A02350 O00001
2014A02350 O00002                         VETO MESSAGE - No. 486
2014A02350 O00003
2014A02350 O00004TO THE ASSEMBLY:
2014A02350 O00005
2014A02350 O00006I am returning herewith, without my approval, the following bill:
2014A02350 O00007
2014A02350 O00008Assembly Bill Number 2350-A, entitled:
2014A02350 O00009
2014A02350 O00010    "AN  ACT to amend the environmental conservation law, in relation to
2014A02350 O00011      requiring the department of environmental  conservation  to  issue
2014A02350 O00012      documentation of hunter safety course completion"
2014A02350 O00013
2014A02350 O00014    NOT APPROVED
2014A02350 O00015
2014A02350 O00016  This bill would require the State to issue a replacement hunter educa-
2014A02350 O00017tion  certificate to a holder of a fishing, hunting, or trapping license
2014A02350 O00018without such holder submitting any proof that he or she  had  undertaken
2014A02350 O00019and completed an education program.
2014A02350 O00020
2014A02350 O00021  Issuing  a  replacement  certificate  certifying that an indivdual has
2014A02350 O00022completed  a  hunter  education  course  absent  proof  the   individual
2014A02350 O00023completed such course would jeopardize the integrity of New York''s hunt-
2014A02350 O00024er  education  program.  For this reason, I am not approving this legis-
2014A02350 O00025lation.
2014A02350 O00026
2014A02350 O00027  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A02350 O00028                              __________
2014A02350 O00000.SO DOC VETO0486        *END*    A2350           VETO                 2014
2014S02838 O00000.SO DOC VETO0514                                 VETO                 2014
2014S02838 O00001
2014S02838 O00002                         VETO MESSAGE - No. 514
2014S02838 O00003
2014S02838 O00004TO THE SENATE:
2014S02838 O00005
2014S02838 O00006I am returning herewith, without my approval, the following bill:
2014S02838 O00007
2014S02838 O00008Senate Bill Number 2838-B, entitled:
2014S02838 O00009
2014S02838 O00010    "AN  ACT  in  relation to requiring a study and report on methods to
2014S02838 O00011      modernize  information  collection,  retention  and  dissemination
2014S02838 O00012      practices  in  the  state;  and  providing  for the repeal of such
2014S02838 O00013      provisions upon expiration thereof"
2014S02838 O00014
2014S02838 O00015    NOT APPROVED
2014S02838 O00016
2014S02838 O00017  This bill would require the  State''s  Chief  Information  Office,  the
2014S02838 O00018Director of the Budget, the Director of State Operations, and the Secre-
2014S02838 O00019tary  of State, in consultation with the State Comptroller, to study and
2014S02838 O00020issue a comprehensive report on methods  to  modernize  the  information
2014S02838 O00021collection,  retention,  and  dissemination practices of agencies in the
2014S02838 O00022state.
2014S02838 O00023
2014S02838 O00024  On January 5, 2011, I issued Executive Order No. 4,  establishing  the
2014S02838 O00025SAGE  Commission.  In  February, 2013, this commission produced a report
2014S02838 O00026recommending new programs and policies to improve efficiency and  reduce
2014S02838 O00027waste.  The  State  has  since nearly completed the nation''s largest and
2014S02838 O00028most comprehensive transformation  of  information  technology  systems,
2014S02838 O00029modernized  the State''s information technology resources, reduced dupli-
2014S02838 O00030cative functions among agencies, and is eliminating paper  transactions,
2014S02838 O00031especially through implementation of e-licensing.  Furthermore, in 2013,
2014S02838 O00032I  issued Executive Order No. 95 which created a centralized program for
2014S02838 O00033the collection and  dissemination  of  State,  local  and  federal  data
2014S02838 O00034through  OpenNY,  a website that provides user-friendly, one-stop access
2014S02838 O00035to the information.
2014S02838 O00036
2014S02838 O00037  As such, the bill''s proposals duplicate efforts already  underway  and
2014S02838 O00038programs already in existence. Therefore, I must disapprove this bill.
2014S02838 O00039
2014S02838 O00040  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S02838 O00041                              __________
2014S02838 O00000.SO DOC VETO0514        *END*    S2838           VETO                 2014
2014A03765 O00000.SO DOC VETO0487                                 VETO                 2014
2014A03765 O00001
2014A03765 O00002                         VETO MESSAGE - No. 487
2014A03765 O00003
2014A03765 O00004TO THE ASSEMBLY:
2014A03765 O00005
2014A03765 O00006I am returning herewith, without my approval, the following bills:
2014A03765 O00007
2014A03765 O00008Assembly Bill Number 3765-A, entitled:
2014A03765 O00009
2014A03765 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014A03765 O00011      services in the courts"
2014A03765 O00012
2014A03765 O00013Assembly Bill Number 7080, entitled:
2014A03765 O00014
2014A03765 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014A03765 O00016      nating uniformed officers of the fire marshal''s office of the town
2014A03765 O00017      of Huntington as peace officers"
2014A03765 O00018
2014A03765 O00019Assembly Bill Number 9330, entitled:
2014A03765 O00020
2014A03765 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A03765 O00022      nating  uniformed court attendants in the town of Ossining, county
2014A03765 O00023      of Westchester as peace officers"
2014A03765 O00024
2014A03765 O00025Assembly Bill Number 9843, entitled:
2014A03765 O00026
2014A03765 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A03765 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014A03765 O00029      peace officers"
2014A03765 O00030
2014A03765 O00031TO THE SENATE:
2014A03765 O00032
2014A03765 O00033I am returning herewith, without my approval, the following bills:
2014A03765 O00034
2014A03765 O00035Senate Bill Number 3894-B, entitled:
2014A03765 O00036
2014A03765 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014A03765 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014A03765 O00039      of Chautauqua county within the grounds of and properties owned by
2014A03765 O00040      the Chautauqua Institution"
2014A03765 O00041
2014A03765 O00042Senate Bill Number 7470, entitled:
2014A03765 O00043
2014A03765 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014A03765 O00045      uniformed  members of the bureau of fire prevention of the town of
2014A03765 O00046      Islip peace officer status"
2014A03765 O00047
2014A03765 O00048Senate Bill Number 7786, entitled:
2014A03765 O00049
2014A03765 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A03765 O00051      nating  uniformed court officers of the town of Highlands as peace
2014A03765 O00052      officers"
2014A03765 O00053
2014A03765 O00054    NOT APPROVED
2014A03765 O00055
2014A03765 O00056  These seven bills would grant peace officer status to fire marshals or
2014A03765 O00057fire prevention service members in two counties, uniformed  court  offi-
2014A03765 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014A03765 O00059in one county. If designated as peace officers,  these  officials  would
2014A03765 O00060
2014A03765 O00061have many of the same legal powers as police officers. These include the
2014A03765 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014A03765 O00063warrantless searches and issue appearance tickets.
2014A03765 O00064
2014A03765 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014A03765 O00066mending that the Legislature create a comprehensive process  for  deter-
2014A03765 O00067mining which categories of officials, on a statewide basis, may need the
2014A03765 O00068police  powers granted to peace officers. I again ask the Legislature to
2014A03765 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014A03765 O00070within  the  broader  context of New York State''s law enforcement needs,
2014A03765 O00071rather than addressing the needs of local government units in an ad  hoc
2014A03765 O00072manner. For these reasons, I will not approve these bills.
2014A03765 O00073
2014A03765 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A03765 O00075                              __________
2014A03765 O00000.SO DOC VETO0487        *END*    A3765           VETO                 2014
2014S03894 O00000.SO DOC VETO0515                                 VETO                 2014
2014S03894 O00001
2014S03894 O00002                         VETO MESSAGE - No. 515
2014S03894 O00003
2014S03894 O00004TO THE ASSEMBLY:
2014S03894 O00005
2014S03894 O00006I am returning herewith, without my approval, the following bills:
2014S03894 O00007
2014S03894 O00008Assembly Bill Number 3765-A, entitled:
2014S03894 O00009
2014S03894 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014S03894 O00011      services in the courts"
2014S03894 O00012
2014S03894 O00013Assembly Bill Number 7080, entitled:
2014S03894 O00014
2014S03894 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014S03894 O00016      nating uniformed officers of the fire marshal''s office of the town
2014S03894 O00017      of Huntington as peace officers"
2014S03894 O00018
2014S03894 O00019Assembly Bill Number 9330, entitled:
2014S03894 O00020
2014S03894 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S03894 O00022      nating  uniformed court attendants in the town of Ossining, county
2014S03894 O00023      of Westchester as peace officers"
2014S03894 O00024
2014S03894 O00025Assembly Bill Number 9843, entitled:
2014S03894 O00026
2014S03894 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S03894 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014S03894 O00029      peace officers"
2014S03894 O00030
2014S03894 O00031TO THE SENATE:
2014S03894 O00032
2014S03894 O00033I am returning herewith, without my approval, the following bills:
2014S03894 O00034
2014S03894 O00035Senate Bill Number 3894-B, entitled:
2014S03894 O00036
2014S03894 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014S03894 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014S03894 O00039      of Chautauqua county within the grounds of and properties owned by
2014S03894 O00040      the Chautauqua Institution"
2014S03894 O00041
2014S03894 O00042Senate Bill Number 7470, entitled:
2014S03894 O00043
2014S03894 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014S03894 O00045      uniformed  members of the bureau of fire prevention of the town of
2014S03894 O00046      Islip peace officer status"
2014S03894 O00047
2014S03894 O00048Senate Bill Number 7786, entitled:
2014S03894 O00049
2014S03894 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S03894 O00051      nating  uniformed court officers of the town of Highlands as peace
2014S03894 O00052      officers"
2014S03894 O00053
2014S03894 O00054    NOT APPROVED
2014S03894 O00055
2014S03894 O00056  These seven bills would grant peace officer status to fire marshals or
2014S03894 O00057fire prevention service members in two counties, uniformed  court  offi-
2014S03894 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014S03894 O00059in one county. If designated as peace officers,  these  officials  would
2014S03894 O00060
2014S03894 O00061have many of the same legal powers as police officers. These include the
2014S03894 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014S03894 O00063warrantless searches and issue appearance tickets.
2014S03894 O00064
2014S03894 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014S03894 O00066mending that the Legislature create a comprehensive process  for  deter-
2014S03894 O00067mining which categories of officials, on a statewide basis, may need the
2014S03894 O00068police  powers granted to peace officers. I again ask the Legislature to
2014S03894 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014S03894 O00070within  the  broader  context of New York State''s law enforcement needs,
2014S03894 O00071rather than addressing the needs of local government units in an ad  hoc
2014S03894 O00072manner. For these reasons, I will not approve these bills.
2014S03894 O00073
2014S03894 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014S03894 O00075                              __________
2014S03894 O00000.SO DOC VETO0515        *END*    S3894           VETO                 2014
2014S04095 O00000.SO DOC VETO0516                                 VETO                 2014
2014S04095 O00001
2014S04095 O00002                         VETO MESSAGE - No. 516
2014S04095 O00003
2014S04095 O00004TO THE SENATE:
2014S04095 O00005
2014S04095 O00006I am returning herewith, without my approval, the following bill:
2014S04095 O00007
2014S04095 O00008Senate Bill Number 4095, entitled:
2014S04095 O00009
2014S04095 O00010    "AN  ACT to amend the state finance law, in relation to compensation
2014S04095 O00011      and medical expenses of certain injured state employees"
2014S04095 O00012
2014S04095 O00013    NOT APPROVED
2014S04095 O00014
2014S04095 O00015  This bill would require that any  parole  officer,  parole  revocation
2014S04095 O00016specialist,  or  warrant officer who is injured or taken sick in any way
2014S04095 O00017during the performance of his or her duties, be paid by  the  State  the
2014S04095 O00018full  amount  of  his or her salary until the disability has ceased. The
2014S04095 O00019State would also be liable for all hospital care  necessitated  by  such
2014S04095 O00020injury or illness.
2014S04095 O00021
2014S04095 O00022  The  Division  of  the  Budget  estimates that this bill will cost the
2014S04095 O00023State nearly $4 million in increased workers'' compensation and  overtime
2014S04095 O00024payments.  Because  of  this  fiscal impact. I am compelled to veto this
2014S04095 O00025bill.
2014S04095 O00026
2014S04095 O00027  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S04095 O00028                              __________
2014S04095 O00000.SO DOC VETO0516        *END*    S4095           VETO                 2014
2014A05465 O00000.SO DOC VETO0488                                 VETO                 2014
2014A05465 O00001
2014A05465 O00002                         VETO MESSAGE - No. 488
2014A05465 O00003
2014A05465 O00004TO THE ASSEMBLY:
2014A05465 O00005
2014A05465 O00006I am returning herewith, without my approval, the following bill:
2014A05465 O00007
2014A05465 O00008Assembly Bill Number 5465-A, entitled:
2014A05465 O00009
2014A05465 O00010    "AN  ACT to amend the environmental conservation law, in relation to
2014A05465 O00011      implementing a demonstration drug disposal program; and  providing
2014A05465 O00012      for the repeal of such provisions upon the expiration thereof"
2014A05465 O00013
2014A05465 O00014    NOT APPROVED
2014A05465 O00015
2014A05465 O00016  This  bill  would  direct the Department of Environmental Conservation
2014A05465 O00017(DEC), in consultation with the New York State Police (NYSP), to  estab-
2014A05465 O00018lish a demonstration drug disposal program in at least three NYSP facil-
2014A05465 O00019ities.
2014A05465 O00020
2014A05465 O00021  The  proper disposal of expired, unused, or unwanted drugs and pharma-
2014A05465 O00022ceutical products is a critical environmental issue.  However, this bill
2014A05465 O00023would duplicate ongoing drug collection and disposal efforts by DEC  and
2014A05465 O00024NYSP, including the management of disposal sites in operation at several
2014A05465 O00025NYSP locations. For this reason, I am compelled to disapprove this bill.
2014A05465 O00026
2014A05465 O00027  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A05465 O00028                              __________
2014A05465 O00000.SO DOC VETO0488        *END*    A5465           VETO                 2014
2014S06124 O00000.SO DOC VETO0517                                 VETO                 2014
2014S06124 O00001
2014S06124 O00002                         VETO MESSAGE - No. 517
2014S06124 O00003
2014S06124 O00004TO THE SENATE:
2014S06124 O00005
2014S06124 O00006I am returning herewith, without my approval, the following bill:
2014S06124 O00007
2014S06124 O00008Senate Bill Number 6124-A, entitled:
2014S06124 O00009
2014S06124 O00010    "AN  ACT  in  relation  to  legalizing,  validating,  ratifying  and
2014S06124 O00011      confirming a transportation contract of the Perry  central  school
2014S06124 O00012      district"
2014S06124 O00013
2014S06124 O00014TO THE ASSEMBLY:
2014S06124 O00015
2014S06124 O00016I am returning herewith, without my approval, the following bill:
2014S06124 O00017
2014S06124 O00018Assembly Bill Number 9977-A, entitled:
2014S06124 O00019
2014S06124 O00020    "AN  ACT  to  provide  for the repayment by the Johnson City central
2014S06124 O00021      school district of certain excess state payments"
2014S06124 O00022
2014S06124 O00023    NOT APPROVED
2014S06124 O00024
2014S06124 O00025  Each of these bills would authorize, outside the State Budget process,
2014S06124 O00026payment of State education aid above the amounts calculated  and  previ-
2014S06124 O00027ously agreed to under current law.
2014S06124 O00028
2014S06124 O00029  Assembly  Bill  Number  9977-A  would  allow  the Johnson City Central
2014S06124 O00030School District to repay a $1.99 million Building Aid  overpayment  over
2014S06124 O00031six years rather than over three years. As a result, the school district
2014S06124 O00032would receive $1.1 million of additional aid for the 2014-15 school year
2014S06124 O00033above  the  aid  amount  that  is currently authorized by law. In recent
2014S06124 O00034years, similar provisions have only been authorized in  the  context  of
2014S06124 O00035enacting the State budget.
2014S06124 O00036
2014S06124 O00037  Senate  Bill  Number  6124-A  would  validate  a  Perry Central School
2014S06124 O00038District transportation contract, even though  the  district  failed  to
2014S06124 O00039meet  long-standing statutory requirements for aid eligibility. Further,
2014S06124 O00040this transportation contract falls  outside  the  scope  of  forgiveness
2014S06124 O00041provisions that were negotiated by the Legislature and the Executive and
2014S06124 O00042included in the 2012-13 Enacted Budget, thereby undoing that agreed upon
2014S06124 O00043solution.
2014S06124 O00044
2014S06124 O00045  Each  of  these  bills  would result in increased and unbudgeted State
2014S06124 O00046costs. For the reasons stated above, I cannot approve these bills.
2014S06124 O00047
2014S06124 O00048  These bill are disapproved.                 (signed) ANDREW M. CUOMO
2014S06124 O00049                              __________
2014S06124 O00000.SO DOC VETO0517        *END*    S6124           VETO                 2014
2014A05906 O00000.SO DOC VETO0489                                 VETO                 2014
2014A05906 O00001
2014A05906 O00002                         VETO MESSAGE - No. 489
2014A05906 O00003
2014A05906 O00004TO THE ASSEMBLY:
2014A05906 O00005
2014A05906 O00006I am returning herewith, without my approval, the following bill:
2014A05906 O00007
2014A05906 O00008Assembly Bill Number 5906, entitled:
2014A05906 O00009
2014A05906 O00010    "AN  ACT to amend the environmental conservation law, in relation to
2014A05906 O00011      the periodic preparation of a state deer management  plan  by  the
2014A05906 O00012      department of environmental conservation"
2014A05906 O00013
2014A05906 O00014    NOT APPROVED
2014A05906 O00015
2014A05906 O00016  This  bill  would require the Department of Environmental Conservation
2014A05906 O00017(DEC) to prepare a state deer  management  plan  for  white-tailed  deer
2014A05906 O00018every five years.
2014A05906 O00019
2014A05906 O00020  DEC  adopted  such a Management Plan for white-tailed deer in New York
2014A05906 O00021State in 2011 that establishes goals for deer management  and  evaluates
2014A05906 O00022methods for controlling abundant deer populations, including hunting and
2014A05906 O00023fertility  control. The plan was developed after several years of public
2014A05906 O00024outreach, which included hunter surveys. DEC will update the plan as  it
2014A05906 O00025deems  necessary  to  address  changing circumstances. Thus, this legis-
2014A05906 O00026lation is unnecessary, and I disapprove it.
2014A05906 O00027
2014A05906 O00028  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A05906 O00029                              __________
2014A05906 O00000.SO DOC VETO0489        *END*    A5906           VETO                 2014
2014S06482 O00000.SO DOC VETO0518                                 VETO                 2014
2014S06482 O00001
2014S06482 O00002                         VETO MESSAGE - No. 518
2014S06482 O00003
2014S06482 O00004TO THE SENATE:
2014S06482 O00005
2014S06482 O00006I am returning herewith, without my approval, the following bill:
2014S06482 O00007
2014S06482 O00008Senate Bill Number 6482, entitled:
2014S06482 O00009
2014S06482 O00010    "AN  ACT  to amend the state finance law, in relation to the payment
2014S06482 O00011      of interest due to not-for-profit corporations and public  benefit
2014S06482 O00012      corporations"
2014S06482 O00013
2014S06482 O00014    NOT APPROVED
2014S06482 O00015
2014S06482 O00016  Nonprofit  service  providers  make  an  essential contribution to New
2014S06482 O00017Yorkers, and I agree with the sponsors of this bill that it is important
2014S06482 O00018to reduce delayed interest payments on late contracts to  these  service
2014S06482 O00019providers.  My Administration has instituted sensible reforms to stream-
2014S06482 O00020line the contracting process and  eliminate  unnecessary  paperwork  and
2014S06482 O00021burdensome procedures. Contract processing speeds are increasing so that
2014S06482 O00022on-time  contracts will be the norm, not the exception, and the need for
2014S06482 O00023the  payment  of  prompt  contracting  interest  will  be  significantly
2014S06482 O00024reduced. This legislation, however, unnecessarily diverts resources that
2014S06482 O00025are  now  being  better applied to solving the problem of late contracts
2014S06482 O00026and eliminating the need for prompt contracting interest payments in the
2014S06482 O00027first place.
2014S06482 O00028
2014S06482 O00029  For these reasons, I vetoed nearly  identical  legislation  last  year
2014S06482 O00030(Veto  No. 235) and the year before (Veto No. 188), and am not approving
2014S06482 O00031this bill.
2014S06482 O00032
2014S06482 O00033  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S06482 O00034                              __________
2014S06482 O00000.SO DOC VETO0518        *END*    S6482           VETO                 2014
2014S06641 O00000.SO DOC VETO0519                                 VETO                 2014
2014S06641 O00001
2014S06641 O00002                         VETO MESSAGE - No. 519
2014S06641 O00003
2014S06641 O00004TO THE SENATE:
2014S06641 O00005
2014S06641 O00006I am returning herewith, without my approval, the following bill:
2014S06641 O00007
2014S06641 O00008Senate Bill Number 6641-C, entitled:
2014S06641 O00009
2014S06641 O00010    "AN  ACT  relating  to the administration of services to people with
2014S06641 O00011      developmental disabilities"
2014S06641 O00012
2014S06641 O00013    NOT APPROVED
2014S06641 O00014
2014S06641 O00015  In May 2013, the Office for Persons  with  Developmental  Disabilities
2014S06641 O00016(OPWDD)  announced its "Front Door" initiative to reform how individuals
2014S06641 O00017with developmental disabilities access services in  order  to  create  a
2014S06641 O00018consistent  statewide  approach  to  accessing, continuing, or modifying
2014S06641 O00019developmental disabilities services. The bill would  require  OPWDD,  in
2014S06641 O00020collabortion  with  its  Developmental Disabilities Advisory Council, to
2014S06641 O00021develop a plan for implementing the Front Door  process  and  to  submit
2014S06641 O00022this plan to the Legislature by January 1, 2015. Implementing guidelines
2014S06641 O00023for the plan would need to be in effect by April 1, 2015.
2014S06641 O00024
2014S06641 O00025  OPWDD  continues to streamline and improve the Front Door process, but
2014S06641 O00026this legislation in many respects duplicates what  is  currently  taking
2014S06641 O00027place.  The bill would unnecessarily divert valuable staff and financial
2014S06641 O00028resources from the effort and would also  impose  additional  unbudgeted
2014S06641 O00029costs.  As  a  result,  this  bill should have been considered in budget
2014S06641 O00030negotiations. Therefore, I cannot approve the bill.
2014S06641 O00031
2014S06641 O00032  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S06641 O00033                              __________
2014S06641 O00000.SO DOC VETO0519        *END*    S6641           VETO                 2014
2014A07018 O00000.SO DOC VETO0490                                 VETO                 2014
2014A07018 O00001
2014A07018 O00002                         VETO MESSAGE - No. 490
2014A07018 O00003
2014A07018 O00004TO THE ASSEMBLY:
2014A07018 O00005
2014A07018 O00006I am returning herewith, without my approval, the following bill:
2014A07018 O00007
2014A07018 O00008Assembly Bill Number 7018, entitled:
2014A07018 O00009
2014A07018 O00010    "AN ACT to amend the town law, in relation to the employer''s ability
2014A07018 O00011      to  suspend  a  police  officer  without  pay pending disciplinary
2014A07018 O00012      charges"
2014A07018 O00013
2014A07018 O00014    NOT APPROVED
2014A07018 O00015
2014A07018 O00016  This bill would limit a town''s ability to  suspend  a  police  officer
2014A07018 O00017charged with an offense, regardless of its seriousness, for more than 30
2014A07018 O00018days without pay while disciplinary charges are pending.
2014A07018 O00019
2014A07018 O00020  I  vetoed  an  identical  bill  in 2012 because it would constrain the
2014A07018 O00021ability of a town police department to effectively manage its  workforce
2014A07018 O00022when allegations of misconduct arise. For this same reason, I disapprove
2014A07018 O00023this bill.
2014A07018 O00024
2014A07018 O00025  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A07018 O00026                              __________
2014A07018 O00000.SO DOC VETO0490        *END*    A7018           VETO                 2014
2014S07000 O00000.SO DOC VETO0520                                 VETO                 2014
2014S07000 O00001
2014S07000 O00002                         VETO MESSAGE - No. 520
2014S07000 O00003
2014S07000 O00004TO THE SENATE:
2014S07000 O00005
2014S07000 O00006I am returning herewith, without my approval, the following bill:
2014S07000 O00007
2014S07000 O00008Senate Bill Number 7000, entitled:
2014S07000 O00009
2014S07000 O00010    "AN  ACT  to  amend the public health law, in relation to a physical
2014S07000 O00011      fitness and activity education campaign"
2014S07000 O00012
2014S07000 O00013    NOT APPROVED
2014S07000 O00014
2014S07000 O00015  This bill would establish a New York State Physical Fitness and Activ-
2014S07000 O00016ity Education Campaign that would utilize social and mass media  outlets
2014S07000 O00017to  increase public awareness of the importance of physical activity and
2014S07000 O00018its corresponding health benefits.
2014S07000 O00019
2014S07000 O00020  The objective of this bill is laudable and consistent with  the  goals
2014S07000 O00021of  several  existing  wellness  programs  overseen by the Department of
2014S07000 O00022Health. The additional costs necessary to effectuate the proposed legis-
2014S07000 O00023lation, however, would require the unplanned use of agency resources and
2014S07000 O00024should be discussed in the context of the upcoming State  budget.  As  a
2014S07000 O00025result,  I am disapproving this legislation. Nonetheless, I am directing
2014S07000 O00026the Department of Health to continue coordinating its  existing  efforts
2014S07000 O00027to  educate  New  Yorkers  about the benefits of physical activity in an
2014S07000 O00028integrated and cost-effective manner.
2014S07000 O00029
2014S07000 O00030  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S07000 O00031                              __________
2014S07000 O00000.SO DOC VETO0520        *END*    S7000           VETO                 2014
2014S07009 O00000.SO DOC VETO0521                                 VETO                 2014
2014S07009 O00001
2014S07009 O00002                         VETO MESSAGE - No. 521
2014S07009 O00003
2014S07009 O00004TO THE SENATE:
2014S07009 O00005
2014S07009 O00006I am returning herewith, without my approval, the following bill:
2014S07009 O00007
2014S07009 O00008Senate Bill Number 7009, entitled:
2014S07009 O00009
2014S07009 O00010    "AN  ACT to amend the general municipal law, in relation to training
2014S07009 O00011      of fire officers in cities of one million or more"
2014S07009 O00012
2014S07009 O00013    NOT APPROVED
2014S07009 O00014
2014S07009 O00015  This bill would require that fire officers in New York City receive an
2014S07009 O00016additional 80 hours of field and classroom training on the City''s  fire,
2014S07009 O00017building  and  construction  codes  as well as the applicable City ordi-
2014S07009 O00018nances.
2014S07009 O00019
2014S07009 O00020  The New York City Mayor''s Office has urged me to disapprove this  bill
2014S07009 O00021for  several  reasons,  including  the  significant fiscal impact of the
2014S07009 O00022bill.
2014S07009 O00023
2014S07009 O00024  I vetoed a similar bill in 2013 and expressed my  wish  that  the  new
2014S07009 O00025mayor  and New York City''s unions would reach an agreement on the appro-
2014S07009 O00026priate scope of training. Although it appears that  differences  between
2014S07009 O00027the parties remain, I am not prepared to dictate to the City the specif-
2014S07009 O00028ics  of  its  training  programs, particularly when such programs have a
2014S07009 O00029significant fiscal impact.
2014S07009 O00030
2014S07009 O00031  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S07009 O00032                              __________
2014S07009 O00000.SO DOC VETO0521        *END*    S7009           VETO                 2014
2014S07367 O00000.SO DOC VETO0522                                 VETO                 2014
2014S07367 O00001
2014S07367 O00002                         VETO MESSAGE - No. 522
2014S07367 O00003
2014S07367 O00004TO THE SENATE:
2014S07367 O00005
2014S07367 O00006I am returning herewith, without my approval, the following bill:
2014S07367 O00007
2014S07367 O00008Senate Bill Number 7367, entitled:
2014S07367 O00009
2014S07367 O00010    "AN ACT to amend the local finance law, in relation to the refunding
2014S07367 O00011      and advance refunding of bonds"
2014S07367 O00012
2014S07367 O00013    NOT APPROVED
2014S07367 O00014
2014S07367 O00015  This  bill  would  provide local governments with an additional option
2014S07367 O00016under which they would be authorized to refund their bonds.
2014S07367 O00017
2014S07367 O00018  While I support the Legislature''s efforts to provide local governments
2014S07367 O00019with additional flexibility in restructuring and minimizing their  debt,
2014S07367 O00020I believe that this bill is unconstitutional because the State Constitu-
2014S07367 O00021tion  explicitly  allows  only  two  options for the refunding of bonds.
2014S07367 O00022Furthermore, localities that try to refund their bonds without complying
2014S07367 O00023with current restrictions could face  steep  and  unbearable  spikes  in
2014S07367 O00024their  debt  service  in  the  future. This bill may also be unnecessary
2014S07367 O00025because, under  current  law,  local  governments  can  already  satisfy
2014S07367 O00026current  criteria  for  refunding  their  bonds without having to refund
2014S07367 O00027additional bonds.
2014S07367 O00028
2014S07367 O00029  For these reasons, I cannot approve this legislation.
2014S07367 O00030
2014S07367 O00031  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S07367 O00032                              __________
2014S07367 O00000.SO DOC VETO0522        *END*    S7367           VETO                 2014
2014A07080 O00000.SO DOC VETO0491                                 VETO                 2014
2014A07080 O00001
2014A07080 O00002                         VETO MESSAGE - No. 491
2014A07080 O00003
2014A07080 O00004TO THE ASSEMBLY:
2014A07080 O00005
2014A07080 O00006I am returning herewith, without my approval, the following bills:
2014A07080 O00007
2014A07080 O00008Assembly Bill Number 3765-A, entitled:
2014A07080 O00009
2014A07080 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014A07080 O00011      services in the courts"
2014A07080 O00012
2014A07080 O00013Assembly Bill Number 7080, entitled:
2014A07080 O00014
2014A07080 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014A07080 O00016      nating uniformed officers of the fire marshal''s office of the town
2014A07080 O00017      of Huntington as peace officers"
2014A07080 O00018
2014A07080 O00019Assembly Bill Number 9330, entitled:
2014A07080 O00020
2014A07080 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00022      nating  uniformed court attendants in the town of Ossining, county
2014A07080 O00023      of Westchester as peace officers"
2014A07080 O00024
2014A07080 O00025Assembly Bill Number 9843, entitled:
2014A07080 O00026
2014A07080 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014A07080 O00029      peace officers"
2014A07080 O00030
2014A07080 O00031TO THE SENATE:
2014A07080 O00032
2014A07080 O00033I am returning herewith, without my approval, the following bills:
2014A07080 O00034
2014A07080 O00035Senate Bill Number 3894-B, entitled:
2014A07080 O00036
2014A07080 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014A07080 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014A07080 O00039      of Chautauqua county within the grounds of and properties owned by
2014A07080 O00040      the Chautauqua Institution"
2014A07080 O00041
2014A07080 O00042Senate Bill Number 7470, entitled:
2014A07080 O00043
2014A07080 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014A07080 O00045      uniformed  members of the bureau of fire prevention of the town of
2014A07080 O00046      Islip peace officer status"
2014A07080 O00047
2014A07080 O00048Senate Bill Number 7786, entitled:
2014A07080 O00049
2014A07080 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00051      nating  uniformed court officers of the town of Highlands as peace
2014A07080 O00052      officers"
2014A07080 O00053
2014A07080 O00054    NOT APPROVED
2014A07080 O00055
2014A07080 O00056  These seven bills would grant peace officer status to fire marshals or
2014A07080 O00057fire prevention service members in two counties, uniformed  court  offi-
2014A07080 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014A07080 O00059in one county. If designated as peace officers,  these  officials  would
2014A07080 O00060
2014A07080 O00061have many of the same legal powers as police officers. These include the
2014A07080 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014A07080 O00063warrantless searches and issue appearance tickets.
2014A07080 O00064
2014A07080 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014A07080 O00066mending that the Legislature create a comprehensive process  for  deter-
2014A07080 O00067mining which categories of officials, on a statewide basis, may need the
2014A07080 O00068police  powers granted to peace officers. I again ask the Legislature to
2014A07080 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014A07080 O00070within  the  broader  context of New York State''s law enforcement needs,
2014A07080 O00071rather than addressing the needs of local government units in an ad  hoc
2014A07080 O00072manner. For these reasons, I will not approve these bills.
2014A07080 O00073
2014A07080 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A07080 O00075                              __________
2014A07080 O00000.SO DOC VETO0491        *END*    A7080           VETO                 2014
2014S07466 O00000.SO DOC VETO0523                                 VETO                 2014
2014S07466 O00001
2014S07466 O00002                         VETO MESSAGE - No. 523
2014S07466 O00003
2014S07466 O00004TO THE SENATE:
2014S07466 O00005
2014S07466 O00006I am returning herewith, without my approval, the following bill:
2014S07466 O00007
2014S07466 O00008Senate Bill Number 7466, entitled:
2014S07466 O00009
2014S07466 O00010    "AN ACT to amend the real property tax law, in relation to requiring
2014S07466 O00011      assessment  disclosure  notices  in  New  York  city  to include a
2014S07466 O00012      description of the method of assessment"
2014S07466 O00013
2014S07466 O00014    NOT APPROVED
2014S07466 O00015
2014S07466 O00016  This bill would require that New York  City''s  Department  of  Finance
2014S07466 O00017(DoF)  include  in  its assessment disclosure notice of certain property
2014S07466 O00018tax assessment data.
2014S07466 O00019
2014S07466 O00020  This bill is identical to legislation I vetoed  last  year  (Veto  No.
2014S07466 O00021243).    DoF  already  provides most of the information required by this
2014S07466 O00022legislation through several separate databases on its website and on the
2014S07466 O00023Notices of Property Value that are mailed to property owners.  Moreover,
2014S07466 O00024New York City advises that this unfunded mandate would require it to, at
2014S07466 O00025significant  cost,  recode  the  DoF''s  systems for very little benefit.
2014S07466 O00026Therefore, for the same reasons that I disapproved this legislation last
2014S07466 O00027year, I cannot approve this bill.
2014S07466 O00028
2014S07466 O00029  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014S07466 O00030                              __________
2014S07466 O00000.SO DOC VETO0523        *END*    S7466           VETO                 2014
2014S07470 O00000.SO DOC VETO0524                                 VETO                 2014
2014S07470 O00001
2014S07470 O00002                         VETO MESSAGE - No. 524
2014S07470 O00003
2014S07470 O00004TO THE ASSEMBLY:
2014S07470 O00005
2014S07470 O00006I am returning herewith, without my approval, the following bills:
2014S07470 O00007
2014S07470 O00008Assembly Bill Number 3765-A, entitled:
2014S07470 O00009
2014S07470 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014S07470 O00011      services in the courts"
2014S07470 O00012
2014S07470 O00013Assembly Bill Number 7080, entitled:
2014S07470 O00014
2014S07470 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014S07470 O00016      nating uniformed officers of the fire marshal''s office of the town
2014S07470 O00017      of Huntington as peace officers"
2014S07470 O00018
2014S07470 O00019Assembly Bill Number 9330, entitled:
2014S07470 O00020
2014S07470 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07470 O00022      nating  uniformed court attendants in the town of Ossining, county
2014S07470 O00023      of Westchester as peace officers"
2014S07470 O00024
2014S07470 O00025Assembly Bill Number 9843, entitled:
2014S07470 O00026
2014S07470 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07470 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014S07470 O00029      peace officers"
2014S07470 O00030
2014S07470 O00031TO THE SENATE:
2014S07470 O00032
2014S07470 O00033I am returning herewith, without my approval, the following bills:
2014S07470 O00034
2014S07470 O00035Senate Bill Number 3894-B, entitled:
2014S07470 O00036
2014S07470 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014S07470 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014S07470 O00039      of Chautauqua county within the grounds of and properties owned by
2014S07470 O00040      the Chautauqua Institution"
2014S07470 O00041
2014S07470 O00042Senate Bill Number 7470, entitled:
2014S07470 O00043
2014S07470 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014S07470 O00045      uniformed  members of the bureau of fire prevention of the town of
2014S07470 O00046      Islip peace officer status"
2014S07470 O00047
2014S07470 O00048Senate Bill Number 7786, entitled:
2014S07470 O00049
2014S07470 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07470 O00051      nating  uniformed court officers of the town of Highlands as peace
2014S07470 O00052      officers"
2014S07470 O00053
2014S07470 O00054    NOT APPROVED
2014S07470 O00055
2014S07470 O00056  These seven bills would grant peace officer status to fire marshals or
2014S07470 O00057fire prevention service members in two counties, uniformed  court  offi-
2014S07470 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014S07470 O00059in one county. If designated as peace officers,  these  officials  would
2014S07470 O00060
2014S07470 O00061have many of the same legal powers as police officers. These include the
2014S07470 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014S07470 O00063warrantless searches and issue appearance tickets.
2014S07470 O00064
2014S07470 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014S07470 O00066mending that the Legislature create a comprehensive process  for  deter-
2014S07470 O00067mining which categories of officials, on a statewide basis, may need the
2014S07470 O00068police  powers granted to peace officers. I again ask the Legislature to
2014S07470 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014S07470 O00070within  the  broader  context of New York State''s law enforcement needs,
2014S07470 O00071rather than addressing the needs of local government units in an ad  hoc
2014S07470 O00072manner. For these reasons, I will not approve these bills.
2014S07470 O00073
2014S07470 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014S07470 O00075                              __________
2014S07470 O00000.SO DOC VETO0524        *END*    S7470           VETO                 2014
2014S07786 O00000.SO DOC VETO0525                                 VETO                 2014
2014S07786 O00001
2014S07786 O00002                         VETO MESSAGE - No. 525
2014S07786 O00003
2014S07786 O00004TO THE ASSEMBLY:
2014S07786 O00005
2014S07786 O00006I am returning herewith, without my approval, the following bills:
2014S07786 O00007
2014S07786 O00008Assembly Bill Number 3765-A, entitled:
2014S07786 O00009
2014S07786 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014S07786 O00011      services in the courts"
2014S07786 O00012
2014S07786 O00013Assembly Bill Number 7080, entitled:
2014S07786 O00014
2014S07786 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014S07786 O00016      nating uniformed officers of the fire marshal''s office of the town
2014S07786 O00017      of Huntington as peace officers"
2014S07786 O00018
2014S07786 O00019Assembly Bill Number 9330, entitled:
2014S07786 O00020
2014S07786 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07786 O00022      nating  uniformed court attendants in the town of Ossining, county
2014S07786 O00023      of Westchester as peace officers"
2014S07786 O00024
2014S07786 O00025Assembly Bill Number 9843, entitled:
2014S07786 O00026
2014S07786 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07786 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014S07786 O00029      peace officers"
2014S07786 O00030
2014S07786 O00031TO THE SENATE:
2014S07786 O00032
2014S07786 O00033I am returning herewith, without my approval, the following bills:
2014S07786 O00034
2014S07786 O00035Senate Bill Number 3894-B, entitled:
2014S07786 O00036
2014S07786 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014S07786 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014S07786 O00039      of Chautauqua county within the grounds of and properties owned by
2014S07786 O00040      the Chautauqua Institution"
2014S07786 O00041
2014S07786 O00042Senate Bill Number 7470, entitled:
2014S07786 O00043
2014S07786 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014S07786 O00045      uniformed  members of the bureau of fire prevention of the town of
2014S07786 O00046      Islip peace officer status"
2014S07786 O00047
2014S07786 O00048Senate Bill Number 7786, entitled:
2014S07786 O00049
2014S07786 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014S07786 O00051      nating  uniformed court officers of the town of Highlands as peace
2014S07786 O00052      officers"
2014S07786 O00053
2014S07786 O00054    NOT APPROVED
2014S07786 O00055
2014S07786 O00056  These seven bills would grant peace officer status to fire marshals or
2014S07786 O00057fire prevention service members in two counties, uniformed  court  offi-
2014S07786 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014S07786 O00059in one county. If designated as peace officers,  these  officials  would
2014S07786 O00060
2014S07786 O00061have many of the same legal powers as police officers. These include the
2014S07786 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014S07786 O00063warrantless searches and issue appearance tickets.
2014S07786 O00064
2014S07786 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014S07786 O00066mending that the Legislature create a comprehensive process  for  deter-
2014S07786 O00067mining which categories of officials, on a statewide basis, may need the
2014S07786 O00068police  powers granted to peace officers. I again ask the Legislature to
2014S07786 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014S07786 O00070within  the  broader  context of New York State''s law enforcement needs,
2014S07786 O00071rather than addressing the needs of local government units in an ad  hoc
2014S07786 O00072manner. For these reasons, I will not approve these bills.
2014S07786 O00073
2014S07786 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014S07786 O00075                              __________
2014S07786 O00000.SO DOC VETO0525        *END*    S7786           VETO                 2014
2014A07080 O00000.SO DOC VETO0491                                 VETO                 2014
2014A07080 O00001
2014A07080 O00002                         VETO MESSAGE - No. 491
2014A07080 O00003
2014A07080 O00004TO THE ASSEMBLY:
2014A07080 O00005
2014A07080 O00006I am returning herewith, without my approval, the following bills:
2014A07080 O00007
2014A07080 O00008Assembly Bill Number 3765-A, entitled:
2014A07080 O00009
2014A07080 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014A07080 O00011      services in the courts"
2014A07080 O00012
2014A07080 O00013Assembly Bill Number 7080, entitled:
2014A07080 O00014
2014A07080 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014A07080 O00016      nating uniformed officers of the fire marshal''s office of the town
2014A07080 O00017      of Huntington as peace officers"
2014A07080 O00018
2014A07080 O00019Assembly Bill Number 9330, entitled:
2014A07080 O00020
2014A07080 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00022      nating  uniformed court attendants in the town of Ossining, county
2014A07080 O00023      of Westchester as peace officers"
2014A07080 O00024
2014A07080 O00025Assembly Bill Number 9843, entitled:
2014A07080 O00026
2014A07080 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014A07080 O00029      peace officers"
2014A07080 O00030
2014A07080 O00031TO THE SENATE:
2014A07080 O00032
2014A07080 O00033I am returning herewith, without my approval, the following bills:
2014A07080 O00034
2014A07080 O00035Senate Bill Number 3894-B, entitled:
2014A07080 O00036
2014A07080 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014A07080 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014A07080 O00039      of Chautauqua county within the grounds of and properties owned by
2014A07080 O00040      the Chautauqua Institution"
2014A07080 O00041
2014A07080 O00042Senate Bill Number 7470, entitled:
2014A07080 O00043
2014A07080 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014A07080 O00045      uniformed  members of the bureau of fire prevention of the town of
2014A07080 O00046      Islip peace officer status"
2014A07080 O00047
2014A07080 O00048Senate Bill Number 7786, entitled:
2014A07080 O00049
2014A07080 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A07080 O00051      nating  uniformed court officers of the town of Highlands as peace
2014A07080 O00052      officers"
2014A07080 O00053
2014A07080 O00054    NOT APPROVED
2014A07080 O00055
2014A07080 O00056  These seven bills would grant peace officer status to fire marshals or
2014A07080 O00057fire prevention service members in two counties, uniformed  court  offi-
2014A07080 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014A07080 O00059in one county. If designated as peace officers,  these  officials  would
2014A07080 O00060
2014A07080 O00061have many of the same legal powers as police officers. These include the
2014A07080 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014A07080 O00063warrantless searches and issue appearance tickets.
2014A07080 O00064
2014A07080 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014A07080 O00066mending that the Legislature create a comprehensive process  for  deter-
2014A07080 O00067mining which categories of officials, on a statewide basis, may need the
2014A07080 O00068police  powers granted to peace officers. I again ask the Legislature to
2014A07080 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014A07080 O00070within  the  broader  context of New York State''s law enforcement needs,
2014A07080 O00071rather than addressing the needs of local government units in an ad  hoc
2014A07080 O00072manner. For these reasons, I will not approve these bills.
2014A07080 O00073
2014A07080 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A07080 O00075                              __________
2014A07080 O00000.SO DOC VETO0491        *END*    A7080           VETO                 2014
2014A07673 O00000.SO DOC VETO0492                                 VETO                 2014
2014A07673 O00001
2014A07673 O00002                         VETO MESSAGE - No. 492
2014A07673 O00003
2014A07673 O00004TO THE ASSEMBLY:
2014A07673 O00005
2014A07673 O00006I am returning herewith, without my approval, the following bill:
2014A07673 O00007
2014A07673 O00008Assembly Bill Number 7673, entitled:
2014A07673 O00009
2014A07673 O00010    "AN  ACT  to  amend the New York State urban development corporation
2014A07673 O00011      act, in relation to  certain  grants  for  assistance  to  provide
2014A07673 O00012      advanced manufacturing employers with a competitive workforce"
2014A07673 O00013
2014A07673 O00014    NOT APPROVED
2014A07673 O00015
2014A07673 O00016  This  bill  would  direct  Empire  State  Development (ESD) to provide
2014A07673 O00017grants, from the Empire State Economic Development Fund (EDF), to educa-
2014A07673 O00018tional institutions, not-for-profits, industry  public-private  partner-
2014A07673 O00019ships,  and  individuals  for training and certification needed to enter
2014A07673 O00020the field of advanced manufacturing.
2014A07673 O00021
2014A07673 O00022  This Administration has made workforce training a vital  component  in
2014A07673 O00023growing the State''s economy, and its implementation is reflected in many
2014A07673 O00024of  the  Regional Economic Development Council''s Strategic Plans.  Addi-
2014A07673 O00025tionally, ESD is already authorized to provide funding through  the  EDF
2014A07673 O00026to  educational  institutions and public, not-for-profit, and for-profit
2014A07673 O00027entities to create and retain jobs, prevent, reduce, and eliminate unem-
2014A07673 O00028ployment and underemployment, and provide training in advanced  manufac-
2014A07673 O00029turing.
2014A07673 O00030
2014A07673 O00031  Creating another grant program to be paid out of the EDF would require
2014A07673 O00032increased funding, which the Legislature has failed to provide. The best
2014A07673 O00033place  to  consider  increasing  the funding of the EDF should be in the
2014A07673 O00034State Budget. Therefore, I am constrained to veto this bill.
2014A07673 O00035
2014A07673 O00036  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A07673 O00037                              __________
2014A07673 O00000.SO DOC VETO0492        *END*    A7673           VETO                 2014
2014A07706 O00000.SO DOC VETO0493                                 VETO                 2014
2014A07706 O00001
2014A07706 O00002                         VETO MESSAGE - No. 493
2014A07706 O00003
2014A07706 O00004TO THE ASSEMBLY:
2014A07706 O00005
2014A07706 O00006I am returning herewith, without my approval, the following bills:
2014A07706 O00007
2014A07706 O00008Assembly Bill Number 7706, entitled:
2014A07706 O00009
2014A07706 O00010    "AN  ACT  to amend the soil and water conservation districts law, in
2014A07706 O00011      relation to establishing a drain tile revolving loan program"
2014A07706 O00012
2014A07706 O00013Assembly Bill Number 9288-A, entitled:
2014A07706 O00014
2014A07706 O00015    "AN ACT to amend the agriculture and markets law, in relation to the
2014A07706 O00016      beginning farmer revolving loan fund program"
2014A07706 O00017
2014A07706 O00018    NOT APPROVED
2014A07706 O00019
2014A07706 O00020  Each of these bills would create a new revolving loan program  without
2014A07706 O00021appropriation  to  be  administered by the Department of Agriculture and
2014A07706 O00022Markets (DAM) and the New York State Soil & Water Conservation Committee
2014A07706 O00023(SWCC). One program would provide low  interest  loans  to  farmers  for
2014A07706 O00024enhancing their farm fields with drain tile, and the other would support
2014A07706 O00025beginning farmers.
2014A07706 O00026
2014A07706 O00027  I  share the sponsors'' concern for providing critical support to farm-
2014A07706 O00028ers, and I have implemented several initiatives to provide financial and
2014A07706 O00029other assistance to grow existing farms and support  new  farms  in  New
2014A07706 O00030York.  The  2014-15 Budget funded the New York Beginning Farmers Fund to
2014A07706 O00031provide grants to beginning farmers, including access to funds for  tile
2014A07706 O00032drainage, and for student loan forgiveness for agriculture college grad-
2014A07706 O00033uates  who  commit to farm in New York. In addition, DAM has initiated a
2014A07706 O00034Beginning Farmer Working Group consisting of farmers,  private  lenders,
2014A07706 O00035and other stakeholders to examine issues important to new farmers and to
2014A07706 O00036assess  the  demand  for  additional  technical and financial assistance
2014A07706 O00037beyond the State and federal assistance already available  to  beginning
2014A07706 O00038farmers.
2014A07706 O00039
2014A07706 O00040  Although  the  intent  of these bills is laudable, the Division of the
2014A07706 O00041Budget estimates it would cost the  State  several  million  dollars  to
2014A07706 O00042capitalize each revolving loan fund and several hundred thousand dollars
2014A07706 O00043annually  to  administer each loan program. Because of the fiscal impli-
2014A07706 O00044cations, these  bills  should  be  considered  in  budget  negotiations.
2014A07706 O00045Therefore, I will not approve either of these bills.
2014A07706 O00046
2014A07706 O00047  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A07706 O00048                              __________
2014A07706 O00000.SO DOC VETO0493        *END*    A7706           VETO                 2014
2014A07721 O00000.SO DOC VETO0494                                 VETO                 2014
2014A07721 O00001
2014A07721 O00002                         VETO MESSAGE - No. 494
2014A07721 O00003
2014A07721 O00004TO THE ASSEMBLY:
2014A07721 O00005
2014A07721 O00006I am returning herewith, without my approval, the following bill:
2014A07721 O00007
2014A07721 O00008Assembly Bill Number 7721-A, entitled:
2014A07721 O00009
2014A07721 O00010    "AN ACT to amend the mental hygiene law, in relation to establishing
2014A07721 O00011      a community housing wait list"
2014A07721 O00012
2014A07721 O00013    NOT APPROVED
2014A07721 O00014
2014A07721 O00015  This  legislation  would  require the Office of Mental Health (OMH) to
2014A07721 O00016establish a statewide community  housing  wait  list  for  persons  with
2014A07721 O00017mental illnesses.
2014A07721 O00018
2014A07721 O00019  I  share the sponsors'' goal of integrating persons with mental illness
2014A07721 O00020into the community. New York is a national leader in the development  of
2014A07721 O00021community  housing  for persons with serious mental illness. The 2014-15
2014A07721 O00022State Budget for OMH includes over $400 million in funding for  housing,
2014A07721 O00023including over $200 million for supported housing.
2014A07721 O00024
2014A07721 O00025  This  bill  would duplicate OMH''s "Single Point of Access" initiative,
2014A07721 O00026which works at the local level to match individuals with mental  illness
2014A07721 O00027with  health  and  housing  services  programs.  The  bill''s approach is
2014A07721 O00028flawed: it would not provide a  clear,  in-time,  localized  picture  of
2014A07721 O00029available  housing resources and, therefore, would not effectively match
2014A07721 O00030persons with appropriate housing vacancies as they arise.  In  addition,
2014A07721 O00031the  bill  does  not  provide clear enforcement authority for OMH in the
2014A07721 O00032event providers or counties fail to submit information on a timely basis
2014A07721 O00033or  submit  erroneous  information.  Without  appropriate  controls  and
2014A07721 O00034enforcement,  the accuracy of a statewide wait list could not be guaran-
2014A07721 O00035teed.
2014A07721 O00036
2014A07721 O00037  This bill would also have an unbudgeted fiscal impact  on  the  State.
2014A07721 O00038For these reasons, I cannot approve this bill.
2014A07721 O00039
2014A07721 O00040  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A07721 O00041                              __________
2014A07721 O00000.SO DOC VETO0494        *END*    A7721           VETO                 2014
2014A08320 O00000.SO DOC VETO0495                                 VETO                 2014
2014A08320 O00001
2014A08320 O00002                         VETO MESSAGE - No. 495
2014A08320 O00003
2014A08320 O00004TO THE ASSEMBLY:
2014A08320 O00005
2014A08320 O00006I am returning herewith, without my approval, the following bill:
2014A08320 O00007
2014A08320 O00008Assembly Bill Number 8320-B, entitled:
2014A08320 O00009
2014A08320 O00010    "AN  ACT  to  amend the real property tax law, in relation to a real
2014A08320 O00011      property tax exemption for  farm  dwellings  owned  by  a  limited
2014A08320 O00012      liability company"
2014A08320 O00013
2014A08320 O00014    NOT APPROVED
2014A08320 O00015
2014A08320 O00016  This  bill  would extend the STAR exemption in farm dwellings owned by
2014A08320 O00017an LLC, a measure which would have  an  annual  fiscal  impact  of  $2.5
2014A08320 O00018million.    Consideration  of  legislation  with such an impact on State
2014A08320 O00019finances should occur in the context of negotiating the budget, when all
2014A08320 O00020of the State''s fiscal needs are considered in a  comprehensive  fashion.
2014A08320 O00021For this reason, I must disapprove this legislation.
2014A08320 O00022
2014A08320 O00023  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08320 O00024                              __________
2014A08320 O00000.SO DOC VETO0495        *END*    A8320           VETO                 2014
2014A08452 O00000.SO DOC VETO0496                                 VETO                 2014
2014A08452 O00001
2014A08452 O00002                         VETO MESSAGE - No. 496
2014A08452 O00003
2014A08452 O00004TO THE ASSEMBLY:
2014A08452 O00005
2014A08452 O00006I am returning herewith, without my approval, the following bill:
2014A08452 O00007
2014A08452 O00008Assembly Bill Number 8452, entitled:
2014A08452 O00009
2014A08452 O00010    "AN ACT to amend the mental hygiene law, in relation to enacting the
2014A08452 O00011      ''people first act of 2014''"
2014A08452 O00012
2014A08452 O00013    NOT APPROVED
2014A08452 O00014
2014A08452 O00015  This  bill  would  require  the  Office for Persons with Developmental
2014A08452 O00016Disabilities (OPWDD) to conduct an analysis of service and support needs
2014A08452 O00017of individuals with developmental disabilities annually. However,  OPWDD
2014A08452 O00018actively  engages in developing plans to serve individuals with develop-
2014A08452 O00019mental disabilities in  collaboration  with  other  State  agencies  and
2014A08452 O00020external  stakeholders;  indeed,  that  is its stated purpose. This bill
2014A08452 O00021simply duplicates existing agency efforts and  would  impose  additional
2014A08452 O00022bureaucratic burdens at significant additional costs with no discernible
2014A08452 O00023extra  benefit  flowing  to  recipients  of  such services. Therefore, I
2014A08452 O00024cannot approve this bill.
2014A08452 O00025
2014A08452 O00026  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08452 O00027                              __________
2014A08452 O00000.SO DOC VETO0496        *END*    A8452           VETO                 2014
2014A08630 O00000.SO DOC VETO0497                                 VETO                 2014
2014A08630 O00001
2014A08630 O00002                         VETO MESSAGE - No. 497
2014A08630 O00003
2014A08630 O00004TO THE ASSEMBLY:
2014A08630 O00005
2014A08630 O00006I am returning herewith, without my approval, the following bills:
2014A08630 O00007
2014A08630 O00008Assembly Bill Number 8630-A, entitled:
2014A08630 O00009
2014A08630 O00010    "AN  ACT to amend the executive law, in relation to accurate report-
2014A08630 O00011      ing of crime statistics;  and  to  amend  the  insurance  law,  in
2014A08630 O00012      relation  to excluding certain crime statistics from consideration
2014A08630 O00013      when making insurance rates"
2014A08630 O00014
2014A08630 O00015    NOT APPROVED
2014A08630 O00016
2014A08630 O00017  This bill would require the  Division  of  Criminal  Justice  Services
2014A08630 O00018(Division)  to  gather and report on data regarding criminal activity in
2014A08630 O00019any federal, state or local correctional facility,  detention  facility,
2014A08630 O00020transportation  facility or other facility located within the geographic
2014A08630 O00021boundaries of a police department. In many cases, other entities already
2014A08630 O00022collect this information. Moreover, this program would impose a substan-
2014A08630 O00023tial unbudgeted cost on the Division for which the Legislature  provided
2014A08630 O00024no appropriation. Such issues are better addressed within the context of
2014A08630 O00025negotiations  for the upcoming State budget. Therefore, I cannot approve
2014A08630 O00026this bill.
2014A08630 O00027
2014A08630 O00028  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08630 O00029                              __________
2014A08630 O00000.SO DOC VETO0497        *END*    A8630           VETO                 2014
2014A08761 O00000.SO DOC VETO0498                                 VETO                 2014
2014A08761 O00001
2014A08761 O00002                         VETO MESSAGE - No. 498
2014A08761 O00003
2014A08761 O00004TO THE ASSEMBLY:
2014A08761 O00005
2014A08761 O00006I am returning herewith, without my approval, the following bill:
2014A08761 O00007
2014A08761 O00008Assembly Bill Number 8761-C, entitled:
2014A08761 O00009
2014A08761 O00010    "AN ACT to amend the state finance law, in relation to certain muni-
2014A08761 O00011      cipalities receiving state aid"
2014A08761 O00012
2014A08761 O00013    NOT APPROVED
2014A08761 O00014
2014A08761 O00015  This  bill  would  make certain municipalities incorporated after 2005
2014A08761 O00016eligible for Aid and Incentives for Municipalities (AIM) funding.    AIM
2014A08761 O00017issues  have always been addressed in the broader context of considering
2014A08761 O00018the State budget; this issue, too, is best dealt  with  in  that  venue.
2014A08761 O00019Therefore, I cannot approve this bill.
2014A08761 O00020
2014A08761 O00021  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08761 O00022                              __________
2014A08761 O00000.SO DOC VETO0498        *END*    A8761           VETO                 2014
2014A08835 O00000.SO DOC VETO0499                                 VETO                 2014
2014A08835 O00001
2014A08835 O00002                         VETO MESSAGE - No. 499
2014A08835 O00003
2014A08835 O00004TO THE ASSEMBLY:
2014A08835 O00005
2014A08835 O00006I am returning herewith, without my approval, the following bill:
2014A08835 O00007
2014A08835 O00008Assembly Bill Number 8835-A, entitled:
2014A08835 O00009
2014A08835 O00010    "AN ACT to amend the mental hygiene law, in relation to establishing
2014A08835 O00011      the  task  force  on  adults  with developmental disabilities; and
2014A08835 O00012      providing for the repeal of such provisions upon expiration there-
2014A08835 O00013      of"
2014A08835 O00014
2014A08835 O00015    NOT APPROVED
2014A08835 O00016
2014A08835 O00017  This bill would establish a task force to make recommendations regard-
2014A08835 O00018ing measures to meet the needs of adults  with  developmental  disabili-
2014A08835 O00019ties;  this is, in fact and in law, the mission of the Office for People
2014A08835 O00020with Developmental Disabilities (OPWDD). Moreover, last year  I  created
2014A08835 O00021by  Executive  Order No. 84 an "Olmstead Cabinet" to address the issues,
2014A08835 O00022and the cabinet has already issued a comprehensive plan to assist  these
2014A08835 O00023individuals. This legislation simply duplicates efforts that this admin-
2014A08835 O00024istration has undertaken. OPWDD has been and will continue to be active-
2014A08835 O00025ly  engaged  in developing plans to serve individuals with developmental
2014A08835 O00026disabilities in collaboration with other  State  agencies  and  external
2014A08835 O00027stakeholders. For these reasons, I do not approve this bill.
2014A08835 O00028
2014A08835 O00029  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08835 O00030                              __________
2014A08835 O00000.SO DOC VETO0499        *END*    A8835           VETO                 2014
2014A08924 O00000.SO DOC VETO0500                                 VETO                 2014
2014A08924 O00001
2014A08924 O00002                         VETO MESSAGE - No. 500
2014A08924 O00003
2014A08924 O00004TO THE ASSEMBLY:
2014A08924 O00005
2014A08924 O00006I am returning herewith, without my approval, the following bill:
2014A08924 O00007
2014A08924 O00008Assembly Bill Number 8924-A, entitled:
2014A08924 O00009
2014A08924 O00010    "AN ACT to amend the social service law, in relation to creating the
2014A08924 O00011      child care regulatory review task force"
2014A08924 O00012
2014A08924 O00013    NOT APPROVED
2014A08924 O00014
2014A08924 O00015  This  bill would establish a task force, primarily consisting of State
2014A08924 O00016agency commissioners, to review the  processes  as  well  as  statutory,
2014A08924 O00017regulatory  and programmatic requirements placed on child care providers
2014A08924 O00018and to make recommendations annually for the streamlining of those proc-
2014A08924 O00019esses and requirements.
2014A08924 O00020
2014A08924 O00021  While the bill''s goal is laudable, it  would  duplicate  the  recently
2014A08924 O00022completed review of child care regulations by the Office of Children and
2014A08924 O00023Family Services, which considered comments from child care providers and
2014A08924 O00024other  members of the public pursuant to the State Administrative Proce-
2014A08924 O00025dure Act process. This regulatory review resulted in  significant  child
2014A08924 O00026care   improvements  that  strengthened  health  and  safety  standards,
2014A08924 O00027corrected conflicting regulatory language relative to the administration
2014A08924 O00028of medication, updated regulations to conform to  state  law,  and  made
2014A08924 O00029regulations  easier for providers to understand. For these reasons, I am
2014A08924 O00030compelled to disapprove the bill.
2014A08924 O00031
2014A08924 O00032  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A08924 O00033                              __________
2014A08924 O00000.SO DOC VETO0500        *END*    A8924           VETO                 2014
2014A09170 O00000.SO DOC VETO0501                                 VETO                 2014
2014A09170 O00001
2014A09170 O00002                         VETO MESSAGE - No. 501
2014A09170 O00003
2014A09170 O00004TO THE ASSEMBLY:
2014A09170 O00005
2014A09170 O00006I am returning herewith, without my approval, the following bill:
2014A09170 O00007
2014A09170 O00008Assembly Bill Number 9170, entitled:
2014A09170 O00009
2014A09170 O00010    "AN ACT to amend the administrative code of the city of New York, in
2014A09170 O00011      relation  to  requiring  assessment-rolls  to  be published on the
2014A09170 O00012      department of finance website"
2014A09170 O00013
2014A09170 O00014    NOT APPROVED
2014A09170 O00015
2014A09170 O00016  This bill would require New York City''s Department of Finance (DoF) to
2014A09170 O00017post its property tax assessment roll and certain property  tax  assess-
2014A09170 O00018ment  data on its website and create a document containing such informa-
2014A09170 O00019tion in a searchable format within nine months.
2014A09170 O00020
2014A09170 O00021  I vetoed similar legislation last year (Veto Memo No.  244  of  2013).
2014A09170 O00022This  bill  would  mandate  that  DoF  either  disclose information in a
2014A09170 O00023website searchable format, which is already provided on  DoF''s  website,
2014A09170 O00024or  disclose information that would be confusing, cumbersome and unhelp-
2014A09170 O00025ful to property owners. As a result, again, I cannot approve this bill.
2014A09170 O00026
2014A09170 O00027  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09170 O00028                              __________
2014A09170 O00000.SO DOC VETO0501        *END*    A9170           VETO                 2014
2014A09288 O00000.SO DOC VETO0502                                 VETO                 2014
2014A09288 O00001
2014A09288 O00002                         VETO MESSAGE - No. 502
2014A09288 O00003
2014A09288 O00004TO THE ASSEMBLY:
2014A09288 O00005
2014A09288 O00006I am returning herewith, without my approval, the following bills:
2014A09288 O00007
2014A09288 O00008Assembly Bill Number 7706, entitled:
2014A09288 O00009
2014A09288 O00010    "AN  ACT  to amend the soil and water conservation districts law, in
2014A09288 O00011      relation to establishing a drain tile revolving loan program"
2014A09288 O00012
2014A09288 O00013Assembly Bill Number 9288-A, entitled:
2014A09288 O00014
2014A09288 O00015    "AN ACT to amend the agriculture and markets law, in relation to the
2014A09288 O00016      beginning farmer revolving loan fund program"
2014A09288 O00017
2014A09288 O00018    NOT APPROVED
2014A09288 O00019
2014A09288 O00020  Each of these bills would create a new revolving loan program  without
2014A09288 O00021appropriation  to  be  administered by the Department of Agriculture and
2014A09288 O00022Markets (DAM) and the New York State Soil & Water Conservation Committee
2014A09288 O00023(SWCC). One program would provide low  interest  loans  to  farmers  for
2014A09288 O00024enhancing their farm fields with drain tile, and the other would support
2014A09288 O00025beginning farmers.
2014A09288 O00026
2014A09288 O00027  I  share the sponsors'' concern for providing critical support to farm-
2014A09288 O00028ers, and I have implemented several initiatives to provide financial and
2014A09288 O00029other assistance to grow existing farms and support  new  farms  in  New
2014A09288 O00030York.  The  2014-15 Budget funded the New York Beginning Farmers Fund to
2014A09288 O00031provide grants to beginning farmers, including access to funds for  tile
2014A09288 O00032drainage, and for student loan forgiveness for agriculture college grad-
2014A09288 O00033uates  who  commit to farm in New York. In addition, DAM has initiated a
2014A09288 O00034Beginning Farmer Working Group consisting of farmers,  private  lenders,
2014A09288 O00035and other stakeholders to examine issues important to new farmers and to
2014A09288 O00036assess  the  demand  for  additional  technical and financial assistance
2014A09288 O00037beyond the State and federal assistance already available  to  beginning
2014A09288 O00038farmers.
2014A09288 O00039
2014A09288 O00040  Although  the  intent  of these bills is laudable, the Division of the
2014A09288 O00041Budget estimates it would cost the  State  several  million  dollars  to
2014A09288 O00042capitalize each revolving loan fund and several hundred thousand dollars
2014A09288 O00043annually  to  administer each loan program. Because of the fiscal impli-
2014A09288 O00044cations, these  bills  should  be  considered  in  budget  negotiations.
2014A09288 O00045Therefore, I will not approve either of these bills.
2014A09288 O00046
2014A09288 O00047  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A09288 O00048                              __________
2014A09288 O00000.SO DOC VETO0502        *END*    A9288           VETO                 2014
2014A09330 O00000.SO DOC VETO0503                                 VETO                 2014
2014A09330 O00001
2014A09330 O00002                         VETO MESSAGE - No. 503
2014A09330 O00003
2014A09330 O00004TO THE ASSEMBLY:
2014A09330 O00005
2014A09330 O00006I am returning herewith, without my approval, the following bills:
2014A09330 O00007
2014A09330 O00008Assembly Bill Number 3765-A, entitled:
2014A09330 O00009
2014A09330 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014A09330 O00011      services in the courts"
2014A09330 O00012
2014A09330 O00013Assembly Bill Number 7080, entitled:
2014A09330 O00014
2014A09330 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014A09330 O00016      nating uniformed officers of the fire marshal''s office of the town
2014A09330 O00017      of Huntington as peace officers"
2014A09330 O00018
2014A09330 O00019Assembly Bill Number 9330, entitled:
2014A09330 O00020
2014A09330 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09330 O00022      nating  uniformed court attendants in the town of Ossining, county
2014A09330 O00023      of Westchester as peace officers"
2014A09330 O00024
2014A09330 O00025Assembly Bill Number 9843, entitled:
2014A09330 O00026
2014A09330 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09330 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014A09330 O00029      peace officers"
2014A09330 O00030
2014A09330 O00031TO THE SENATE:
2014A09330 O00032
2014A09330 O00033I am returning herewith, without my approval, the following bills:
2014A09330 O00034
2014A09330 O00035Senate Bill Number 3894-B, entitled:
2014A09330 O00036
2014A09330 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014A09330 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014A09330 O00039      of Chautauqua county within the grounds of and properties owned by
2014A09330 O00040      the Chautauqua Institution"
2014A09330 O00041
2014A09330 O00042Senate Bill Number 7470, entitled:
2014A09330 O00043
2014A09330 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014A09330 O00045      uniformed  members of the bureau of fire prevention of the town of
2014A09330 O00046      Islip peace officer status"
2014A09330 O00047
2014A09330 O00048Senate Bill Number 7786, entitled:
2014A09330 O00049
2014A09330 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09330 O00051      nating  uniformed court officers of the town of Highlands as peace
2014A09330 O00052      officers"
2014A09330 O00053
2014A09330 O00054    NOT APPROVED
2014A09330 O00055
2014A09330 O00056  These seven bills would grant peace officer status to fire marshals or
2014A09330 O00057fire prevention service members in two counties, uniformed  court  offi-
2014A09330 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014A09330 O00059in one county. If designated as peace officers,  these  officials  would
2014A09330 O00060
2014A09330 O00061have many of the same legal powers as police officers. These include the
2014A09330 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014A09330 O00063warrantless searches and issue appearance tickets.
2014A09330 O00064
2014A09330 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014A09330 O00066mending that the Legislature create a comprehensive process  for  deter-
2014A09330 O00067mining which categories of officials, on a statewide basis, may need the
2014A09330 O00068police  powers granted to peace officers. I again ask the Legislature to
2014A09330 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014A09330 O00070within  the  broader  context of New York State''s law enforcement needs,
2014A09330 O00071rather than addressing the needs of local government units in an ad  hoc
2014A09330 O00072manner. For these reasons, I will not approve these bills.
2014A09330 O00073
2014A09330 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A09330 O00075                              __________
2014A09330 O00000.SO DOC VETO0503        *END*    A9330           VETO                 2014
2014A09493 O00000.SO DOC VETO0504                                 VETO                 2014
2014A09493 O00001
2014A09493 O00002                         VETO MESSAGE - No. 504
2014A09493 O00003
2014A09493 O00004TO THE ASSEMBLY:
2014A09493 O00005
2014A09493 O00006I am returning herewith, without my approval, the following bill:
2014A09493 O00007
2014A09493 O00008Assembly Bill Number 9493, entitled:
2014A09493 O00009
2014A09493 O00010    "AN  ACT  to  amend the civil service law, in relation to permitting
2014A09493 O00011      the name of a promotion  candidate  to  appear  on  two  promotion
2014A09493 O00012      eligible lists for the same title, under certain circumstances"
2014A09493 O00013
2014A09493 O00014    NOT APPROVED
2014A09493 O00015
2014A09493 O00016  This bill would allow individuals who are involuntarily transferred or
2014A09493 O00017reassigned  within  State  service  to  remain  on their former agency''s
2014A09493 O00018promotion eligible list until that list expires. This bill  allows  only
2014A09493 O00019these  particular  employees  to  be on two agencies'' promotion eligible
2014A09493 O00020lists with no limitation on the length of time they may remain  on  both
2014A09493 O00021lists. Other State employees have no such right, including those employ-
2014A09493 O00022ees  who  voluntarily  transfer  to a new agency. There is no reason for
2014A09493 O00023benefitting only one group of employees. For this reason, I am compelled
2014A09493 O00024to veto this bill.
2014A09493 O00025
2014A09493 O00026  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09493 O00027                              __________
2014A09493 O00000.SO DOC VETO0504        *END*    A9493           VETO                 2014
2014A09564 O00000.SO DOC VETO0505                                 VETO                 2014
2014A09564 O00001
2014A09564 O00002                         VETO MESSAGE - No. 505
2014A09564 O00003
2014A09564 O00004TO THE ASSEMBLY:
2014A09564 O00005
2014A09564 O00006I am returning herewith, without my approval, the following bill:
2014A09564 O00007
2014A09564 O00008Assembly Bill Number 9564, entitled:
2014A09564 O00009
2014A09564 O00010    "AN  ACT  to  amend the general municipal law and the administrative
2014A09564 O00011      code of the city of New York, in relation  to  service  by  police
2014A09564 O00012      officers in an undercover capacity"
2014A09564 O00013
2014A09564 O00014    NOT APPROVED
2014A09564 O00015
2014A09564 O00016  This legislation would limit the amount of time a police officer would
2014A09564 O00017be required to serve in an undercover capacity, a subject more appropri-
2014A09564 O00018ately  considered in collective bargaining than in legislation.  Indeed,
2014A09564 O00019on June 12, 2014, the New York City Police  Department  and  the  Detec-
2014A09564 O00020tives''  Endowment  Association  of  the  City of New York entered into a
2014A09564 O00021Memorandum of Agreement that specifically addressed this issue and elim-
2014A09564 O00022inated the need for this legislation. For this reason, I disapprove this
2014A09564 O00023bill.
2014A09564 O00024
2014A09564 O00025  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09564 O00026                              __________
2014A09564 O00000.SO DOC VETO0505        *END*    A9564           VETO                 2014
2014A09766 O00000.SO DOC VETO0506                                 VETO                 2014
2014A09766 O00001
2014A09766 O00002                         VETO MESSAGE - No. 506
2014A09766 O00003
2014A09766 O00004TO THE ASSEMBLY:
2014A09766 O00005
2014A09766 O00006I am returning herewith, without my approval, the following bills:
2014A09766 O00007
2014A09766 O00008Assembly Bill Number 9766-A, entitled:
2014A09766 O00009
2014A09766 O00010    "AN  ACT to amend the public health law, in relation to services for
2014A09766 O00011      individuals with developmental disabilities"
2014A09766 O00012
2014A09766 O00013    NOT APPROVED
2014A09766 O00014
2014A09766 O00015  This bill would limit the  entities  that  can  provide  managed  care
2014A09766 O00016services  for  individuals with developmental disabilities to those non-
2014A09766 O00017profits  with  prior  experience  providing  developmental  disabilities
2014A09766 O00018services  in New York State. In so restricting providers of managed care
2014A09766 O00019services to only those with prior experience under the regulation of New
2014A09766 O00020York State, the bill would violate the Commerce  Clause  of  the  United
2014A09766 O00021States Constitution. Therefore, I cannot approve this bill.
2014A09766 O00022
2014A09766 O00023  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09766 O00024                              __________
2014A09766 O00000.SO DOC VETO0506        *END*    A9766           VETO                 2014
2014A09798 O00000.SO DOC VETO0507                                 VETO                 2014
2014A09798 O00001
2014A09798 O00002                         VETO MESSAGE - No. 507
2014A09798 O00003
2014A09798 O00004TO THE ASSEMBLY:
2014A09798 O00005
2014A09798 O00006I am returning herewith, without my approval, the following bill:
2014A09798 O00007
2014A09798 O00008Assembly Bill Number 9798, entitled:
2014A09798 O00009
2014A09798 O00010    "AN  ACT  to  amend the New York state urban development corporation
2014A09798 O00011      act, in relation to extending the amount of time between notice of
2014A09798 O00012      a project and a public hearing"
2014A09798 O00013
2014A09798 O00014    NOT APPROVED
2014A09798 O00015
2014A09798 O00016  This bill would require the Urban  Development  Corporation  (UDC)  to
2014A09798 O00017give  a  30-day notice period for all capital projects regardless of the
2014A09798 O00018type of project or funding source. Since 1990, and with the exception of
2014A09798 O00019projects involving the exercise of condemnation power, the UDC has  only
2014A09798 O00020been required to give 10-days notice.
2014A09798 O00021
2014A09798 O00022  The  10-day  notice period enacted in 1990 has greatly streamlined the
2014A09798 O00023delivery of economic development assistance, allowing projects  to  meet
2014A09798 O00024all  legal  requirements and to be presented expeditiously to the Public
2014A09798 O00025Authorities Control Board for final  approval,  often  within  the  same
2014A09798 O00026month.  The current law has been embraced by both the communities served
2014A09798 O00027and project developers. This bill  would  unnecessarily  delay  economic
2014A09798 O00028development  assistance  to  hundreds  of projects throughout the state,
2014A09798 O00029many of them in economically distressed upstate communities.  For  these
2014A09798 O00030reasons, I am compelled to veto this bill.
2014A09798 O00031
2014A09798 O00032  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09798 O00033                              __________
2014A09798 O00000.SO DOC VETO0507        *END*    A9798           VETO                 2014
2014A09843 O00000.SO DOC VETO0508                                 VETO                 2014
2014A09843 O00001
2014A09843 O00002                         VETO MESSAGE - No. 508
2014A09843 O00003
2014A09843 O00004TO THE ASSEMBLY:
2014A09843 O00005
2014A09843 O00006I am returning herewith, without my approval, the following bills:
2014A09843 O00007
2014A09843 O00008Assembly Bill Number 3765-A, entitled:
2014A09843 O00009
2014A09843 O00010    "AN ACT to amend the criminal procedure law, in relation to security
2014A09843 O00011      services in the courts"
2014A09843 O00012
2014A09843 O00013Assembly Bill Number 7080, entitled:
2014A09843 O00014
2014A09843 O00015    "AN  ACT  to amend the criminal procedure law, in relation to desig-
2014A09843 O00016      nating uniformed officers of the fire marshal''s office of the town
2014A09843 O00017      of Huntington as peace officers"
2014A09843 O00018
2014A09843 O00019Assembly Bill Number 9330, entitled:
2014A09843 O00020
2014A09843 O00021    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09843 O00022      nating  uniformed court attendants in the town of Ossining, county
2014A09843 O00023      of Westchester as peace officers"
2014A09843 O00024
2014A09843 O00025Assembly Bill Number 9843, entitled:
2014A09843 O00026
2014A09843 O00027    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09843 O00028      nating  uniformed  court  officers  in  the town of New Windsor as
2014A09843 O00029      peace officers"
2014A09843 O00030
2014A09843 O00031TO THE SENATE:
2014A09843 O00032
2014A09843 O00033I am returning herewith, without my approval, the following bills:
2014A09843 O00034
2014A09843 O00035Senate Bill Number 3894-B, entitled:
2014A09843 O00036
2014A09843 O00037    "AN ACT to amend the criminal procedure law, in  relation  to  peace
2014A09843 O00038      officer status of special deputy sheriffs appointed by the sheriff
2014A09843 O00039      of Chautauqua county within the grounds of and properties owned by
2014A09843 O00040      the Chautauqua Institution"
2014A09843 O00041
2014A09843 O00042Senate Bill Number 7470, entitled:
2014A09843 O00043
2014A09843 O00044    "AN ACT to amend the criminal procedure law, in relation to granting
2014A09843 O00045      uniformed  members of the bureau of fire prevention of the town of
2014A09843 O00046      Islip peace officer status"
2014A09843 O00047
2014A09843 O00048Senate Bill Number 7786, entitled:
2014A09843 O00049
2014A09843 O00050    "AN ACT to amend the criminal procedure law, in relation  to  desig-
2014A09843 O00051      nating  uniformed court officers of the town of Highlands as peace
2014A09843 O00052      officers"
2014A09843 O00053
2014A09843 O00054    NOT APPROVED
2014A09843 O00055
2014A09843 O00056  These seven bills would grant peace officer status to fire marshals or
2014A09843 O00057fire prevention service members in two counties, uniformed  court  offi-
2014A09843 O00058cers  or  court attendants in four counties, and special deputy sheriffs
2014A09843 O00059in one county. If designated as peace officers,  these  officials  would
2014A09843 O00060
2014A09843 O00061have many of the same legal powers as police officers. These include the
2014A09843 O00062powers  to: use force to make arrests, make warrantless arrests, conduct
2014A09843 O00063warrantless searches and issue appearance tickets.
2014A09843 O00064
2014A09843 O00065  In  2011,  2012, and 2013, I vetoed similar or identical bills, recom-
2014A09843 O00066mending that the Legislature create a comprehensive process  for  deter-
2014A09843 O00067mining which categories of officials, on a statewide basis, may need the
2014A09843 O00068police  powers granted to peace officers. I again ask the Legislature to
2014A09843 O00069work with me to develop such a  comprehensive  approach  to  this  issue
2014A09843 O00070within  the  broader  context of New York State''s law enforcement needs,
2014A09843 O00071rather than addressing the needs of local government units in an ad  hoc
2014A09843 O00072manner. For these reasons, I will not approve these bills.
2014A09843 O00073
2014A09843 O00074  These bills are disapproved.                (signed) ANDREW M. CUOMO
2014A09843 O00075                              __________
2014A09843 O00000.SO DOC VETO0508        *END*    A9843           VETO                 2014
2014A09847 O00000.SO DOC VETO0509                                 VETO                 2014
2014A09847 O00001
2014A09847 O00002                         VETO MESSAGE - No. 509
2014A09847 O00003
2014A09847 O00004TO THE ASSEMBLY:
2014A09847 O00005
2014A09847 O00006I am returning herewith, without my approval, the following bill:
2014A09847 O00007
2014A09847 O00008Assembly Bill Number 9847-A, entitled:
2014A09847 O00009
2014A09847 O00010    "AN  ACT  creating  the Starr public library district in the town of
2014A09847 O00011      Rhinebeck, New York; and to amend chapter 672 of the laws of  1993
2014A09847 O00012      amending  the  public authorities law relating to the construction
2014A09847 O00013      and financing of  facilities  for  certain  public  libraries,  in
2014A09847 O00014      relation to including the Starr public library district within the
2014A09847 O00015      provisions of such chapter and providing for financing through the
2014A09847 O00016      dormitory authority"
2014A09847 O00017
2014A09847 O00018    NOT APPROVED
2014A09847 O00019
2014A09847 O00020  This  bill  would  authorize  the creation of the Starr Public Library
2014A09847 O00021District.
2014A09847 O00022
2014A09847 O00023  While I appreciate the Legislature''s desire to assist in  the  mainte-
2014A09847 O00024nance  and  growth  of  a  public library, I am concerned that this bill
2014A09847 O00025would establish yet another level of local government. At  a  time  when
2014A09847 O00026taxpayers are being overwhelmed with out-of-control property taxes, this
2014A09847 O00027bill  has the potential to add to this onerous burden and add further to
2014A09847 O00028the plethora of levels of local government.
2014A09847 O00029
2014A09847 O00030  For this reason, I disapprove this bill.
2014A09847 O00031
2014A09847 O00032  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A09847 O00033                              __________
2014A09847 O00000.SO DOC VETO0509        *END*    A9847           VETO                 2014
2014A09977 O00000.SO DOC VETO0510                                 VETO                 2014
2014A09977 O00001
2014A09977 O00002                         VETO MESSAGE - No. 510
2014A09977 O00003
2014A09977 O00004TO THE SENATE:
2014A09977 O00005
2014A09977 O00006I am returning herewith, without my approval, the following bill:
2014A09977 O00007
2014A09977 O00008Senate Bill Number 6124-A, entitled:
2014A09977 O00009
2014A09977 O00010    "AN  ACT  in  relation  to  legalizing,  validating,  ratifying  and
2014A09977 O00011      confirming a transportation contract of the Perry  central  school
2014A09977 O00012      district"
2014A09977 O00013
2014A09977 O00014TO THE ASSEMBLY:
2014A09977 O00015
2014A09977 O00016I am returning herewith, without my approval, the following bill:
2014A09977 O00017
2014A09977 O00018Assembly Bill Number 9977-A, entitled:
2014A09977 O00019
2014A09977 O00020    "AN  ACT  to  provide  for the repayment by the Johnson City central
2014A09977 O00021      school district of certain excess state payments"
2014A09977 O00022
2014A09977 O00023    NOT APPROVED
2014A09977 O00024
2014A09977 O00025  Each of these bills would authorize, outside the State Budget process,
2014A09977 O00026payment of State education aid above the amounts calculated  and  previ-
2014A09977 O00027ously agreed to under current law.
2014A09977 O00028
2014A09977 O00029  Assembly  Bill  Number  9977-A  would  allow  the Johnson City Central
2014A09977 O00030School District to repay a $1.99 million Building Aid  overpayment  over
2014A09977 O00031six years rather than over three years. As a result, the school district
2014A09977 O00032would receive $1.1 million of additional aid for the 2014-15 school year
2014A09977 O00033above  the  aid  amount  that  is currently authorized by law. In recent
2014A09977 O00034years, similar provisions have only been authorized in  the  context  of
2014A09977 O00035enacting the State budget.
2014A09977 O00036
2014A09977 O00037  Senate  Bill  Number  6124-A  would  validate  a  Perry Central School
2014A09977 O00038District transportation contract, even though  the  district  failed  to
2014A09977 O00039meet  long-standing statutory requirements for aid eligibility. Further,
2014A09977 O00040this transportation contract falls  outside  the  scope  of  forgiveness
2014A09977 O00041provisions that were negotiated by the Legislature and the Executive and
2014A09977 O00042included in the 2012-13 Enacted Budget, thereby undoing that agreed upon
2014A09977 O00043solution.
2014A09977 O00044
2014A09977 O00045  Each  of  these  bills  would result in increased and unbudgeted State
2014A09977 O00046costs. For the reasons stated above, I cannot approve these bills.
2014A09977 O00047
2014A09977 O00048  These bill are disapproved.                 (signed) ANDREW M. CUOMO
2014A09977 O00049                              __________
2014A09977 O00000.SO DOC VETO0510        *END*    A9977           VETO                 2014
2014A10049 O00000.SO DOC VETO0511                                 VETO                 2014
2014A10049 O00001
2014A10049 O00002                         VETO MESSAGE - No. 511
2014A10049 O00003
2014A10049 O00004TO THE ASSEMBLY:
2014A10049 O00005
2014A10049 O00006I am returning herewith, without my approval, the following bills:
2014A10049 O00007
2014A10049 O00008Assembly Bill Number 10049, entitled:
2014A10049 O00009
2014A10049 O00010    "AN  ACT to amend the tax law, in relation to establishing a musical
2014A10049 O00011      and theatrical production  business  franchise  credit;  to  amend
2014A10049 O00012      chapter 59 of the laws of 2014, amending the tax law relating to a
2014A10049 O00013      musical  and  theatrical  production  credit,  in  relation to the
2014A10049 O00014      effective date thereof; and providing for the  repeal  of  certain
2014A10049 O00015      provisions upon expiration thereof"
2014A10049 O00016
2014A10049 O00017    NOT APPROVED
2014A10049 O00018
2014A10049 O00019  This  bill would accelerate the availability of the musical and theat-
2014A10049 O00020rical production tax credit enacted in this year''s Enacted Budget by one
2014A10049 O00021year, from January 1, 2015  to  January  1,  2014.  The  Enacted  Budget
2014A10049 O00022provided  that  such  credit  would  be first available in taxable years
2014A10049 O00023beginning in 2015 and for four years thereafter. This  bill  would  make
2014A10049 O00024this credit available for the 2014 tax year even though all such invest-
2014A10049 O00025ments for 2014 have either occurred or have been scheduled. As a result,
2014A10049 O00026the bill would not further the purpose of the credit. Moreover, the bill
2014A10049 O00027could  produce  an  additional  cost  of  $4  million  for  Fiscal  Year
2014A10049 O000282015-2016. For these reasons, and because consideration of  such  legis-
2014A10049 O00029lation should take place in the context of negotiating the State budget,
2014A10049 O00030I am not approving this bill.
2014A10049 O00031
2014A10049 O00032  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A10049 O00033                              __________
2014A10049 O00000.SO DOC VETO0511        *END*    A10049          VETO                 2014
2014A08955 O00000.SO DOC APPR008                                  APPROVAL             2014
2014A08955 O00001
2014A08955 O00002                 APPROVAL MEMORANDUM - No. 8 Chapter 441
2014A08955 O00003
2014A08955 O00004      MEMORANDUM filed with Assembly Bill Number 8955-B, entitled:
2014A08955 O00005
2014A08955 O00006    "AN  ACT  to  amend  the general business law, in relation to credit
2014A08955 O00007        record freezes and protected minors"
2014A08955 O00008
2014A08955 O00009    APPROVED
2014A08955 O00010
2014A08955 O00011  This bill would allow parents and other authorized persons to  request
2014A08955 O00012credit record freezes for minors in order to protect them from the harms
2014A08955 O00013of  identity  theft.  As  passed,  this  bill contains certain technical
2014A08955 O00014defects.  The Legislature has agreed to  remedy  these  deficiencies  by
2014A08955 O00015passing additional legislation. On that basis, I am signing this bill.
2014A08955 O00016
2014A08955 O00017  This bill is approved.                      (signed) ANDREW M. CUOMO
2014A08955 O00018
2014A08955 O00019                                ______
2014A08955 O00000.SO DOC APPR008         *END*    A8955           APPROVAL             2014
2014S00089 O00000.SO DOC APPR009                                  APPROVAL             2014
2014S00089 O00001
2014S00089 O00002                 APPROVAL MEMORANDUM - No. 9 Chapter 474
2014S00089 O00003
2014S00089 O00004        MEMORANDUM filed with Senate Bill Number 89-A, entitled:
2014S00089 O00005
2014S00089 O00006    "AN  ACT  to  amend  the executive law, in relation to creation of a
2014S00089 O00007        uniform system for the  issuance  of  identification  cards  for
2014S00089 O00008        retired members of the state police"
2014S00089 O00009
2014S00089 O00010    APPROVED
2014S00089 O00011
2014S00089 O00012  This  legislation gives the Superintendent of the State Police express
2014S00089 O00013statutory authority to issue identification cards to those State  police
2014S00089 O00014officers who retire in good standing.
2014S00089 O00015
2014S00089 O00016  However,  as  passed,  the  bill  would  have  resulted in significant
2014S00089 O00017unfunded costs. The Legislature has agreed to amend the language driving
2014S00089 O00018those costs, and on that basis, I am signing this bill.
2014S00089 O00019
2014S00089 O00020  This bill is approved.                      (signed) ANDREW M. CUOMO
2014S00089 O00021
2014S00089 O00022                                ______
2014S00089 O00000.SO DOC APPR009         *END*    S89             APPROVAL             2014
2014S03810 O00000.SO DOC APPR010                                  APPROVAL             2014
2014S03810 O00001
2014S03810 O00002                APPROVAL MEMORANDUM - No. 10  Chapter 475
2014S03810 O00003
2014S03810 O00004       MEMORANDUM filed with SENATE Bill Number 3810-D, entitled:
2014S03810 O00005
2014S03810 O00006    "AN  ACT  to  amend the education law, the business corporation law,
2014S03810 O00007        the limited liability company law and the  partnership  law,  in
2014S03810 O00008        relation  to  providing  for  the licensing of the profession of
2014S03810 O00009        geology; and to repeal section 12 of chapter 550 of the laws  of
2014S03810 O00010        2011,  amending  the  business corporation law and the education
2014S03810 O00011        law relating to design professional service corporations"
2014S03810 O00012
2014S03810 O00013    APPROVED
2014S03810 O00014
2014S03810 O00015  This bill would provide for the licensure of  professional  geologists
2014S03810 O00016in  New  York  State.  Geologists  routinely  conduct investigations and
2014S03810 O00017provide interpretive geologic services related to  the  development  and
2014S03810 O00018protection  of  groundwater resources, the assessment and development of
2014S03810 O00019New York State''s mineral, gas, and oil reserves, and  the  environmental
2014S03810 O00020clean-up  of hazardous wastes and the potential for migration of contam-
2014S03810 O00021ination.   Rigorous licensure  requirements  for  this  profession  will
2014S03810 O00022provide  the public with an assurance of a minimum and standard level of
2014S03810 O00023competency and professional accountability.
2014S03810 O00024
2014S03810 O00025  Members of other licensed professions have expressed concern that this
2014S03810 O00026bill may limit or alter their ability  to  practice  their  professions.
2014S03810 O00027However,  nothing  in this act should be construed to alter, diminish or
2014S03810 O00028interfere with the existing scope of  practice  of  any  other  licensed
2014S03810 O00029profession  under  Title VIII of the Education Law, and the State Educa-
2014S03810 O00030tion Department has confirmed this analysis.
2014S03810 O00031
2014S03810 O00032  This bill, however, contained several  minor  technical  defects.  The
2014S03810 O00033Legislature  has  agreed to pass additional legislation to address these
2014S03810 O00034defects, thereby allowing  the  State  Education  Department  to  timely
2014S03810 O00035implement the statute. On that basis, I am signing this bill.
2014S03810 O00036
2014S03810 O00037  This bill is approved.                      (signed) ANDREW M. CUOMO
2014S03810 O00038
2014S03810 O00039                                ______
2014S03810 O00000.SO DOC APPR010         *END*    S3810           APPROVAL             2014
2014S06830 O00000.SO DOC APPR011                                  APPROVAL             2014
2014S06830 O00001
2014S06830 O00002                APPROVAL MEMORANDUM - No. 11  Chapter 476
2014S06830 O00003
2014S06830 O00004       MEMORANDUM filed with SENATE Bill Number 6830-A, entitled:
2014S06830 O00005
2014S06830 O00006    "AN  ACT  to amend the state law, in relation to cessation of juris-
2014S06830 O00007        diction of a parcel of land comprising part of  the  West  Point
2014S06830 O00008        Military Reservation"
2014S06830 O00009
2014S06830 O00010    APPROVED
2014S06830 O00011
2014S06830 O00012  This  bill would enact State Law Section 52-c authorizing the creation
2014S06830 O00013of a deed or release to cede to the United States full concurrent juris-
2014S06830 O00014diction over certain tracts of  land  within  the  West  Point  Military
2014S06830 O00015Reservation located in Orange County. This bill would give State, county
2014S06830 O00016and local law enforcement authorities legal jurisdiction concurrent with
2014S06830 O00017the federal government in the enforcement of state and federal laws, and
2014S06830 O00018would  increase security and protection, on the Military Reservation. On
2014S06830 O00019that basis, I am signing this bill.
2014S06830 O00020
2014S06830 O00021  This bill is approved.                      (signed) ANDREW M. CUOMO
2014S06830 O00022
2014S06830 O00023                                ______
2014S06830 O00000.SO DOC APPR011         *END*    S6830           APPROVAL             2014
2014S07888 O00000.SO DOC APPR012                                  APPROVAL             2014
2014S07888 O00001
2014S07888 O00002                APPROVAL MEMORANDUM - No. 12 Chapter 477
2014S07888 O00003
2014S07888 O00004        MEMORANDUM filed with Senate Bill Number 7888, entitled:
2014S07888 O00005
2014S07888 O00006    "AN  ACT  to  amend the penal law, the executive law and the general
2014S07888 O00007        business law, in relation to fireworks, dangerous fireworks  and
2014S07888 O00008        sparkling devices; and to repeal certain provisions of the penal
2014S07888 O00009        law relating thereto"
2014S07888 O00010
2014S07888 O00011    APPROVED
2014S07888 O00012
2014S07888 O00013  I  vetoed  earlier  versions  of  this  bill,  which  would permit the
2014S07888 O00014possession and sale of certain fireworks in  this  state  under  limited
2014S07888 O00015circumstances,  in  2011  (Veto No. 59) and in 2013 (Veto No. 281). This
2014S07888 O00016bill improves upon those bills by expressly banning  the  possession  of
2014S07888 O00017these fireworks in New York city and by requiring municipalities outside
2014S07888 O00018of  New  York  City  to  affirmatively  enact a local law electing to be
2014S07888 O00019covered by this legislation.
2014S07888 O00020
2014S07888 O00021  To address the imminent need for rules addressing public safety  risks
2014S07888 O00022related  to  the  possession  and sale of fireworks, I have directed the
2014S07888 O00023Department of Homeland Security and Emergency Services'' Office  of  Fire
2014S07888 O00024Prevention  and  Control  and  the  Department  of  State to immediately
2014S07888 O00025promulgate regulations focusing on, among other  safety  considerations,
2014S07888 O00026the  proper  storage, use, registration, incident reporting, and removal
2014S07888 O00027and disposal of these fireworks. The safe implementation of this  bill''s
2014S07888 O00028provisions is a priority of this Administration.
2014S07888 O00029
2014S07888 O00030  For these reasons, and because the Department of Homeland Security and
2014S07888 O00031Emergency  Services  and  the  Department  of State will be promulgating
2014S07888 O00032necessary regulations without delay, I approve this legislation.
2014S07888 O00033
2014S07888 O00034  This bill is approved.                      (signed) ANDREW M. CUOMO
2014S07888 O00035
2014S07888 O00036                                ______
2014S07888 O00000.SO DOC APPR012         *END*    S7888           APPROVAL             2014
2014S07374 O00000.SO DOC APPR013                                  APPROVAL             2014
2014S07374 O00001
2014S07374 O00002                APPROVAL MEMORANDUM - No. 13 Chapter 478
2014S07374 O00003
2014S07374 O00004        MEMORANDUM filed with Senate Bill Number 7374, entitled:
2014S07374 O00005
2014S07374 O00006    "AN ACT to amend the mental hygiene law, in relation to transitional
2014S07374 O00007        care"
2014S07374 O00008
2014S07374 O00009    APPROVED
2014S07374 O00010
2014S07374 O00011  This  bill  would  amend the statutory definition of transitional care
2014S07374 O00012and would create equitable rights  for  individuals  with  developmental
2014S07374 O00013disabilities under the Mental Hygiene Law.
2014S07374 O00014
2014S07374 O00015  School-age  individuals  with  a  disability may be placed in a school
2014S07374 O00016outside of their district if their  home  district  cannot  serve  their
2014S07374 O00017needs.  When  those  individuals  turn  21 and are, therefore, no longer
2014S07374 O00018eligible for educational assistance, the Office for Persons with  Devel-
2014S07374 O00019opmental  Disabilities (OPWDD) maintains transitional funding to support
2014S07374 O00020these individuals while it seeks to secure  an  appropriate  residential
2014S07374 O00021placement.  This bill would equalize existing due process provisions and
2014S07374 O00022ensure that all individuals receiving transitional care be provided with
2014S07374 O00023the opportunity to be heard with respect to the OPWDD''s proposed  place-
2014S07374 O00024ment.  This  Administration  has  always  supported the expansion of due
2014S07374 O00025process rights to all individuals receiving transitional care.  However,
2014S07374 O00026as  passed,  the  bill would impede the State''s ability to expeditiously
2014S07374 O00027transition individuals to  more  appropriate  settings  as  they  become
2014S07374 O00028available  and  would  also  have  significant,  unplanned fiscal impli-
2014S07374 O00029cations.
2014S07374 O00030
2014S07374 O00031  To ensure that this process is properly  implemented  and  to  promote
2014S07374 O00032fiscal  responsibility,  the  Legislature  has agreed to enact a chapter
2014S07374 O00033amendment that addresses these issues. In light of this agreement, I  am
2014S07374 O00034approving this legislation.
2014S07374 O00035
2014S07374 O00036  This bill is approved.                      (signed) ANDREW M. CUOMO
2014S07374 O00037
2014S07374 O00038                                ______
2014S07374 O00000.SO DOC APPR013         *END*    S7374           APPROVAL             2014
2014A07721 O00000.SO DOC VETO0494                                 VETO                 2014
2014A07721 O00001
2014A07721 O00002                         VETO MESSAGE - No. 494
2014A07721 O00003
2014A07721 O00004TO THE ASSEMBLY:
2014A07721 O00005
2014A07721 O00006I am returning herewith, without my approval, the following bill:
2014A07721 O00007
2014A07721 O00008Assembly Bill Number 7721-A, entitled:
2014A07721 O00009
2014A07721 O00010    "AN ACT to amend the mental hygiene law, in relation to establishing
2014A07721 O00011      a community housing wait list"
2014A07721 O00012
2014A07721 O00013    NOT APPROVED
2014A07721 O00014
2014A07721 O00015  This  legislation  would  require the Office of Mental Health (OMH) to
2014A07721 O00016establish a statewide community  housing  wait  list  for  persons  with
2014A07721 O00017mental illnesses.
2014A07721 O00018
2014A07721 O00019  I  share the sponsors'' goal of integrating persons with mental illness
2014A07721 O00020into the community. New York is a national leader in the development  of
2014A07721 O00021community  housing  for persons with serious mental illness. The 2014-15
2014A07721 O00022State Budget for OMH includes over $400 million in funding for  housing,
2014A07721 O00023including over $200 million for supported housing.
2014A07721 O00024
2014A07721 O00025  This  bill  would duplicate OMH''s "Single Point of Access" initiative,
2014A07721 O00026which works at the local level to match individuals with mental  illness
2014A07721 O00027with  health  and  housing  services  programs.  The  bill''s approach is
2014A07721 O00028flawed: it would not provide a  clear,  in-time,  localized  picture  of
2014A07721 O00029available  housing resources and, therefore, would not effectively match
2014A07721 O00030persons with appropriate housing vacancies as they arise.  In  addition,
2014A07721 O00031the  bill  does  not  provide clear enforcement authority for OMH in the
2014A07721 O00032event providers or counties fail to submit information on a timely basis
2014A07721 O00033or  submit  erroneous  information.  Without  appropriate  controls  and
2014A07721 O00034enforcement,  the accuracy of a statewide wait list could not be guaran-
2014A07721 O00035teed.
2014A07721 O00036
2014A07721 O00037  This bill would also have an unbudgeted fiscal impact  on  the  State.
2014A07721 O00038For these reasons, I cannot approve this bill.
2014A07721 O00039
2014A07721 O00040  The bill is disapproved.                    (signed) ANDREW M. CUOMO
2014A07721 O00041                              __________
2014A07721 O00000.SO DOC VETO0494        *END*    A7721           VETO                 2014',
    manual_fix = true,
    manual_fix_notes = 'fixed duplicate approval text for approval 11'
WHERE fragment_id = 'SOBI.D141125.T044946.TXT-0-BILL';