**Senate Calendar API**
=======================

.. note:: Assembly calendar data is currently not sent to us at this time. chamber must be 'senate'.

Get a single calendar
---------------------

**Usage**
::
   Full calendar:
      /api/3/calendars/{year}/{calendarNumber}
   Floor calendar:
      /api/3/calendars/{year}/{calendarNumber}/floor
   Supplemental calendar:
      /api/3/calendars/{year}/{calendarNumber}/{versionCharacter}
   Active list:
      /api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}

**Optional Params**

+-----------+---------+---------------------------------------------------------------------------------------------+
| Parameter | Values  | Description                                                                                 |
+===========+=========+=============================================================================================+
| full      | boolean | (default true) Set to true to get a full calendar response instead of a summary.            |
+-----------+---------+---------------------------------------------------------------------------------------------+

**Examples**
::
   /api/3/calendars/2014/54               (Get calendar 54 of 2014)
   /api/3/calendars/2014/54?&full=false   (Get a summary of calendar 54)
   /api/3/calendars/2014/54/0             (Get the base active list for calendar 54)
   /api/3/calendars/2014/54/floor         (Get the floor calendar for calendar 54)
   /api/3/calendars/2014/54/B             (Get supplemental calendar B of calendar 54)

**Sample Responses**

Full calendar:

.. code-block:: javascript

    {
        "success": true,
        "message": "",
        "responseType": "calendar",
        "result": {
            "year": 2014,                 // Year the calendar was published
            "calendarNumber": 54,         // Incremental identifier for calendars within a year
            "floorCalendar": {...},       // See supplemental/floor calendar response result
            "supplementalCalendars": {
                "items": {...},           // Map of supplemental version characters to
                                        //  supplemental calendar response results
                "size": 2
            },
            "activeLists": {
                "items": {...},           // Map of sequence numbers to active list response results
                "size": 3
            },
            "calDate": "2014-06-20"       // The date this calendar was active for
        }
    }

Supplemental/Floor calendar:

.. code-block:: javascript

    {
      "success": true,
      "message": "",
      "responseType": "calendar-floor",   // "calendar-supplemental" if the response is a supplemental
      "result": {
        "year": 2014,                             // The year the calendar was released
        "calendarNumber": 54,                     // Incremental identifier for calendars within a year
        "version": "floor",                       // The supplemental version, "floor" or
                                                  //  a single capital character
        "calDate": "2014-06-20",
        "releaseDateTime": "2014-06-20T02:01",    // The date this supplemental was released
        "entriesBySection": {                     // A listing of bills mapped to their floor status
          "items": {
            "THIRD_READING": {                    // List of bills on their third reading
              "items": [
                {                                 // Modified bill response (link below)
                  "basePrintNo": "A5625",
                  "session": 2013,
                  "printNo": "A5625A",
                  "billType": {
                    "chamber": "ASSEMBLY",
                    "desc": "Assembly",
                    "resolution": false
                  },
                  "title": "Extends the expiration of the New York state French and Indian war 250th anniversary commemoration commission until December 31, 2015",
                  "activeVersion": "A",
                  "year": 2013,
                  "publishedDateTime": "2013-03-04T14:32:46",
                  "substitutedBy": null,
                  "sponsor": {
                    "member": {
                      "memberId": 466,
                      "shortName": "ENGLEBRIGHT",
                      "sessionYear": 2013,
                      "fullName": "Steven Englebright",
                      "districtCode": 4
                    },
                    "budget": false,
                    "rules": false
                  },
                  "billCalNo": 1090,              // The calendar number that ids this bill
                                                  //  within all calendars
                  "sectionType": "THIRD_READING", // The floor status of this bill
                  "subBillInfo": {                // Bill info response for a substituted bill
                    "basePrintNo": "S7605",
                    "session": 2013,
                    "printNo": "S7605",
                    "billType": {
                      "chamber": "SENATE",
                      "desc": "Senate",
                      "resolution": false
                    },
                    "title": "Extends the expiration of the New York state French and Indian war 250th anniversary commemoration commission until December 31, 2015",
                    "activeVersion": "",
                    "year": 2014,
                    "publishedDateTime": "2014-05-15T18:17:31",
                    "substitutedBy": null,
                    "sponsor": {
                      "member": {
                        "memberId": 385,
                        "shortName": "ESPAILLAT",
                        "sessionYear": 2013,
                        "fullName": "Adriano Espaillat",
                        "districtCode": 31
                      },
                      "budget": false,
                      "rules": false
                    }
                  },
                  "billHigh": false               // Set to true if this is a high priority bill
                },
                ...
              ],
              "size": 284
            },
            "STARRED_ON_THIRD_READING": {     // Another floor status. All statuses include:
                                        // ORDER_OF_THE_FIRST_REPORT, ORDER_OF_THE_SECOND_REPORT,
                                        // ORDER_OF_THE_SPECIAL_REPORT, THIRD_READING,
                                        // THIRD_READING_FROM_SPECIAL_REPORT,
                                        // STARRED_ON_THIRD_READING
              "items": [...],
              "size": 3
            }
          },
          "size": 2
        }
      }
    }

