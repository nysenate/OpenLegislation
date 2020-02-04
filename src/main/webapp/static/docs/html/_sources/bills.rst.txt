**Bills and Resolutions API**
=============================

.. note:: While bills and resolutions serve different purposes, in the context of these docs, the term 'bill' will include resolutions as well since the API request/response structure for both are identical.

----------

Get a single bill
-----------------

**Usage**

Retrieve bill by session year and print no
::
   (GET) /api/3/bills/{sessionYear}/{printNo}

**Optional Params**

+----------------+----------------------------------------------------------------------------------------------+
| Parameter      | Values                                                                                       |
+================+==============================================================================================+
| view           | 'default', 'info', 'no_fulltext', 'only_fulltext', 'with_refs', 'with_refs_no_fulltext'      |
+----------------+----------------------------------------------------------------------------------------------+
| version        | If view=only_fulltext, use the version to specify the amendment letter, e.g. version=A       |
+----------------+----------------------------------------------------------------------------------------------+
| fullTextFormat | (PLAIN or HTML) Which bill text formats will be included. Multiple formats can be requested. |
+----------------+----------------------------------------------------------------------------------------------+

View options

+------------------+----------------------------------------------------------------------------------+
| View type        | Description                                                                      |
+==================+==================================================================================+
| default          | If the view param is omitted, the default response will be as documented below.  |
+------------------+----------------------------------------------------------------------------------+
| info             | If you only need a bill summary, i.e. no full text, memo, or vote data.          |
+------------------+----------------------------------------------------------------------------------+
| no_fulltext      | Identical to the default response except the full text will be omitted.          |
+------------------+----------------------------------------------------------------------------------+
| only_fulltext    | If you only need the full text for a bill. Use the version param if needed.      |
+------------------+----------------------------------------------------------------------------------+
| with_refs        | If you need basic info views included for any related bills (e.g. same as bills).|
+------------------+----------------------------------------------------------------------------------+
|                  | with_refs_no_fulltext is the same as above, just without any full text.          |
+------------------+----------------------------------------------------------------------------------+

.. note:: Bills typically get amended and their print no gets suffixed with an amendment letter (e.g. S1234B). The bill API returns bill responses that contain every amendment version so you should just provide
          the base print no (e.g. S1234).

**Examples**

Request bill S2180 of session year 2013
::
   /api/3/bills/2013/S2180

Request summary of bill A450 of session year 2013
::
   /api/3/bills/2013/A450?view=info

.. _bill-response:

**Response**

Default Bill Response

