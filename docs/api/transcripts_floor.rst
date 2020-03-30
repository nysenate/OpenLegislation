**Senate Floor Transcripts API**
================================

.. note:: Assembly transcripts are not available at this time.

Get a single Transcript
-----------------------

**Usage**

Retrieve transcript by dateTime
::
    (GET) /api/3/transcripts/{dateTime}

**Examples**

Request transcript 2014-09-03T09:00
::
    /api/3/transcripts/2014-09-03T09:00

**Response**

Full Transcript Response

.. code-block:: javascript

    {
      "success" : true,                                         // Indicates if a transcript was found.
      "message" : "Data for transcript 2014-09-03T09:00:00",    // Response description.
      "responseType" : "transcript",                            // Response data type.
      "result" : {
        "sessionDateTime" : "2014-09-03T09:00",                 // Filename of transcript.
        "sessionType" : "REGULAR SESSION",                      // Session type
        "dateTime" : "2014-09-03T09:00:00                       // Date Time of senate session.
        "location" : "ALBANY, NEW YORK",                        // Location of senate session.
        "text" : "5100\n\n 1     NEW YORK STATE SE.."           // The text of the transcript.
        }
    }

Get a transcript pdf
--------------------

**Usage**

Retrieve transcript pdf by dateTime
::
    (GET) /api/3/transcripts/{dateTime}.pdf

**Examples**

Request transcript 2014-09-03T09:00
::
    /api/3/transcripts/2014-09-03T09:00

Get a list of transcripts
-------------------------

**Usage**

List transcripts within a year
::
    (GET) /api/3/transcripts/{year}

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return                            |
+-----------+--------------------+--------------------------------------------------------+
| offset    | > 1                | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+
| full      | boolean            | Set to true to see the full transcript responses.      |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List 50 transcripts from 2014
::
    /api/3/transcripts/2014?limit=50
List 50 complete transcripts starting from 51
::
    /api/3/transcripts/2014?limit=50%offset=51&full=true
List 10 complete transcripts sorted by increasing date
::
    /api/3/transcripts/2014?limit=10&full=true

**Response**

.. code-block:: javascript

    {
      "success" : true,                               // True if request was fine.
      "message" : "",
      "responseType" : "transcript list",
      "total" : 167,                                  // Total transcripts in the listing
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 0,                                // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 10,                                   // Max number of results to show
      "result" : {
        "items": [{ ... }],                           // Array of transcript responses
        "size": 10
      }
    }
