.. Open Legislation documentation master file, created by
   sphinx-quickstart on Mon Dec  8 14:50:44 2014.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.


**Open Legislation v2.0 API Docs**
==================================

**What is this?**
=================

`Open Legislation`_ is a web service that delivers legislative information from the New York State Senate and Assembly
to the public in near-real time. It is used to serve legislative data for `nysenate.gov`_ and other various services.

Legislative data is sent via the Legislative Bill Drafting Commission (LBDC) in a raw file format. The data is
ingested and cleaned up internally by Open Leg and made available for consumption through a REST API.

This documentation explains how to utilize the REST API to retrieve bills, resolutions, laws, committee agendas, and more.
You will need to sign up for a `free API key`_ in order to use this service.

The code is open source and available on `Github`_.

.. _Open Legislation: http://openleg-dev.nysenate.gov
.. _free API key: http://openleg-dev.nysenate.gov
.. _nysenate.gov:    http://www.nysenate.gov
.. _Github: http://github.com/nysenate/OpenLegislation

**Legislative Content Types**
=============================

We currently offer the following types of data:

   - Bills and Resolutions
   - Committee Agendas
   - Senate Calendars
   - NYS Laws
   - Senate Floor and Public Hearing Transcripts
   - Committees

**Terminology**
===============

First let's define some common legislative terminology

:Session Year:  A legislative session year in New York State comprises of two years, with the first year being an odd numbered year.

:Bill: A bill is passed with the intention of amending or creating a specific portion of NYS Law.

:Resolution: A resolution does not necessarily impact law and are often introduced to provide honorable mentions.

:Calendar: A legislative calendar is a snapshot of the status of bills that are under discussion at a particular time.
           A calendar will typically contain a floor calendar and an active list, along with supplemental floor calendars and active lists.

:Floor Calendar: The floor calendar is a listing of all bills that are under discussion (on the floor).  A bill must be read on the
                 senate floor a minimum of three times to be eligible for a vote, and the floor calendar will indicate the number of
                 readings for each bill at its time of publication.

:Active List: An active list is a listing of bills that are scheduled for discussion during a single senate session.

:Committee: Committees are groups of senators that are focused on particular areas of law.  Bills must be approved by a committee
            in order to be reported to the senate floor.

:Uni-Bill: A uni bill is a bill that is sent through both chambers concurrently


**Bills and Resolutions API**
=============================

.. note:: While bills and resolutions serve different purposes, in the context of these docs, the term 'bill' will include
          resolutions as well since the API requests and responses for both are identical.

----------

Get a single bill
-----------------

**Usage**

Retrieve bill by session year and print no
::
   (GET) /api/3/bills/{sessionYear}/{printNo}

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| summary   | boolean            | Show a summary of the bill instead of the full content |
+-----------+--------------------+--------------------------------------------------------+
| detail    | boolean            | Show extra details (overrides 'summary')               |
+-----------+--------------------+--------------------------------------------------------+

.. note:: Bills typically get amended and their print no gets suffixed with an amendment letter (e.g. S1234B)
          The bill API returns bill responses that contain every amendment version so you should just provide
          the base print no (e.g. S1234).

**Examples**

Request bill S2180 of session year 2013
::
   /api/3/bills/2013/S2180
Request summary of bill A450 of session year 2013
::
   /api/3/bills/2013/A450?summary=true

.. _`bill response`:

**Response**

Full Bill Response

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

.. note:: If **summary** is set to true, the above response would be truncated after the 'programInfo' block.

If **detail** is set to true, the following content will also be present in the response:

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

-----

Get a list of bills
-------------------

**Usage**

List bills within a session year
::
   (GET) /api/3/bills/{sessionYear}

.. _`bill listing params`:

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| limit     | 1 - 1000           | Number of results to return                            |
+-----------+--------------------+--------------------------------------------------------+
| offset    | > 1                | Result number to start from                            |
+-----------+--------------------+--------------------------------------------------------+
| full      | boolean            | Set to true to see the full bill responses.            |
+-----------+--------------------+--------------------------------------------------------+
| sort      | string             | Sort by any field from the response.                   |
+-----------+--------------------+--------------------------------------------------------+

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

--------

Search for bills
----------------

Read this [insert link] for info on how to construct search terms. The bill search index is comprised of full bill responses
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


Get bill updates
----------------

To identify which bills have received updates within a given time period you can use the bill updates api.

**Usage**

List of bills updated after the given date/time
::
    /api/3/bills/updates/{fromDateTime}/

List of bills updated during the given date/time range
::
    /api/3/bills/updates/{fromDateTime}/{toDateTime}

