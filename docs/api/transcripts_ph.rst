**Hearing Transcripts API**
==================================

.. note:: Most Assembly hearings are not available at this time.

Get a single Hearing
---------------------------

**Usage**

Retrieve hearing by id or filename::

    GET /api/3/hearings/{id}
    GET /api/3/hearings/{filename}

**Examples**

Request hearing 5::

    GET /api/3/hearings/5

Request hearing "10-29-13 NYsenate_Flanagan_Education_FINAL.txt"::

    GET /api/3/hearings/10-29-13 NYsenate_Flanagan_Education_FINAL.txt


**Response**

Full Hearing Response

.. code-block:: javascript

    {
      "success" : true,                               // Indicates if a hearing was found.
      "message" : "Data for hearing 10-29..",  // Response description
      "responseType" : "hearing",                     // Response data type
      "result" : {
        "id": 179
        "filename" : "10-29-13 NYsenate_Flanagan..",  // Filename of hearing transcript
        "title" : "PUBLIC HEARING THE REGENTS RE..",  // Title of hearing
        "date" : "2013-10-29",                        // Date of hearing
        "address" : "Senate Hearing Room\n250 Br..",  // Address of hearing
        "committees" : [ {                            // List of committees/task forces/other groups holding the hearing
          "name" : "EDUCATION",                       // Name of committee/task force/other group
          "type" : "COMMITTEE"                        // Type of group, committee/task force/legislative commission/etc
          "chamber" : "SENATE"                        // Chamber of committee
        } ],
        "startTime" : "10:00",                        // Time the hearing started
        "endTime" : "14:00",                          // Time the hearing ended
        "text" : "\n\n\n       1     BEFORE THE NE.." // The text of the hearing
        }
    }

Get a hearing pdf
------------------------

**Usage**

Retrieve hearing pdf by id or filename::

    GET /api/3/hearings/{id}.pdf
    GET /api/3/hearings/{filename}.pdf

**Examples**

Request hearing 09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt::

    GET /api/3/hearings/09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt.pdf


-----

Get a list of hearings
-----------------------------

**Usage**

List hearings within a year::

    GET /api/3/hearings/{year}

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return                            |
+-----------+--------------------+--------------------------------------------------------+
| offset    | > 1                | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+
| full      | boolean            | Set to true to see the full hearing responses.         |
|           |                    |                                                        |
|           |                    | **Deprecated:** Scheduled for removal December 1,      |
|           |                    | 2026. See the :ref:`API migration notes                |
|           |                    | <hearing-collection-full-migration>`.                  |
+-----------+--------------------+--------------------------------------------------------+
| summary   | boolean            | Set to true to see each hearing's details, without its |
|           |                    | text. Defaults to false, which returns only the id and |
|           |                    | filename of each hearing.                              |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List hearings from 2014::

    GET /api/3/hearings/2014

List the details of hearings from 2014, starting from result 26::

    GET /api/3/hearings/2014?summary=true&offset=26

List the details of hearings from 2014 sorted by increasing date::

    GET /api/3/hearings/2014?summary=true&sort=date:ASC

**Response**

.. code-block:: javascript

    {
      "success" : true,                               // True if request was fine.
      "message" : "",
      "responseType" : "hearing-id list",
      "total" : 451,                                  // Total hearings in the listing
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 25,                               // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 25,                                   // Max number of results to show
      "result" : {
        "items": [{ ... }],                           // Hearing ids, or hearing details if summary=true
        "size": 25
      }
    }

.. _hearing-updates:

Get hearing updates
-------------------

To identify which hearings have been added or changed within a given time period you can use the hearing updates api.

**Usage**

List of hearings updated within the past seven days::

    GET /api/3/hearings/updates

List of hearings updated since the given date/time::

    GET /api/3/hearings/updates/{fromDateTime}

List of hearings updated during the given date/time range::

    GET /api/3/hearings/updates/{fromDateTime}/{toDateTime}

.. note:: Give ``fromDateTime`` and ``toDateTime`` as ISO 8601 date-times in New York local time, without an offset.
   For example December 10, 2014, 1:30:02 PM is ``2014-12-10T13:30:02``. A date alone, such as ``2014-12-10``,
   means midnight. An offset such as ``Z`` or ``-05:00`` is ignored, not converted.
   The range includes ``fromDateTime`` and excludes ``toDateTime``. With no ``toDateTime``, the range has no end.

Results are always ordered by the date/time OpenLeg last added or changed each hearing, oldest first.
Unlike the other update APIs, this one does not take ``type``, ``order`` or ``detail``, and the response
does not include ``fromDateTime`` or ``toDateTime``.

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return. Default 25.               |
+-----------+--------------------+--------------------------------------------------------+
| offset    | >= 1               | Result number to start from. Default 1.                |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

Hearings updated since January 1, 2025, 10 at a time::

    GET /api/3/hearings/updates/2025-01-01?limit=10

**Response**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "hearing-update-token list",
      "total" : 16,
      "offsetStart" : 1,
      "offsetEnd" : 10,
      "limit" : 10,
      "result" : {
        "items" : [
          {
            "hearingId" : {                     // The id and filename of the hearing
              "id" : 305,
              "filename" : "Agriculture2025.txt"
            },
            "dateTime" : "2025-03-04T12:36:02.276462"   // When OpenLeg last added or changed the hearing
          },
          ... (truncated)
        ],
        "size" : 10
      }
    }
