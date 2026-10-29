**Senate Agenda API**
=====================

Committee Agendas are a collection of legislative meetings that take place to discuss bills and ultimately pass them to the floor.

The committee agendas for a given week are contained within a collection known as the *weekly agenda*. The weekly agenda
starts at 1 for the first week, and increments by 1 for every subsequent week. The numbering of the agendas resets at the
start of every year. Therefore a weekly agenda can be uniquely identified using the *Agenda No* and a *Year*.

Get a single agenda
-------------------

**Usage**

Retrieve an agenda by year and agenda no::

    GET /api/3/agendas/{year}/{agendaNo}

**Examples**

Request agenda 2 of session year 2017::

   GET /api/3/agendas/2017/2

.. note:: Agenda responses have a lot of data. They contain their own data as well as bill data. The example, agenda 2 has over 10,000 lines.



Get a list of agendas for a single year
---------------------------------------

**Usage**

List the weekly agendas in a year, in ascending order of agenda number::

    GET /api/3/agendas/{year}

Each item is an agenda summary: its id, week, publish date and time, the number of addenda and committees, and the
number of bills considered and voted on. Request a single agenda for its committees and bills.

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return (default 25)               |
+-----------+--------------------+--------------------------------------------------------+
| offset    | >= 1               | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List the agendas of 2025::

   GET /api/3/agendas/2025

List 2025's agendas 10 at a time, starting from the 11th::

   GET /api/3/agendas/2025?limit=10&offset=11

**Response**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "agenda-summary list",
      "total" : 21,                                   // Total agendas in the year
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 21,                               // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 25,                                   // Max number of results to show
      "result" : {
        "items" : [ {
          "id" : {
            "number" : 1,
            "year" : 2025
          },
          "weekOf" : "2025-01-13",
          "publishedDateTime" : "2025-01-13T15:21:28.027862",
          "totalAddendum" : 1,
          "totalBillsConsidered" : 98,
          "totalBillsVotedOn" : 0,
          "totalCommittees" : 1
        }, ... ],
        "size" : 21
      }
    }



Get a specific committee with an agenda
---------------------------------------

**Usage**

Retrieve a specific committee within an agenda::

    GET /api/3/agendas/{year}/{agendaNo}/{committeeName}

**Examples**

Request the Rules committee agenda 2 of session year 2017::

   GET /api/3/agendas/2017/2/Rules

Get a list of committee meeting times in a time range
-----------------------------------------------------

**Usage**

Retrieve a list of committee meetings between from and to date/time, ordered by earliest first::

    GET /api/3/agendas/meetings/{from datetime}/{to datetime}

**Examples**

Retrieve a list of committee meetings between Jan 1st, 2017 to Feb 1st, 2017::

   GET /api/3/agendas/meetings/2017-01-01/2017-02-01



Search an Agenda for a term
-------------------------------------------------

**Usage**

Search agendas across all years for a term::

    GET /api/3/agendas/search

.. note:: The param term is required and the value of this parameter is whatever you are looking to find

**Optional Params**

+-----------+-----------------------------------------------------------------------------------------+
| Parameter | Values                                                                                  |
+===========+=========================================================================================+
| full      | 'true', 'false'                                                                         |
|           |                                                                                         |
|           | **Deprecated:** Scheduled for removal December 1, 2026. See the :ref:`API migration     |
|           | notes <agenda-search-full-migration>`.                                                  |
+-----------+-----------------------------------------------------------------------------------------+
| limit     | Limit the number of results                                                             |
+-----------+-----------------------------------------------------------------------------------------+
| offset    | Start the results from offset                                                           |
+-----------+-----------------------------------------------------------------------------------------+
| sort      | A lucene sort string                                                                    |
+-----------+-----------------------------------------------------------------------------------------+

**Examples**

Search agendas for the term crime::

   GET /api/3/agendas/search?term=crime



Search all agendas in a calendar year for a term
-------------------------------------------------

**Usage**

Search agendas across all years for a term::

    GET /api/3/agendas/{year}/search

.. note:: The param term is required and the value of this parameter is whatever you are looking to find

**Optional Params**

+-----------+-----------------------------------------------------------------------------------------+
| Parameter | Values                                                                                  |
+===========+=========================================================================================+
| full      | 'true', 'false'                                                                         |
|           |                                                                                         |
|           | **Deprecated:** Scheduled for removal December 1, 2026. See the :ref:`API migration     |
|           | notes <agenda-search-full-migration>`.                                                  |
+-----------+-----------------------------------------------------------------------------------------+
| limit     | Limit the number of results                                                             |
+-----------+-----------------------------------------------------------------------------------------+
| offset    | Start the results from offset                                                           |
+-----------+-----------------------------------------------------------------------------------------+
| sort      | A lucene sort string                                                                    |
+-----------+-----------------------------------------------------------------------------------------+

**Examples**

Search 2017's agendas for a specific term, 10 results at a time, starting from the 11th::

   GET /api/3/agendas/2017/search?term=crime&limit=10&offset=11



Get updated agenda Id's
-----------------------

.. warning:: The following api calls work (by default) on the process date time. If we reprocess our data, the timestamps listed may not produce the same results. You can use the published date time to get updates from an earlier time

**Usage**

List of agendas updated since the start of the day seven days ago::

    GET /api/3/agendas/updates

List of agendas updated since the given date/time::

    GET /api/3/agendas/updates/{from}

List of agendas updated during the given date/time range::

    GET /api/3/agendas/updates/{from}/{to}

