**Senate Floor Transcripts API**
================================

.. note:: Assembly transcripts are not available at this time.

Get a single Transcript
-----------------------

**Usage**

Retrieve transcript by dateTime and sessionType. The sessionType is only required if two transcripts have the same dateTime::

    GET /api/3/transcripts/{dateTime}
    GET /api/3/transcripts/{dateTime}/{sessionType}

**Examples**

Request transcript 2014-09-03T09:00::

    GET /api/3/transcripts/2014-09-03T09:00

**Response**

Full Transcript Response

.. code-block:: javascript

    {
      "success" : true,                                         // Indicates if a transcript was found.
      "message" : "Data for transcript 2014-09-03T09:00:00",    // Response description.
      "responseType" : "transcript",                            // Response data type.
      "result" : {
        "dateTime" : "2014-09-03T09:00",                        // The date and time of the session.
        "sessionType" : "REGULAR SESSION",                      // Session type
        "location" : "ALBANY, NEW YORK",                        // Location of senate session.
        "text" : "5100\n\n 1     NEW YORK STATE SE.."           // The text of the transcript.
        }
    }

Request the extraordinary session 3 transcript on 2010-12-31T10:00::

    GET /api/3/transcripts/2010-12-31T10:00/EXTRAORDINARY SESSION III

**Response**

Full Transcript Response

.. code-block:: javascript

    {
      "success" : true,
      "message" : "Data for transcript 2010-12-31T10:00",
      "responseType" : "transcript",
      "result" : {
        "dateTime" : "2010-12-31T10:00",
        "sessionType" : "EXTRAORDINARY SESSION III",
        "location" : "ALBANY, NEW YORK",
        "text" : "..."
      }
    }

Get a transcript pdf
--------------------

**Usage**

Retrieve transcript pdf by dateTime. The sessionType is only required if two transcripts have the same dateTime::

    GET /api/3/transcripts/{dateTime}.pdf
    GET /api/3/transcripts/{dateTime}/{sessionType}.pdf

**Examples**

Request transcript 2014-09-03T09:00::

    GET /api/3/transcripts/2014-09-03T09:00

Get a list of transcripts
-------------------------

**Usage**

List transcripts within a year::

    GET /api/3/transcripts/{year}

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return                            |
+-----------+--------------------+--------------------------------------------------------+
| offset    | > 1                | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+
| full      | boolean            | Set to true to see the full transcript responses.      |
|           |                    |                                                        |
|           |                    | **Deprecated:** Scheduled for removal December 1,      |
|           |                    | 2026. See the :ref:`API migration notes                |
|           |                    | <transcript-collection-full-migration>`.               |
+-----------+--------------------+--------------------------------------------------------+
| summary   | boolean            | Set to true to see each transcript's details, without  |
|           |                    | its text. Defaults to false, which returns only the    |
|           |                    | dateTime and sessionType of each transcript.           |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List transcripts from 2014::

    GET /api/3/transcripts/2014

List the details of transcripts from 2014, starting from result 26::

    GET /api/3/transcripts/2014?summary=true&offset=26

List the details of transcripts from 2014 sorted by increasing date::

    GET /api/3/transcripts/2014?summary=true&sort=dateTime:ASC

**Response**

.. code-block:: javascript

    {
      "success" : true,                               // True if request was fine.
      "message" : "",
      "responseType" : "transcript-id list",
      "total" : 167,                                  // Total transcripts in the listing
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 25,                               // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 25,                                   // Max number of results to show
      "result" : {
        "items": [{ ... }],                           // Transcript ids, or transcript details if summary=true
        "size": 25
      }
    }

.. _transcript-updates:

Get transcript updates
----------------------

To identify which transcripts have been added or changed within a given time period you can use the transcript updates api.
To keep a copy of transcripts current with it, see :ref:`Keeping Up with Changes<keeping-up>`.

**Usage**

List of transcripts updated within the past seven days::

    GET /api/3/transcripts/updates

List of transcripts updated since the given date/time::

    GET /api/3/transcripts/updates/{fromDateTime}

List of transcripts updated during the given date/time range::

    GET /api/3/transcripts/updates/{fromDateTime}/{toDateTime}

With no ``toDateTime`` the range has no end.

Results are always ordered by the date/time OpenLeg last added or changed each transcript, oldest first.
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

Transcripts updated since January 1, 2025, 10 at a time::

    GET /api/3/transcripts/updates/2025-01-01?limit=10

**Response**

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "transcript-update-token list",
      "total" : 134,
      "offsetStart" : 1,
      "offsetEnd" : 10,
      "limit" : 10,
      "result" : {
        "items" : [
          {
            "transcriptId" : {                  // The id of the transcript
              "dateTime" : "2025-01-08T12:14",
              "sessionType" : "Regular Session"   // Session types can contain spaces
            },
            "updateDateTime" : "2025-01-13T08:51:04.087967"   // When OpenLeg last added or changed the transcript
          },
          ... (truncated)
        ],
        "size" : 10
      }
    }