.. code-block:: javascript

   {
      "success": true,                            // Indicates if bill was found
      "message": "Data for bill S2180-2013",      // Response description
      "responseType": "bill",                     // Response data type
      "result":
      {                                           // Actual data of bill contained in 'result'
      "basePrintNo": "S2180",                     // Print no of bill (not including amendment version)
      "session": 2013,                            // Session year bill is active in
      "printNo": "S2180",                         // Print no of bill (may include amendment version)
      "billType": {
        "chamber": "SENATE",                      // Which chamber the bill was introduced (SENATE or ASSEMBLY)
        "desc": "Senate",                         // Type of bill
        "resolution": false                       // True if this is a resolution
      },
      "title": "Provides enhanced..",             // Title of the bill
      "activeVersion": "",                        // Current amendment version ("" for initial version)
      "year": 2013,                               // Year the bill was introduced on
      "publishedDateTime": "2013-01-14T10:36:22", // Date/Time this bill was first published via LBDC
      "substitutedBy": {                          // If the bill was substituted, the bill id will be contained
        "basePrintNo": "A1989",                   // The base print no of the substituted bill
        "session": 2013                           // Session year of the substituted bill
      },
      "sponsor": {                                // Contains sponsor information
        "member": {                               // Contains sponsor member details (can be null)
          "memberId": 422,                        // Id of the sponsor
          "shortName": "GOLDEN",                  // Last name of sponsor (unique within a session year)
          "sessionYear": 2013,                    // Session year this sponsor was active in
          "fullName": "Martin J. Golden",         // Full name of sponsor
          "districtCode": 22                      // Legislative district code of this sponsor
        },
        "budget": false,                          // True if this is a budget bill
        "rules": false                            // True if this bill was sponsored by the rules committee
      },
      "summary": "Provides enhanced sentence...", // Summary of the bill
      "signed": false,                            // True if this bill has been signed or adopted (if its a resolution)
      "status": {                                 // Status Information of the bill
        "statusType": "IN_SENATE_COMM",           // Status Code
        "statusDesc": "In Senate Committee",      // Description of status code
        "actionDate": "2014-06-20",               // Date when this status was updated
        "committeeName": "RULES",                 // If the bill is in a committee, the committee name is shown here
        "billCalNo": null                         // If the bill is on the floor, the calendar number of the bill is shown here.
      },
      "milestones": {                             // The milestones list contains a list of statuses (same structure
        "items": [                                // as the 'status' object above.
          {
            "statusType": "IN_SENATE_COMM",
            "statusDesc": "In Senate Committee",
            "actionDate": "2014-06-20",
            "committeeName": "RULES",
            "billCalNo": null
          }
        ],
        "size": 1
      },
      "programInfo": {                            // Some bills are introduced as part of a program by the governor or an agency
        "name": "Department of Motor Vehicles",   // The name of the program/agency
        "sequenceNo": 2                           // The position of this bill within that program/agency list
      },
      // ---- Bill summary view ends here --- //
      "amendments": {                              // Contains info specific to an amendment (base version is "")
        "items": {
          "": {                                   // Map of Amendment versions
            "basePrintNo": "S2180",               // Bill print no/session details duplicated here
            "session": 2013,
            "printNo": "S2180",
            "version": "",                        // Amendment version
            "publishDate": "2013-01-14",          // Date this amendment was published
            "sameAs": {                           // List of bill that are identical to this within the same session year
               "items": [{
                  "basePrintNo": "A2098",
                  "session": 2013,
                  "printNo": "A2098",
                  "version": ""
               }],
               "size": 1
            },
            "memo": "BILL NUMBER:S2180",        // The sponsor's memo which explains the bill. Only available for senate bills.
            "lawSection": "Penal Law",            // The primary section of law this bill impacts.
            "lawCode": "Add Â§265.18, Pen L",     // A code that states the actions being taken on specific portions of law.
            "actClause": "AN ACT to amend the..", // An Act to Clause
            "fullText": "...",                    // Full text of the bill amendment
            "coSponsors": {                       // List of co sponsors
              "items": [
               {
                "memberId": 391,
                "shortName": "AVELLA",
                "sessionYear": 2013,
                "fullName": "Tony Avella",
                "districtCode": 11
               }
              ],
              "size": 1
            },
            "multiSponsors": {                    // List of multi sponsors (only for assembly bills)
              "items": [],
              "size": 0
            },
            "uniBill": false,                     // Indicates if this is a uni bill
            "stricken": false                     // Indicates if this amendment has been stricken
          }
        },
        "size": 1
      }
      "votes": {                                  // Votes will be stored here if there are any
         "items": [
          {
            "version": "",                        // Amendment version vote was taken on
            "voteType": "COMMITTEE",              // Type of vote (COMMITTEE or FLOOR)
            "voteDate": "2013-04-22",             // Date the vote was taken
            "committee": {                        // If it was a committee vote, the committee will be shown here
              "chamber": "SENATE",
              "name": "Rules"
            },
            "memberVotes": {                      // The actual votes are shown here
              "items": {
                "EXC": {                          // Map by vote codes
                   "items": [                     // List of members that voted with this code
                     {
                       "memberId": 424,
                       "shortName": "HANNON",
                       "sessionYear": 2013
                     }
                   ],
                    "size": 1
                },
                "AYEWR": {..},                    // Other votes truncated here for brevity
                "NAY": {..},
                "AYE": {..}
              },
              "size": 4
            }
          },
        ],
        "size": 1
      },
      "vetoMessages" : {                          // If a veto memo from the governor was sent, it will show up here
          "items" : [ {
            "billId" : {                          // Bill id replicated here
              "basePrintNo" : "A10049",
              "session" : 2013,
              "printNo" : "A10049",
              "version" : ""
            },
            "year" : 2014,                        // Year this veto was sent
            "vetoNumber" : 511,                   // Veto number (unique to a single year)
            "memoText" : ".....",                 // The content of the veto memo
            "vetoType" : "STANDARD",              // The type of veto
            "chapter" : 0,                        // The chapter (if applicable)
            "billPage" : 0,                       // For line vetos, a page number may be specified
            "lineStart" : 0,
            "lineEnd" : 0,
            "signer" : "ANDREW M. CUOMO",         // Governor Name
            "signedDate" : null                   // Date Signed (if present)
          } ],
          "size" : 1
      },
      "approvalMessage": {                        // Approval message from the governor (if present)
         "billId": {                              // Bill id the approval message was sent for
            "basePrintNo": "S6830",
            "session": 2013,
            "printNo": "S6830A",
            "version": "A"
         },
         "year": 2014,                             // Year this approval message was sent
         "approvalNumber": 11,                     // Approval number (unique to a single year)
         "chapter": 476,                           // The chapter (if applicable)
         "signer": "ANDREW M. CUOMO",              // Governor Name
         "text": "...."                            // Text of the approval message
      },
      "additionalSponsors": {                      // If there are additional sponsors, the members will be listed here
         "items": [],
         "size": 0
      },
      "pastCommittees": {                          // Lists out all the committees this bill was in
         "items": [
            {
            "chamber": "ASSEMBLY",                 // Committee Chamber
            "name": "GOVERNMENTAL OPERATIONS",     // Name of committee
            "sessionYear": 2013,                   // Session year it was referenced by the committee
            "referenceDate": "2014-06-10T00:00"    // Date it was referenced by the committee
            }],
         "size": 1
      },
      "actions": {                                 // The actions that have occurred on a bill
         "items": [
         {
            "billId": {
               "basePrintNo": "S6830",
               "session": 2013,
               "printNo": "S6830",
               "version": ""                       // Specifies which amendment version of the bill the action affects
            },
            "date": "2014-03-17",                  // Date of the action
            "chamber": "SENATE",                   // Chamber this action occurred in
            "sequenceNo": 1,                       // Number used to order the actions sequentially
            "text": "REFERRED TO INVESTIGATIONS.." // The text describing the action
         },
         "size": 1
      },
      "previousVersions": {                        // Lists the previous versions of this bill from prior session years.
         "items": [
            {
            "basePrintNo": "A1989",                // Bill id of the previous bill
            "session": 2013,
            "printNo": "A1989",
            "version": ""
            }
         ],
         "size": 1
      },
      "committeeAgendas": {                        // If this bill was on a committee agenda, they will be referenced here
         "items": [
         {
           "agendaId": {                           // Id of the agenda
             "number": 2,
             "year": 2013
           },
           "committeeId": {                        // Id of the committee
             "chamber": "SENATE",
             "name": "Health"
           }
         }],
         "size": 1
      },
      "calendars": {                               // If the bill was on a senate calendar, the calendars will be
         "items": [                                // referenced here
            {
            "year": 2013,                          // Calendar year
            "calendarNumber": 4                    // Calendar number
            }
         ],
         "size": 1
      }
   }

If **view** is set to 'info', the above response would be truncated after the 'programInfo' block.

If **view** is set to 'with_refs', the default response will be returned with the following data appended:

.. code-block:: javascript

   "billInfoRefs": {                               // Any bills that were referenced (e.g. same as, previous versions)
     "items": {                                    // will be mapped here using the basePrintNo-sessionYear as the key.
       "A2098-2013": {
          // 'Summary' response for this bill
          // hidden here for brevity
       }
      }
     "size": 1
   }

---------

Get PDF of bill text
--------------------

If you just need a pdf of the latest full text of the bill, you can make the following request:
::
    (GET) /api/3/bills/{sessionYear}/{printNo}.pdf

If the bill is found, a PDF will be generated with the full text of the bill.

-------

Get a list of bills
-------------------

**Usage**

List bills within a session year
::
   (GET) /api/3/bills/{sessionYear}

.. _`bill listing params`:

**Optional Params**

+----------------+--------------------+--------------------------------------------------------+
| Parameter      | Values             | Description                                            |
+================+====================+========================================================+
| limit          | 1 - 1000           | Number of results to return                            |
+----------------+--------------------+--------------------------------------------------------+
| offset         | >= 1               | Result number to start from                            |
+----------------+--------------------+--------------------------------------------------------+
| full           | boolean            | Set to true to see the full bill responses.            |
+----------------+--------------------+--------------------------------------------------------+
| idsOnly        | boolean            | Set to true to see only the printNo and session        |
|                |                    | for each bill.  (overrides 'full' parameter)           |
+----------------+--------------------+--------------------------------------------------------+
| sort           | string             | Sort by any field from the response.                   |
+----------------+--------------------+--------------------------------------------------------+
| fullTextFormat | (PLAIN or HTML)    | Which bill text formats will be included.              |
|                |                    | Multiple formats can be requested.                     |
+----------------+--------------------+--------------------------------------------------------+

**Default Sort Order**

By default, (i.e. no sort param was included in the request)
the results will be in ascending order by the bill's published date time (sort=publishedDateTime:DESC)

**Examples**

List 100 bills from 2013
::
   /api/3/bills/2013?limit=100

List 100 complete bills starting from 101
::
   /api/3/bills/2013?limit=100&offset=101&full=true

Sort by increasing published date
::
   /api/3/bills/2013?sort=publishedDateTime:ASC

Sort by increasing status action date, (default)
::
   /api/3/bills/2013?sort=status.actionDate:ASC

**Response**

.. code-block:: javascript

   {
      "success": true,                     // True if the request was fine
      "message": "",
      "responseType": "bill-info list",
      "total": 25568,                      // Total bills in the listing
      "offsetStart": 1,                    // Offset value
      "offsetEnd": 50,                     // To paginate, set query param offset={offsetEnd + 1}
      "limit": 50,                         // Max number of results shown
      "result": {
        "items": [{ ... }],                // Array of bill responses (either summary or full view)
        "size": 50
      }
   }

-------

Search for bills
----------------

Read our :doc:`search API docs<search_api>` for info on how to construct search terms. The bill search index is comprised of full bill responses
(i.e. the json response returned when requesting a single bill) so query and sort strings will be based on that response
structure.


**Usage**

Search across all session years
::
   (GET) /api/3/bills/search?term=YOUR_TERM

Search within a session year
::
   (GET) /api/3/bills/{sessionYear}/search?term=YOUR_TERM


**Required Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| term      | string             | ElasticSearch query string                             |
+-----------+--------------------+--------------------------------------------------------+

**Optional Params**

Same as the `bill listing params`_.

**Examples**

.. warning:: If you are querying a field that is heavily nested (like the amendment specific fields), prefix the field with a \\*. This is a wildcard expression. E.g   ?term=\\*memo:'Some phrase'

Search for a general term (matches against any data field)
::
    (GET) /api/3/bills/search?term=Gun Control

Search for 2013 'resolutions'
::
    (GET) /api/3/bills/2013/search?term=billType.resolution:true

Search for all bills and resolutions sponsored by a Senator, ordered by most recent status update
::
    (GET) /api/3/bills/search?term=sponsor.member.shortName:BRESLIN&sort=status.actionDate:DESC

Search for full text containing the phrase 'Marriage Equality'. Note the use of the \\* prefix to match full texts regardless of amendment version
::
    (GET) /api/3/bills/search?term=\*.fullText:"Marriage Equality"

Search for bills that were published between a certain date range, ordered by increasing published date
::
    (GET) /api/3/bills/2013/search?term=publishedDateTime:[2014-01-01 TO 2014-01-02]&sort=publishedDateTime:ASC

-------

Get bill updates
----------------

To identify which bills have received updates within a given time period you can use the bill updates api.


.. warning::
    There are two types of updates, 'processed' and 'published'.
    Processed refers to the date that OpenLeg processed the data which is useful if you are trying to stay synchronized with OpenLeg.
    Published refers to the date during which data was intended to be published.
    This can differ from the processed date because OpenLeg can periodically reprocess it's data to fix issues.
    By default the type is set to 'processed'.

**Usage**

List of bills updated during the given date/time range
::
    /api/3/bills/updates/{fromDateTime}/{toDateTime}

List of bills updated since the given date/time
::
    /api/3/bills/updates/{fromDateTime}

.. note:: The 'fromDateTime' and 'toDateTime' parameters should be formatted as the ISO 8601 Date Time format.
   For example December 10, 2014, 1:30:02 PM should be inputted as 2014-12-10T13:30:02.
   The fromDateTime and toDateTime range is exclusive/inclusive respectively.

**Optional Params**

+----------------+----------------------+--------------------------------------------------------+
| Parameter      | Values               | Description                                            |
+================+======================+========================================================+
| type           | (processed|published)| The type of bill update (see below for explanation)    |
+----------------+----------------------+--------------------------------------------------------+
| detail         | boolean              | Set to true to see `detailed update digests`_          |
+----------------+----------------------+--------------------------------------------------------+
| filter         | string               | Filter by update type. See `update filters`_           |
+----------------+----------------------+--------------------------------------------------------+
| order          | string (asc|desc)    | Order the results by update date/time                  |
+----------------+----------------------+--------------------------------------------------------+
| summary        | boolean              | Include a bill info response per item                  |
+----------------+----------------------+--------------------------------------------------------+
| fullBill       | boolean              | Include a bill info response per item                  |
+----------------+----------------------+--------------------------------------------------------+
| fullTextFormat | (PLAIN or HTML)      | Which bill text formats will be included               |
|                |                      | if full bills are requested.                           |
|                |                      | Multiple formats can be requested.                     |
+----------------+----------------------+--------------------------------------------------------+

.. warning:: By default the type is set to 'processed'. Ensure you have the right type in the api request so you receive the results you are looking for

**Examples**

Bills that were updated between February 13, 2019 8:00:00AM and February 13, 2019 at 10:55:48AM
::
    /api/3/bills/updates/2019-02-13T08:00:00/2019-02-13T10:55:48

.. _bill-update-token-response:

**Response (detail = false)**

.. code-block:: javascript

    {
        success: true,
        message: "",
        responseType: "update-token list",
        total: 74,
        offsetStart: 1,
        offsetEnd: 50,
        limit: 50,
        "result": {
            "items": [
                {
                   id: {
                        basePrintNo: "S1826",
                        session: 2019,
                        basePrintNoStr: "S1826-2019"
                    },
                    contentType: "BILL",
                    sourceId: "2019-02-13-09.01.14.643609_LDSPON_S01826.XML-1-LDSPON",
                    sourceDateTime: "2019-02-13T09:01:14.643609",
                    processedDateTime: "2019-02-13T09:06:09.796845"
                },
                ... (truncated)
    }


.. warning:: By default the type is set to 'processed'. As we reprocess our data periodically, it's possible this specific api call may not produce the result shown. However, the response you receive will follow the format in the example

.. _`update filters`:

You can filter the results of the API by specifying a specific type of update you are interested in. For example you
may only want to know which bills have had status updates, or which bills had full text changes.

Update Filters:

+-----------------+----------------------------------+
| Field           |  Description                     |
+=================+==================================+
| ACT_CLAUSE      | The enacting clause              |
+-----------------+----------------------------------+
| ACTION          | Bill Actions                     |
+-----------------+----------------------------------+
| ACTIVE_VERSION  | Active amendment version         |
+-----------------+----------------------------------+
| APPROVAL        | Approval Memos                   |
+-----------------+----------------------------------+
| COSPONSOR       | Co/sponsor changes               |
+-----------------+----------------------------------+
| FULLTEXT        | Bill full text                   |
+-----------------+----------------------------------+
| LAW             | Law code and primary sections    |
+-----------------+----------------------------------+
| MEMO            | Sponsor memos                    |
+-----------------+----------------------------------+
| MULTISPONSOR    | Multi-sponsor changes            |
+-----------------+----------------------------------+
| SPONSOR         | Sponsor changes                  |
+-----------------+----------------------------------+
| STATUS          | Bill status updates              |
+-----------------+----------------------------------+
| STATUS_CODE     | Bill status 'code' updates       |
+-----------------+----------------------------------+
| SUMMARY         | Bill summary                     |
+-----------------+----------------------------------+
| TITLE           | Bill title                       |
+-----------------+----------------------------------+
| VETO            | Veto messages                    |
+-----------------+----------------------------------+
| VOTE            | Bill votes                       |
+-----------------+----------------------------------+

**Examples**

Get a list of bills that have had status changes between January 1, 2014 12 AM and January 5, 2014 2 PM
::
    (GET) /api/3/bills/updates/2014-01-01T00:00:00/2014-01-05T14:00:00?filter=status&order=desc

.. _`detailed update digests`:

To view the actual updates that have occurred on a bill use the following API

**Usage**

All updates on a specific bill
::
    /api/3/bills/{sessionYear}/{printNo}/updates/

Updates on a specific bill from a given date/time.
::
    /api/3/bills/{sessionYear}/{printNo}/updates/{fromDateTime}/

Updates on a specific bill during a given date/time range.
::
    /api/3/bills/{sessionYear}/{printNo}/updates/{fromDateTime}/{toDateTime}

**Example**

Updates for S1234-2013 between December 1, 2014 and December 2, 2014
::
    /api/3/bills/2013/S1234/updates/2014-12-01T00:00:00/2014-12-02T00:00:00

.. _bill-update-digest-response:

**Response**

Sample response:

.. code-block:: javascript

    {
        "success": true,
        "message": "",
        "responseType": "update-digest list",
        "total": 23,
        "offsetStart": 1,
        "offsetEnd": 23,
        "limit": 50,
        "result": {
        "items": [
            {
            "id": {
                "basePrintNo": "S1234",
                "session": 2013
            },
            "sourceId": "SOBI.D121220.T160535.TXT-0-BILL",  // The source file that made the change
            "sourceDateTime": "2012-12-20T16:05:35",        // The date of the source file
            "processedDateTime": "2014-12-13T13:40:08.564879",
            "action": "INSERT",                              // Database operation
            "scope": "Bill",                                 // Type of data modified
            "fields": {                                      // Database fields that were updated
                "summary": "",
                "statusDate": "2013-01-09",
                "publishedDateTime": "2012-12-20 16:05:35",
                "committeeChamber": "senate",
                "programInfo": null,
                "subBillPrintNo": null,
                "createdDateTime": "2014-12-13 13:40:08.564879",
                "title": "Creates the office of the taxpayer advocate",
                "programInfoNum": null,
                "billCalNo": null,
                "activeYear": "2013",
                "committeeName": "INVESTIGATIONS AND GOVERNMENT OPERATIONS",
                "activeVersion": " ",
                "status": "IN_SENATE_COMM"
            }
        },
        ... (truncated)

.. warning:: By default the type is set to 'processed'. As we reprocess our data periodically, it's possible this specific api call may not produce the result shown. However, the response you receive will follow the format in the example