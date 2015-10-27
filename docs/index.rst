**Open Legislation v2.0 API Docs**
==================================

What is this?
-------------

`Open Legislation`_ is a web service that delivers legislative information from the New York State Senate and Assembly
to the public in near-real time. It is used to serve legislative data for `nysenate.gov`_ and other various services.

Legislative data is sent via the Legislative Bill Drafting Commission (LBDC) in a raw file format. The data is
ingested and cleaned up internally by Open Leg and made available for consumption through a REST API.

This documentation explains how to utilize the REST API to retrieve bills, resolutions, laws, committee agendas, and more.
You will need to sign up for a free API key from the Open Legislation homepage in order to use this service.

The code is open source and available on `Github`_.

.. _Open Legislation: http://legislation.nysenate.gov
.. _nysenate.gov:    http://www.nysenate.gov
.. _Github: http://github.com/nysenate/OpenLegislation

API Usage
---------

**Obtaining a key**

To register and obtain an API key, visit our main page `here <http://legislation.nysenate.gov>`_.

**Making requests**

All URIs listed in these docs are relative to the Open Legislation subdomain:
::
    legislation.nysenate.gov

To make an API request using your key, set the key string as the value of the 'key' request parameter in the request URL.

For example, if you wanted to get data for bill S1 of the 2015 session, your request would look something like this:
::
    legislation.nysenate.gov/api/3/bills/2015/S1?key=*your key goes here*

Legislative Content Types
-------------------------

We currently offer the following types of data:

   - Bills and Resolutions
   - Committee Agendas
   - Senate Calendars
   - NYS Laws
   - Senate Floor and Public Hearing Transcripts
   - Committees
   - Members

Terminology
-----------

First let's define some common legislative terminology

:Session Year:
    A legislative session year in New York State comprises of two years, with the first year being an odd numbered year.

:Bill:
    A bill is passed with the intention of amending or creating a specific portion of NYS Law.

:Resolution:
    A resolution does not necessarily impact law and are often introduced to provide honorable mentions.

:Calendar:
    A legislative calendar is a snapshot of the status of bills that are under discussion at a particular time.
    A calendar will typically contain a floor calendar and an active list, along with supplemental floor calendars and active lists.

:Floor Calendar:
    The floor calendar is a listing of all bills that are under discussion (on the floor).  A bill must be read on the
    senate floor a minimum of three times to be eligible for a vote, and the floor calendar will indicate the number of
    readings for each bill at its time of publication.

:Active List:
    An active list is a listing of bills that are scheduled for discussion during a single senate session.

:Committee:
    Committees are groups of senators that are focused on particular areas of law.  Bills must be approved by a committee
    in order to be reported to the senate floor.

:Uni-Bill:
    A uni bill is a bill that is sent through both chambers concurrently