Active List:

.. code-block:: javascript

    {
      "success": true,
      "message": "",
      "responseType": "calendar-activelist",
      "result": {
        "year": 2014,                             // The year the calendar was released
        "calendarNumber": 54,                     // Incremental identifier for calendars within a year
        "sequenceNumber": 0,                      // Indicates publish sequence of active lists
        "calDate": "2014-06-20",                  // The date this calendar was active
        "releaseDateTime": "2014-06-20T04:28:48", // The date and time this active list was released
        "notes": null,                            // Notes regarding the active list, pretty much always null
        "entries": {                              // List of bills on this active list
          "items": [
            {                                   // Modified bill response (see above link)
              "basePrintNo": "S4779",
              "session": 2013,
              "printNo": "S4779B",
              "billType": {
                "chamber": "SENATE",
                "desc": "Senate",
                "resolution": false
              },
              "title": "Relates to inheritance by children conceived after the death of a genetic parent",
              "activeVersion": "B",
              "year": 2013,
              "publishedDateTime": "2013-04-23T15:04:37",
              "substitutedBy": {
                "basePrintNo": "A7461",
                "session": 2013
              },
              "sponsor": {
                "member": {
                  "memberId": 413,
                  "shortName": "BONACIC",
                  "sessionYear": 2013,
                  "fullName": "John J. Bonacic",
                  "districtCode": 42
                },
                "budget": false,
                "rules": false
              },
              "billCalNo": 192                    // The calendar number that ids this bill
                                                //  within all calendars
            },
            ...
          ],
          "size": 31
        }
      }
    }

Get a listing of calendars
--------------------------

**Usage**
::
   Full calendars:
      /api/3/calendars/{year}
   Supplemental/Floor calendars:
      /api/3/calendars/{year}/supplemental
   Active lists:
      /api/3/calendars/{year}/activelist

.. _cal-list-params:

**Optional Params**

+-----------+---------+---------------------------------------------------------------------------------------------+
| Parameter | Values  | Description                                                                                 |
+===========+=========+=============================================================================================+
| full      | boolean | (default false) Set to true to see the full calendar responses instead of the summaries.    |
+-----------+---------+---------------------------------------------------------------------------------------------+
| limit     | string  | (default 100) Limit the number of results                                                   |
+-----------+---------+---------------------------------------------------------------------------------------------+
| offset    | number  | (default 1) Start results from offset                                                       |
+-----------+---------+---------------------------------------------------------------------------------------------+
| order     | string  | (default 'ASC') Determines the order the calendar responses.  Responses are ordered by      |
|           |         | calendar number and then either sequenceNumber or version if they are active lists or       |
|           |         | supplementals respectively.                                                                 |
+-----------+---------+---------------------------------------------------------------------------------------------+

**Examples**
::
   /api/3/calendars/2014?full=true                       (Get all calendar data from 2014)
   /api/3/calendars/2014?limit=1&order=DESC              (Get the latest calendar from 2014)
   /api/3/calendars/2014/activelist?limit=5              (Get the first 5 active lists of 2014)
   /api/3/calendars/2014/supplemental?limit=5&offset=5   (Get the second 5 supplementals of 2014)

Search for calendars
--------------------

Read our :doc:`search API docs<search_api>` for info on how to construct search terms.
The calendar search index is comprised of full calendar responses
(i.e. the json response returned when requesting a single calendar) so query and sort strings will be based on that response
structure.

**Usage**

Search across all session years
::
   (GET) /api/3/calendars/search?term=YOUR_TERM

Search within a year
::
   (GET) /api/3/calendars/{year}/search?term=YOUR_TERM


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

Also takes all :ref:`calendar listing optional params<cal-list-params>` with the exception of order

Get calendar updates
--------------------