.. note:: The fromDateTime and toDateTime should be formatted as the ISO Date Time format.
          For example December 10, 2014, 1:30:02 PM should be inputted as 2014-12-10T13:30:02

**Optional Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| detail    | boolean            | Set to true to see `detailed update digests`_          |
+-----------+--------------------+--------------------------------------------------------+

**Examples**

Bills that were updated between December 1, 2014 and December 2, 2014
::
    /api/3/bills/updates/2014-12-01T00:00:00/2014-12-02T00:00:00

**Response (detail = false)**

.. code-block:: javascript

    {
        "success": true,
        "message": "",
        "responseType": "bill-update-token list",
        "total": 2423,
        "offsetStart": 1,
        "offsetEnd": 100,
        "limit": 100,
        "result": {
        "items": [
          {
            "billId": {                                    // Bill Id for the bill that got updated
                "basePrintNo": "S7867",
                "session": 2011
            },
            "lastUpdatedOn": "2014-12-03T15:37:30.677921"  // When this bill was last updated
                                                           // during the given date range
          },
          {
            "billId": {
                "basePrintNo": "S4530",
                "session": 2011
            },
            "lastUpdatedOn": "2014-12-03T15:37:30.818888"
          }
        ],
        "size": 2
    }

.. _`detailed update digests`:

To view the actual updates that have occurred on a bill use the following API

**Usage**

All updates on a specific bill
::
    /api/3/bills/{sessionYear}/{printNo}/updates/

Updates on a specific bill from a given date/time.
::
    /api/3/bills/{sessionYear}/{printNo}/updates/{fromDateTime}/

Updates on a specific bil during a given date/time range.
::
    /api/3/bills/{sessionYear}/{printNo}/updates/{fromDateTime}/{toDateTime}

**Example**

Updates for S1234-2013 between December 1, 2014 and December 2, 2014
::
    /api/3/bills/2013/S1234/updates/2014-12-01T00:00:00/2014-12-02T00:00:00

**Response**

Sample response:

.. code-block:: javascript

    {
        "success": true,
        "message": "",
        "responseType": "bill-update-digest list",
        "total": 19,
        "offsetStart": 1,
        "offsetEnd": 19,
        "limit": 0,
        "result": {
            "items": [
            {
                "action": "INSERT",                      // Type of action (INSERT/UPDATE/DELETE)
                "scope": "Bill",                         // Data type affected
                "updates": {                             // Raw output of internal change log, varies depending on the
                                                         // changes made
                    "summary": "",
                    "active_version": " ",
                    "committee_chamber": "senate",
                    "status_date": "2013-01-09",
                    "program_info_num": null,
                    "title": "Creates the office of the taxpayer advocate",
                    "active_year": "2013",
                    "sub_bill_print_no": null,
                    "created_date_time": "2014-12-08 19:58:01.772303",
                    "committee_name": "INVESTIGATIONS AND GOVERNMENT OPERATIONS",
                    "program_info": null,
                    "published_date_time": "2012-12-20 16:05:35",
                    "bill_cal_no": null,
                    "status": "IN_SENATE_COMM"
                },
                "updatedOn": "2014-12-08T19:58:01.772303",        // When this change was recorded
                "sourceDataId": "SOBI.D121220.T160535.TXT-0-BILL" // Id of the originating source data file (internal)
            },
            ....
        }
    }

**Senate Calendar API**
=======================

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

   Optional Params:
   full (boolean) - Set to true to see the full calendar response instead of a summary.
                     (default true)

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
        success: true,
        message: "",
        responseType: "calendar",
        result: {
            year: 2014,                 // Year the calendar was published
            calendarNumber: 54,         // Incremental identifier for calendars within a year
            floorCalendar: {...},       // See supplemental/floor calendar response result
            supplementalCalendars: {
                items: {...},           // Map of supplemental version characters to
                                        //  supplemental calendar response results
                size: 2
            },
            activeLists: {
                items: {...},           // Map of sequence numbers to active list response results
                size: 3
            },
            calDate: "2014-06-20"       // The date this calendar was active for
        }
    }

Supplemental/Floor calendar:

.. code-block:: javascript

    {
      success: true,
      message: "",
      responseType: "calendar-floor",   // "calendar-supplemental" if the response is a supplemental
      result: {
        year: 2014,                             // The year the calendar was released
        calendarNumber: 54,                     // Incremental identifier for calendars within a year
        version: "floor",                       // The supplemental version, "floor" or
                                                //  a single capital character
        calDate: "2014-06-20",
        releaseDateTime: "2014-06-20T02:01",    // The date this supplemental was released
        entriesBySection: {                     // A listing of bills mapped to their floor status
          items: {
            THIRD_READING: {                    // List of bills on their third reading
              items: [
                {                               // Modified bill response (link below)
                  basePrintNo: "A5625",
                  session: 2013,
                  printNo: "A5625A",
                  billType: {
                    chamber: "ASSEMBLY",
                    desc: "Assembly",
                    resolution: false
                  },
                  title: "Extends the expiration of the New York state French and Indian war 250th anniversary commemoration commission until December 31, 2015",
                  activeVersion: "A",
                  year: 2013,
                  publishedDateTime: "2013-03-04T14:32:46",
                  substitutedBy: null,
                  sponsor: {
                    member: {
                      memberId: 466,
                      shortName: "ENGLEBRIGHT",
                      sessionYear: 2013,
                      fullName: "Steven Englebright",
                      districtCode: 4
                    },
                    budget: false,
                    rules: false
                  },
                  billCalNo: 1090,              // The calendar number that ids this bill
                                                //  within all calendars
                  sectionType: "THIRD_READING", // The floor status of this bill
                  subBillInfo: {                // Bill info response for a substituted bill
                    basePrintNo: "S7605",
                    session: 2013,
                    printNo: "S7605",
                    billType: {
                      chamber: "SENATE",
                      desc: "Senate",
                      resolution: false
                    },
                    title: "Extends the expiration of the New York state French and Indian war 250th anniversary commemoration commission until December 31, 2015",
                    activeVersion: "",
                    year: 2014,
                    publishedDateTime: "2014-05-15T18:17:31",
                    substitutedBy: null,
                    sponsor: {
                      member: {
                        memberId: 385,
                        shortName: "ESPAILLAT",
                        sessionYear: 2013,
                        fullName: "Adriano Espaillat",
                        districtCode: 31
                      },
                      budget: false,
                      rules: false
                    }
                    },
                  billHigh: false               // Set to true if this is a high priority bill
                },
                ...
              ],
                      size: 284
            },
            STARRED_ON_THIRD_READING: {     // Another floor status. All statuses include:
                                        // ORDER_OF_THE_FIRST_REPORT, ORDER_OF_THE_SECOND_REPORT,
                                        // ORDER_OF_THE_SPECIAL_REPORT, THIRD_READING,
                                        // THIRD_READING_FROM_SPECIAL_REPORT,
                                        // STARRED_ON_THIRD_READING
              items: [...],
              size: 3
            }
          },
          size: 2
        }
      }
    }

`bill response`_

Active List:

.. code-block:: javascript

    {
      success: true,
      message: "",
      responseType: "calendar-activelist",
      result: {
        year: 2014,                             // The year the calendar was released
        calendarNumber: 54,                     // Incremental identifier for calendars within a year
        sequenceNumber: 0,                      // Indicates publish sequence of active lists
        calDate: "2014-06-20",                  // The date this calendar was active
        releaseDateTime: "2014-06-20T04:28:48", // The date and time this active list was released
        notes: null,                            // Notes regarding the active list, pretty much always null
        entries: {                              // List of bills on this active list
          items: [
            {                                   // Modified bill response (see above link)
              basePrintNo: "S4779",
              session: 2013,
              printNo: "S4779B",
              billType: {
                chamber: "SENATE",
                desc: "Senate",
                resolution: false
              },
              title: "Relates to inheritance by children conceived after the death of a genetic parent",
              activeVersion: "B",
              year: 2013,
              publishedDateTime: "2013-04-23T15:04:37",
              substitutedBy: {
                basePrintNo: "A7461",
                session: 2013
              },
              sponsor: {
                member: {
                  memberId: 413,
                  shortName: "BONACIC",
                  sessionYear: 2013,
                  fullName: "John J. Bonacic",
                  districtCode: 42
                },
                budget: false,
                rules: false
              },
              billCalNo: 192                    // The calendar number that ids this bill
                                                //  within all calendars
            },
            ...
          ],
          size: 31
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

   Optional Params:
   full (boolean) - Set to true to see the full calendar responses instead of the summaries.
         (default false)
   order ('ASC'|'DESC') - Determines the order the calendar responses.  Responses are ordered by
         calendar number and then either sequenceNumber or version if they are active lists or
         supplementals respectively.  (default 'ASC')
   limit (number) - Limit the number of results (default 100)
   offset (number) - Start results from offset (default 1)

**Examples**
::
   /api/3/calendars/2014?full=true                       (Get all calendar data from 2014)
   /api/3/calendars/2014?limit=1&order=DESC              (Get the latest calendar from 2014)
   /api/3/calendars/2014/activelist?limit=5              (Get the first 5 active lists of 2014)
   /api/3/calendars/2014/supplemental?limit=5&offset=5   (Get the second 5 supplementals of 2014)

**Committee API**
=================

Get a current committee version
===============================

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
========================================
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
      success : true,
      message : "",
      responseType : "committee",
      result : {
        chamber : "SENATE",     // The chamber of this committee (SENATE or ASSEMBLY)
        name : "Finance",       // The name of this committee
        sessionYear : 2013,     // The session year of this committee version
        referenceDate : "2014-02-28T11:25:44",  // The date and time that this configuration
                                                //  of committee members was reported
        reformed : "2014-03-03T17:09:09",       // The date and time that this configuration
                                                //  of committee members was replaced
                                                // If null, then this is the current committee version
        location : "Room 124 CAP",  // The location where this committee meets
        meetDay : "TUESDAY",        // The day of the week that this committee meets
        meetTime : "11:00",         // The time of day that this committee meets
        meetAltWeek : false,        // True if this committee meets on alternate weeks
        meetAltWeekText : "",       // Describes the committee's alternate schedule if applicable
        committeeMembers : {        // A listing of members in this committee
          items : [
            {
              memberId : 376,                   // An arbitrary unique id used to identify members
                                                //  in our database
              shortName : "DEFRANCISCO",        // The committee member's lbdc shortname
              sessionYear : 2013,               // The session year this member was active in
              fullName : "John A. DeFrancisco", // The member's full name
              districtCode : 50,                // A code designating the member's home district
              sequenceNo : 1,                   // The member's position in the list of committee members
              title : "CHAIR_PERSON"            // The member's role in the committee
                                                //  Valid roles include:
                                                //  "CHAIR_PERSON", "VICE_CHAIR", and "MEMBER"
            },
            ...
          ],
          size : 24
        }
      }
    }

Get committee history
=====================
**Usage**
::
   /api/3/committees/{session}/{chamber}/{committeeName}/history

   Optional Params:
   full (boolean) - Set to true to get full committee responses. (default false)
   order ('ASC'|'DESC') - Determines the order the committee responses.
         Sorted by date.  (default 'DESC')
   limit (number) - Limit the number of results (default 50)
   offset (number) - Start results from offset (default 1)
**Example**
::
   /api/3/committees/2013/senate/Aging/history  (Get 2013 history for the aging committee)
   /api/3/committees/2013/senate/Aging/history?limit=1&order=ASC&full=true
   (Get the first version of the Aging committee from 2013)

Get all current committees
==========================

**Usage**
::
   /api/3/committees/{session}/{chamber}

   Optional Params:
   full (boolean) - Set to true to see the full committee responses (default false)
**Example**
::
   /api/3/committees/2013/senate?&full=true  (Get full responses for all current senate committees for session 2013


**Search APIs**
===============

Most of the Open Legislation data APIs include search functionality.  We built our search layer using elasticsearch
and we aim to provide as much elasticsearch functionality as possible through the APIs.

Every search layer API will have a required request parameter "term" and an optional request param "sort".

.. _`term`:

term
====

.. _`elasticsearch query string`: http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax

The term parameter takes in an `elasticsearch query string`_.

The simplest way to search is to send a general search term.
For example, to search for legislation pertaining to apples, the following query will do the job.
::
    /api/3/bills/search?term=apples

In addition to a simple search term, there are a number of advanced features available.  Our search index is generated
with data in the same JSON format as the API responses, so any response field that is nested under "result" is fair game
for search.  Going back to the previous example, a number of resolutions appear in the results for the apples search query.
Looking back at the `bill response`_, we see that resolutions are designated by the "resolution" boolean under "billType".
In order to filter resolutions out of the search results, a field query can be chained to the original query using "AND".
::
    /api/3/bills/search?term=apples%20AND%20billType.resolution:false

For a full enumeration of query features see the `elasticsearch query string`_ syntax.

sort
====

Searches can be sorted by any number valid response fields.  This is accomplished using the sort request parameter,
which takes a comma separated string of response fields, each designated with a sort order ("ASC" or "DESC") separated
from the field with a colon.  For example, to get the 2013 governor's program bills in canonical order:
::
    /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20_missing_:substitutedBy
                            &sort=programInfo.sequenceNo:ASC

Or, you may want to order them by their status and action date:
::
    /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20_missing_:substitutedBy
                            &sort=status.statusType:ASC,status.actionDate:DESC

Search Response
===============

.. code-block:: javascript

    {
      success: true,
      message: "",
      responseType: "search-results list",
      total: 7,
      offsetStart: 1,
      offsetEnd: 7,
      limit: 10,
      result: {
        items: [
          {
            result: { ... },            // A search result
            rank: 0.3587615191936493    // The ranking of the search result
          },
          ...                           // More search results
        ],
        size: 7
      }
    }
