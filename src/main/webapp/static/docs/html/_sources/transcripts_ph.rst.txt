**Public Hearing Transcripts API**
==================================

.. note:: Assembly public hearings are not available at this time.

Get a single Public Hearing
---------------------------

**Usage**

Retrieve public hearing by filename
::
    (GET) /api/3/hearings/{filename}

**Examples**

Request public hearing "10-29-13 NYsenate_Flanagan_Education_FINAL.txt"
::
    /api/3/hearings/10-29-13 NYsenate_Flanagan_Education_FINAL.txt

**Response**

Full Public Hearing Response

.. code-block:: javascript

    {
      "success" : true,                               // Indicates if a public hearing was found.
      "message" : "Data for public hearing 10-29..",  // Response description
      "responseType" : "hearing",                     // Response data type
      "result" : {
        "filename" : "10-29-13 NYsenate_Flanagan..",  // Filename of public hearing transcript
        "title" : "PUBLIC HEARING THE REGENTS RE..",  // Title of public hearing
        "date" : "2013-10-29",                        // Date of public hearing
        "address" : "Senate Hearing Room\n250 Br..",  // Address of public hearing
        "committees" : [ {                            // List of committees/task forces/other groups holding the hearing
          "name" : "EDUCATION",                       // Name of committee/task force/other group
          "chamber" : "SENATE"                        // Chamber of committee
        } ],
        "startTime" : "10:00",                        // Time the public hearing started
        "endTime" : "14:00",                          // Time the public hearing ended
        "text" : "\n\n\n       1     BEFORE THE NE.." // The text of the public hearing
        }
    }

Get a public hearing pdf
------------------------

**Usage**

Retrieve public hearing pdf by filename
::
    (GET) /api/3/hearings/{filename}.pdf

**Examples**

Request public hearing 09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt
::
    /api/3/hearings/09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt.pdf


-----

Get a list of public hearings
-----------------------------

**Usage**

List public hearings within a year
::
    (GET) /api/3/hearings/{year}

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return                            |
+-----------+--------------------+--------------------------------------------------------+
| offset    | > 1                | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+
| full      | boolean            | Set to true to see the full public hearing responses.  |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List 50 public hearings from 2014
::
    /api/3/hearings/2014?limit=50

List 50 complete public hearings starting from 51
::
    /api/3/hearings/2014?limit=50%offset=51&full=true

List 10 complete public hearings sorted by increasing date
::
    /api/3/hearings/2014?limit=10&full=true&sort=dateTime:ASC

**Response**

.. code-block:: javascript

    {
      "success" : true,                               // True if request was fine.
      "message" : "",
      "responseType" : "hearing-id list",
      "total" : 451,                                  // Total public hearings in the listing
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 0,                                // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 10,                                   // Max number of results to show
      "result" : {
        "items": [{ ... }],                           // Array of public hearing responses
        "size": 10
      }
    }
