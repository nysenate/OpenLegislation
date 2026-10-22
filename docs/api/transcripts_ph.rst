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
