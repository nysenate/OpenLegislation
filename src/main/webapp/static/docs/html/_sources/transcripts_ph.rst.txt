**Hearing Transcripts API**
==================================

.. note:: Most Assembly hearings are not available at this time.

Get a single Hearing
---------------------------

**Usage**

Retrieve hearing by id or filename
::
    (GET) /api/3/hearings/{id}
    (GET) /api/3/hearings/{filename}

**Examples**

Request hearing 5
::
    /api/3/hearings/5

Request hearing "10-29-13 NYsenate_Flanagan_Education_FINAL.txt"
::
    /api/3/hearings/10-29-13 NYsenate_Flanagan_Education_FINAL.txt


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

Retrieve hearing pdf by id or filename
::
    (GET) /api/3/hearings/{id}.pdf
    (GET) /api/3/hearings/{filename}.pdf

**Examples**

Request hearing 09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt
::
    /api/3/hearings/09-12-13 NYSsenate_DeFrancisco_Buffalo_FINAL.txt.pdf


-----

Get a list of hearings
-----------------------------

**Usage**

List hearings within a year
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
| full      | boolean            | Set to true to see the full hearing responses.  |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

List 50 hearings from 2014
::
    /api/3/hearings/2014?limit=50

List 50 complete hearings starting from 51
::
    /api/3/hearings/2014?limit=50%offset=51&full=true

List 10 complete hearings sorted by increasing date
::
    /api/3/hearings/2014?limit=10&full=true&sort=dateTime:ASC

**Response**

.. code-block:: javascript

    {
      "success" : true,                               // True if request was fine.
      "message" : "",
      "responseType" : "hearing-id list",
      "total" : 451,                                  // Total hearings in the listing
      "offsetStart" : 1,                              // Offset value
      "offsetEnd" : 0,                                // To paginate, set query param offset = {offsetEnd + 1}
      "limit" : 10,                                   // Max number of results to show
      "result" : {
        "items": [{ ... }],                           // Array of hearing responses
        "size": 10
      }
    }