.. note:: Give ``from`` and ``to`` as ISO 8601 date-times in New York local time, without an offset.
   For example December 10, 2014, 1:30:02 PM is ``2014-12-10T13:30:02``. A date alone, such as ``2014-12-10``,
   means midnight. An offset such as ``Z`` or ``-05:00`` is ignored, not converted.
   The range includes ``from`` and excludes ``to``. The response reports the range it used, with ``toDateTime`` set to the last
   microsecond included, e.g. ``2014-12-10T13:30:01.999999``. When the range runs to now, it can carry up to nine
   fractional digits. With no ``to``, the range runs to now.

**Optional Params**

+-----------+----------------------+--------------------------------------------------------+
| Parameter | Values               | Description                                            |
+===========+======================+========================================================+
| type      | (processed|published)| Which update date to filter and order by: the date     |
|           |                      | OpenLeg processed the data, or the date it was         |
|           |                      | published. Default processed.                          |
+-----------+----------------------+--------------------------------------------------------+
| detail    | boolean              | Set to true to see update digests instead of tokens    |
+-----------+----------------------+--------------------------------------------------------+
| order     | string (asc|desc)    | Order the results by update date/time. Default asc,    |
|           |                      | oldest first.                                          |
+-----------+----------------------+--------------------------------------------------------+
| limit     | 1 - 1000             | Number of results to return. Default 50.               |
+-----------+----------------------+--------------------------------------------------------+
| offset    | >= 1                 | Result number to start from. Default 1.                |
+-----------+----------------------+--------------------------------------------------------+

**Examples**

Get the first 50 updates since January 1, 2017::

    GET /api/3/agendas/updates/2017-01-01

Get updates for all of January 2025, 10 at a time::

    GET /api/3/agendas/updates/2025-01-01/2025-02-01?limit=10

.. _agenda-update-token-response:

**Response (detail=false)**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-token list",
      "total" : 4,
      "offsetStart" : 1,
      "offsetEnd" : 4,
      "limit" : 10,
      "result" : {
        "items" : [
          {
            "id" : {                // The number and year of the updated agenda
              "number" : 1,
              "year" : 2025
            },
            "contentType" : "AGENDA",
            // The id of the reference that triggered the update
            "sourceId" : "2025-01-21-16.34.54.269983_SENAGENV_RULES.XML-1-AGENDA_VOTE",
            // The publish date time of the reference source
            "sourceDateTime" : "2025-01-21T16:34:54.269983",
            // The date and time that the reference was processed
            "processedDateTime" : "2025-01-21T16:41:08.597759"
          },
          ... (truncated)
        ],
        "size" : 4
      },
      "fromDateTime" : "2025-01-01T00:00:00",       // First instant in the range
      "toDateTime" : "2025-01-31T23:59:59.999999"   // Last instant in the range
    }

Get updated agenda digests
--------------------------

**Usage**

Get updates for an agenda, from the earliest::

    GET /api/3/agendas/{year}/{agendaNo}/updates

Get updates for an agenda since the given date/time::

    GET /api/3/agendas/{year}/{agendaNo}/updates/{from}

Get updates for an agenda during the given date/time range::

    GET /api/3/agendas/{year}/{agendaNo}/updates/{from}/{to}

Dates follow the same rules as the agenda updates API above. Each item is an update digest with its updated fields.

**Optional Params**

+-----------+----------------------+--------------------------------------------------------+
| Parameter | Values               | Description                                            |
+===========+======================+========================================================+
| type      | (processed|published)| Which update date to filter and order by. Default      |
|           |                      | processed.                                             |
+-----------+----------------------+--------------------------------------------------------+
| order     | string (asc|desc)    | Order the results by update date/time. Default asc,    |
|           |                      | oldest first.                                          |
+-----------+----------------------+--------------------------------------------------------+
| limit     | 1 - 1000             | Number of results to return. Default 50.               |
+-----------+----------------------+--------------------------------------------------------+
| offset    | >= 1                 | Result number to start from. Default 1.                |
+-----------+----------------------+--------------------------------------------------------+

**Examples**

Get the first 50 updates for agenda 15 in 2017::

    GET /api/3/agendas/2017/15/updates

Get updates for agenda 12 in 2017 from Feb 1st, 2017 to now::

    GET /api/3/agendas/2017/12/updates/2017-02-01

Get updates for agenda 12 in 2017 published from Jan 1st, 2017 up to Dec 1st, 2017::

    GET /api/3/agendas/2017/12/updates/2017-01-01/2017-12-01?type=published

Get updates for agenda 1 in 2025, 10 at a time::

    GET /api/3/agendas/2025/1/updates?limit=10

.. _agenda-update-digest-response:

**Response**

The agenda updates API returns the same items with ``detail=true``.

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-digest list",
      "total" : 9,
      "offsetStart" : 1,
      "offsetEnd" : 9,
      "limit" : 10,
      "result" : {
        "items" : [
          {
            "id" : {
              "number" : 1,
              "year" : 2025
            },
            "contentType" : "AGENDA",
            "sourceId" : "2025-01-13-15.21.28.027862_SENAGEN_00001.XML-1-AGENDA",
            "sourceDateTime" : "2025-01-13T15:21:28.027862",
            "processedDateTime" : "2025-01-13T15:26:02.400415",
            "action" : "Insert",                // The update action that was performed (Insert|Update|Delete)
            "scope" : "Agenda",                 // Which part of the agenda was updated
            "fields" : {                        // Updated fields
              "Created Date Time" : "2025-01-13 15:26:02.400415",
              "Published Date Time" : "2025-01-13 15:21:28.027862"
            },
            "fieldCount" : 2
          },
          ... (truncated)
        ],
        "size" : 9
      },
      "fromDateTime" : "1970-01-01T00:00:00",        // The earliest, with no "from"
      "toDateTime" : "2026-10-29T12:52:03.683239885"   // Just before now, with no "to"
    }
