.. Open Legislation documentation master file, created by
   sphinx-quickstart on Mon Dec  8 14:50:44 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

Open Legislation v2.0 API Docs
==============================

-------------
What is this?
-------------

`Open Legislation`_ is a web service that delivers legislative information from the New York State Senate and Assembly
to the public in near-real time. It is used to serve legislative data for `nysenate.gov`_ and other various services.

This documentation explains how to utilize the REST API to retrieve bills, resolutions, laws, committee agendas, and more.
You will need to sign up for a `free API key`_ in order to use this service.

The code is open source and available on `Github`_.

.. _Open Legislation: http://openleg-dev.nysenate.gov
.. _free API key: http://openleg-dev.nysenate.gov
.. _nysenate.gov:    http://www.nysenate.gov
.. _Github: http://github.com/nysenate/OpenLegislation

-------------------------
Legislative Content Types
-------------------------

We currently offer the following types of data:

   - Bills and Resolutions
   - Committee Agendas
   - Senate Calendars
   - NYS Laws
   - Senate Floor and Public Hearing Transcripts
   - Committees

-----------
Terminology
-----------

Session Year
   A legislative session year in New York State comprises of two years, with the first year being an odd numbered year.

Bill
   A bill is passed with the intention of amending or creating a specific portion of NYS Law.

Resolution
   A resolution does not necessarily impact law and are often introduced to provide honorable mentions.

Calendar
   A legislative calendar is a snapshot of the status of bills that are under discussion at a particular time.
   A calendar will typically contain a floor calendar and an active list, along with supplemental floor calendars and active lists.

Floor Calendar
   The floor calendar is a listing of all bills that are under discussion (on the floor).  A bill must be read on the
   senate floor a minimum of three times to be eligible for a vote, and the floor calendar will indicate the number of
   readings for each bill at its time of publication.

Active List
   An active list is a listing of bills that are scheduled for discussion during a single senate session.

Committee
   Committees are groups of senators that are focused on particular areas of law.  Bills must be approved by a committee
   in order to be reported to the senate floor.

---------------------
Bills and Resolutions
---------------------

**Get a single bill.**

Usage
::
   /api/3/bills/{sessionYear}/{printNo}

   Optional Params:
   summary (boolean) - Show a summary of the bill instead of the full content
   detail (boolean) - Show extra details

Examples
::
   /api/3/bills/2013/S2180
   /api/3/bills/2013/A450?summary=true

**Get listings of all bills during a session year**

Usage:
::
   /api/3/bills/{sessionYear}

   Optional Params:
   limit (number) - Number of results to return
   offset (number)  - Result number to start from
   full (boolean) - Set to true to see the full bill responses instead of the summaries.
   sort (string) - Sort by any field from the response, e.g. someFieldName:ASC

Examples:
::
   /api/3/bills/2013?limit=100 (List 100 bills from 2013)
   /api/3/bills/2013?limit=100&offset=101&full=true (List 100 complete bills starting from 101)
   /api/3/bills/2013?sort=publishedDateTime:ASC (Sort by increasing published date)
   /api/3/bills/2013?sort=status.actionDate:ASC (Sort by increasing status action date, (default))

----------------
Senate Calendars
----------------

**Get a single calendar.**

Usage:
::
   Full calendar:
      /api/3/calendars/{year}/{calendarNumber}
   Floor calendar:
      /api/3/calendars/{year}/{calendarNumber}/floor
   Supplemental calendar:
      /api/3/calendars/{year}/{calendarNumber}/{versionCharacter}
   Active list:
      /api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}

   Optional Params:
   full (boolean) - Set to true to see the full calendar response instead of a summary.
                     (default true)

Examples:
::
   /api/3/calendars/2014/54               (Get calendar 54 of 2014)
   /api/3/calendars/2014/54?&full=false   (Get a summary of calendar 54)
   /api/3/calendars/2014/54/0             (Get the base active list for calendar 54)
   /api/3/calendars/2014/54/floor         (Get the floor calendar for calendar 54)
   /api/3/calendars/2014/54/B             (Get supplemental calendar B of calendar 54)

**Get a listing of calendars for a given year**

Usage:
::
   Full calendars:
      /api/3/calendars/{year}
   Supplemental/Floor calendars:
      /api/3/calendars/{year}/supplemental
   Active lists:
      /api/3/calendars/{year}/activelist

   Optional Params:
   full (boolean) - Set to true to see the full calendar responses instead of the summaries.
         (default false)
   order ('ASC'|'DESC') - Determines the order the calendar responses.  Responses are ordered by
         calendar number and then either sequenceNumber or version if they are active lists or
         supplementals respectively.  (default 'ASC')
   limit (number) - Limit the number of results (default 100)
   offset (number) - Start results from offset (default 1)

Examples:
::
   /api/3/calendars/2014?full=true                       (Get all calendar data from 2014)
   /api/3/calendars/2014?limit=1&order=DESC              (Get the latest calendar from 2014)
   /api/3/calendars/2014/activelist?limit=5              (Get the first 5 active lists of 2014)
   /api/3/calendars/2014/supplemental?limit=5&offset=5   (Get the second 5 supplementals of 2014)

----------
Committees
----------

**Get the current version of a single committee for a given session year**

Usage:
::
   /api/3/committees/{session}/{chamber}/{committeeName}
Example:
::
   /api/3/committees/2013/senate/Cultural%20Affairs,%20Tourism,%20Parks%20and%20Recreation

**Get a committee version active at a given time for a given session year**

Usage:
::
   /api/3/committees/{session}/{chamber}/{committeeName}/{ISODateTime}
Example:
::
   /api/3/committees/2013/senate/Codes/2014-03-01T09:30:00
   (Get the codes committee at 9:30 AM on March 1st, 2014)

**Get the history for a single committee for a given session year**

Usage:
::
   /api/3/committees/{session}/{chamber}/{committeeName}/history

   Optional Params:
   full (boolean) - Set to true to get full committee responses. (default false)
   order ('ASC'|'DESC') - Determines the order the committee responses.
         Sorted by date.  (default 'DESC')
   limit (number) - Limit the number of results (default 50)
   offset (number) - Start results from offset (default 1)
Example:
::
   /api/3/committees/2013/senate/Aging/history  (Get 2013 history for the aging committee)
   /api/3/committees/2013/senate/Aging/history?limit=1&order=ASC&full=true
   (Get the first version of the Aging committee from 2013)

**Get a listing of current committees for a given chamber**

Usage:
::
   /api/3/committees/{session}/{chamber}

   Optional Params:
   full (boolean) - Set to true to see the full committee responses (default false)
Example:
::
   /api/3/committees/2013/senate?&full=true  (Get full responses for all current senate committees for session 2013

Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

