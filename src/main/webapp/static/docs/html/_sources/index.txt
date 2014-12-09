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

-----------
Terminology
-----------

Session Year
   A legislative session year in New York State comprises of two years, with the first year being an odd numbered year.

Bill
   A bill is passed with the intention of amending or creating a specific portion of NYS Law.

Resolution
   A resolution does not necessarily impact law and are often introduced to provide honorable mentions.


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

Indices and tables
==================

* :ref:`genindex`
* :ref:`modindex`
* :ref:`search`