To identify which calendars have received updates within a given time period you can use the calendar updates api.

**Usage**

List of calendars updated within the past seven days
::
    (GET) /api/3/calendars/updates

List of calendars updated after the given date/time
::
    (GET) /api/3/calendars/updates/{fromDateTime}

List of calendars updated during the given date/time range
::
    (GET) /api/3/calendars/updates/{fromDateTime}/{toDateTime}

.. note:: The 'fromDateTime' and 'toDateTime' parameters should be formatted as the ISO 8601 Date Time format.
   For example December 10, 2014, 1:30:02 PM should be inputted as 2014-12-10T13:30:02.
   The fromDateTime and toDateTime range is exclusive/inclusive respectively.

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| detail    | boolean            | Set to true to see `detailed update digests`_          |
+-----------+--------------------+--------------------------------------------------------+
| order     | string (asc|desc)  | Order the results by update date/time                  |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

Calendars that were updated between January 1st and January 20th of 2019
::
    (GET) /api/3/calendars/updates/2019-01-01T00:00:00/2019-01-20T00:00:00

.. _calendar-update-token-response:

**Response (detail=false)**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-token list",
      "total" : 4,
      "offsetStart" : 1,
      "offsetEnd" : 4,
      "limit" : 100,
      "result" : {
        "items" : [
          {
            "id" : {        // The year and calendar number of the updated calendar
              "year" : 2019,
              "calendarNumber" : 1
            },
            // The id of the reference that triggered the update
            "sourceId" : "2019-01-14-15.55.39.563595_SENCAL_00001.XML-1-CALENDAR",
            // The publish date time of the reference source
            "sourceDateTime" : "2019-01-14T15:55:39.563595",
            // The date and time that the reference was processed
            "processedDateTime" : "2019-01-14T16:01:20.389704"
          },
          ... (truncated)
        ],
        "size" : 2
      }
    }

Get specific calendar updates
-----------------------------

**Usage**

Get updates for a calendar within a datetime range
::
    (GET) /api/3/calendars/{year}/{calendarNumber}/updates/{fromDateTime}/{toDateTime}

Get all updates for a calendar
::
    (GET) /api/3/calendars/{year}/{calendarNumber}/updates

**Optional Params**

+-----------+----------------------+---------------------------------------------------------------+
| Parameter | Values               | Description                                                   |
+===========+======================+===============================================================+
| type      |(processed|published) | The type of bill update (see below for explanation)           |
+-----------+----------------------+---------------------------------------------------------------+
| order     | string (asc|desc)    | Order the results by update date/time                         |
+-----------+----------------------+---------------------------------------------------------------+
| limit     | string               | (default 100) Limit the number of results                     |
+-----------+----------------------+---------------------------------------------------------------+
| offset    | number               | (default 1) Start results from offset                         |
+-----------+----------------------+---------------------------------------------------------------+

**Examples**

Get updates for calendar 54 of 2014 that occurred between 9 AM and 5 PM on June 20th, 2014
::
    (GET) /api/3/calendars/2014/54/updates/2014-06-20T09:00:00/2014-06-20T17:00:00

.. _calendar-update-digest-response:

**Response (type=published)**

.. _`detailed update digests`:

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-digest list",
      "total" : 3,
      "offsetStart" : 1,
      "offsetEnd" : 3,
      "limit" : 0,
      "result" : {
        "items" : [
          {
            "id" : {
              "year" : 2014,
              "calendarNumber" : 54
            },
            "sourceId" : "SOBI.D140620.T153915.TXT-1-CALENDAR",
            "sourceDateTime" : "2014-06-20T15:39:15",
            "processedDateTime" : "2014-12-15T15:21:34.786472",
            "action" : "INSERT",                // The update action that was performed
            "scope" : "Calendar Active List",   // The type of sub calendar that was updated
            "fields" : {                        // Updated fields
              "publishedDateTime" : "2014-06-20 05:28:51",
              "notes" : "",
              "sequenceNo" : "0",
              "createdDateTime" : "2014-12-15 15:21:34.786472",
              "id" : "302",
              "calendarDate" : "2014-06-20",
              "releaseDateTime" : "2014-06-20 04:28:48"
            }
          },
          ... (truncated)
        ],
        "size" : 3
      }
    }

.. warning:: By default the type is set to 'processed'. As we reprocess our data periodically, it's possible this specific api call may not produce the result shown. However, the response you receive will follow the format in the example