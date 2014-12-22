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
