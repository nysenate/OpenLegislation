**Senate Agenda API**
=====================

Committee Agendas are a collection of legislative meetings that take place to discuss bills and ultimately pass them to the floor.

The committee agendas for a given week are contained within a collection known as the *weekly agenda*. The weekly agenda
starts at 1 for the first week, and increments by 1 for every subsequent week. The numbering of the agendas resets at the
start of every year. Therefore a weekly agenda can be uniquely identified using the *Agenda No* and a *Year*.

Get a single agenda
-------------------

**Usage**

Retrieve an agenda by year and agenda no
::
    (GET) /api/3/agendas/{year}/{agendaNo}

**Examples**

Request agenda 2 of session year 2017
::
   /api/3/agendas/2017/2

.. note:: Agenda responses have a lot of data. They contain their own data as well as bill data. The example, agenda 2 has over 10,000 lines.



Get a list of agendas for a single year
---------------------------------------

**Usage**

Returns a list of agenda ids in ascending order that occur in the given year
::
    (GET) /api/3/agendas/{year}

**Examples**

::
   /api/3/agendas/2017



Get a specific committee with an agenda
---------------------------------------

**Usage**

Retrieve a specific committee within an agenda
::
    /api/3/agendas/{year}/{agendaNo}/{committeeName}

**Examples**

Request the Rules committee agenda 2 of session year 2017
::
   /api/3/agendas/2017/2/Rules

Get a list of committee meeting times in a time range
-------------------------------------------------

**Usage**

Retrieve a list of committee meetings between from and to date/time, ordered by earliest first
::
    (GET) /api/3/agendas/meetings/{from datetime}/{to datetime}

**Examples**

Retrieve a list of committee meetings between Jan 1st, 2017 to Feb 1st, 2017
::
   /api/3/agendas/meetings/2017-01-01/2017-02-01



Search an Agenda for a term
-------------------------------------------------

**Usage**

Search agendas across all years for a term
::
    (GET) /api/3/agendas/search

.. note:: The param term is required and the value of this parameter is whatever you are looking to find

**Optional Params**

+-----------+-----------------------------------------------------------------------------------------+
| Parameter | Values                                                                                  |
+===========+=========================================================================================+
| full      | 'true', 'false'                                                                         |
+-----------+-----------------------------------------------------------------------------------------+

**Examples**

Search agendas for the term crime
::
   /api/3/agendas/search?term=crime&full=false



Search all agendas in a calendar year for a term
-------------------------------------------------

**Usage**

Search agendas across all years for a term
::
    (GET) /api/3/agendas/{year}/search

.. note:: The param term is required and the value of this parameter is whatever you are looking to find

**Optional Params**

+-----------+-----------------------------------------------------------------------------------------+
| Parameter | Values                                                                                  |
+===========+=========================================================================================+
| full      | 'true', 'false'                                                                         |
+-----------+-----------------------------------------------------------------------------------------+
| limit     | Limit the number of results                                                             |
+-----------+-----------------------------------------------------------------------------------------+
| offset    | Start the results from offset                                                           |
+-----------+-----------------------------------------------------------------------------------------+
+-----------+-----------------------------------------------------------------------------------------+
| type      | Determine whether to use process date time or published date time                        |
+-----------+-----------------------------------------------------------------------------------------+


**Examples**

Search agendas for a specific term
::
   /api/3/agendas/2017/search?term=crime&full=false&limit=10&offset=5



Get updated agenda Id's
-----------------------

.. warning:: The following api calls work (by default) on the process date time. If we reprocess our data, the timestamps listed may not produce the same results. You can use the published date time to get updates from an earlier time

**Usage**

Return a list of agenda ids that have changed during a specified date/time range
::
    (GET) /api/3/agendas/updates/

.. note:: This api call gets updates in the last 7 days

**Usage**

Get updates from the time specified to now
::
    (GET) /api/3/agendas/updates/{from}

**Examples**

Get updates for all of the 2017 session
::
    /api/3/agendas/updates/2017-01-01/

**Usage**

Get updates for a specified time range
::
    (GET) /api/3/agendas/updates/{from}/{to}

**Examples**

Get updates for all of January 2018
::
    /api/3/agendas/updates/2018-01-01/2018-01-31



Get updated agenda digests
--------------------------

**Usage**

This api call gets all digests for an agenda in a calendar year
::
    (GET) /api/3/agendas/{year}/{agendaNo}/updates

**Examples**

Get all updates for agenda 15 in 2017
::
    /api/3/agendas/2017/15/updates

**Usage**

Get a list of agenda digests in a specific time range of the calendar year to now
::
    (GET) /api/3/agendas/{year}/{agendaNo}/updates/{from}

.. note:: Where 'from' is an ISO date time

**Examples**

Get updates for agenda 12 in 2017 from Feb 1st, 2017 to now
::
    /api/3/agendas/2017/12/updates/2017-02-01

**Usage**

Return agenda digests that have changed during a specified date/time range
::
    (GET) /api/3/agendas/{year}/{agendaNo}/updates/{from}/{to}

.. note:: Where 'from' and 'to' are ISO date times

**Examples**

Get updates for agenda 12 in 2017 from Jan 1st, 2017 to Dec 1st, 2017
::
    /api/3/agendas/2017/12/updates/2017-01-01/2017-12-01?type=published