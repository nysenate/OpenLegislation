**Senate Committee API**
=================

In OpenLegislation, committee data is processed in a way that tracks the membership of each committee over time.
Committees are stored as committee versions, each of which represent a time period where there were no changes in
committee membership for a specific committee.  Committee versions contain a list of members in the committee,
information on where and when the committee meets, and the dates when the committee version began and was reformed.

Get a current committee version
-------------------------------

.. note:: Assembly committee data is currently not sent to us at this time. chamber must be 'senate'.

**Usage**
::
   /api/3/committees/{session}/{chamber}/{committeeName}

**Example**
::
   /api/3/committees/2013/senate/Cultural%20Affairs,%20Tourism,%20Parks%20and%20Recreation
   (Get the current version of the Cultural Affairs, Tourism, Parks and Recreation committee)

**Sample Response**

See `committee version response`_

Get a committee version at specific time
----------------------------------------

This request will return a committee version (if one exists) corresponding to the given committee that was created
before or on the given time and reformed after the given time.

**Usage**
::
   /api/3/committees/{session}/{chamber}/{committeeName}/{ISODateTime}

**Example**
::
   /api/3/committees/2013/senate/Finance/2014-03-01T09:30:00
   (Get the codes committee at 9:30 AM on March 1st, 2014)

.. _`committee version response`:

**Sample Response**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "committee",
      "result" : {
        "chamber" : "SENATE",     // The chamber of this committee (SENATE or ASSEMBLY)
        "name" : "Finance",       // The name of this committee
        "sessionYear" : 2013,     // The session year of this committee version
        "referenceDate" : "2014-02-28T11:25:44",  // The date and time that this configuration
                                                  //  of committee members was reported
        "reformed" : "2014-03-03T17:09:09",       // The date and time that this configuration
                                                  //  of committee members was replaced
                                                  // If null, then this is the current committee version
        "location" : "Room 124 CAP",  // The location where this committee meets
        "meetDay" : "TUESDAY",        // The day of the week that this committee meets
        "meetTime" : "11:00",         // The time of day that this committee meets
        "meetAltWeek" : false,        // True if this committee meets on alternate weeks
        "meetAltWeekText" : "",       // Describes the committee's alternate schedule if applicable
        "committeeMembers" : {        // A listing of members in this committee
          "items" : [
            {
              "memberId" : 376,                   // An arbitrary unique id used to identify members
                                                  //  in our database
              "shortName" : "DEFRANCISCO",        // The committee member's lbdc shortname
              "sessionYear" : 2013,               // The session year this member was active in
              "fullName" : "John A. DeFrancisco", // The member's full name
              "districtCode" : 50,                // A code designating the member's home district
              "sequenceNo" : 1,                   // The member's position in the list of committee members
              "title" : "CHAIR_PERSON"            // The member's role in the committee
                                                //  Valid roles include:
                                                //  "CHAIR_PERSON", "VICE_CHAIR", and "MEMBER"
            },
            ...
          ],
          "size" : 24
        }
      }
    }

Get committee history
---------------------

**Usage**
::
   /api/3/committees/{session}/{chamber}/{committeeName}/history

.. _comm-history-params:

**Optional Params**

+-----------+---------+---------------------------------------------------------------------------------------------+
| Parameter | Values  | Description                                                                                 |
+===========+=========+=============================================================================================+
| full      | boolean | (default false) Set to true to see the full committee responses instead of the summaries.   |
+-----------+---------+---------------------------------------------------------------------------------------------+
| limit     | string  | (default 50) Limit the number of results                                                    |
+-----------+---------+---------------------------------------------------------------------------------------------+
| offset    | number  | (default 1) Start results from offset                                                       |
+-----------+---------+---------------------------------------------------------------------------------------------+
| order     | string  | (default 'DESC') Determines the order the committee responses.  Sorted by created date.     |
+-----------+---------+---------------------------------------------------------------------------------------------+

**Example**
::
   /api/3/committees/2013/senate/Aging/history  (Get 2013 history for the aging committee)
   /api/3/committees/2013/senate/Aging/history?limit=1&order=ASC&full=true
   (Get the first version of the Aging committee from 2013)

Get all current committees
--------------------------

**Usage**
::
   /api/3/committees/{session}/{chamber}

**Optional Params**

+-----------+---------+---------------------------------------------------------------------------------------------+
| Parameter | Values  | Description                                                                                 |
+===========+=========+=============================================================================================+
| full      | boolean | (default false) Set to true to get full committee responses instead of summaries.           |
+-----------+---------+---------------------------------------------------------------------------------------------+

**Example**
::
   /api/3/committees/2013/senate?&full=true
   (Get full responses for all current senate committees for session 2013)

Search for committees
---------------------

Read our :doc:`search API docs<search_api>` for info on how to construct search terms.
The committee search index is comprised of full committee responses
(i.e. the json response returned when requesting a single committee)
so query and sort strings will be based on that response structure.

**Usage**

Search across all session years
::
   (GET) /api/3/committees/search?term=YOUR_TERM

Search within a session year
::
   (GET) /api/3/committees/{sessionYear}/search?term=YOUR_TERM


**Required Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| term      | string             | :ref:`ElasticSearch query string<search-term>`         |
+-----------+--------------------+--------------------------------------------------------+

**Optional Params**

+--------------+--------------------+---------------------------------------------------------------------------------+
| Parameter    | Values             | Description                                                                     |
+==============+====================+=================================================================================+
| sort         | string             | :ref:`ElasticSearch sort string<search-sort>`                                   |
+--------------+--------------------+---------------------------------------------------------------------------------+
| current      | boolean            | (default true) Searches only current committee versions if true                 |
+--------------+--------------------+---------------------------------------------------------------------------------+

Also takes all :ref:`committee history optional params<comm-history-params>` with the exception of order