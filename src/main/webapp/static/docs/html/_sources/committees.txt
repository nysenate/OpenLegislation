**Committee API**
=================

Get a current committee version
-------------------------------

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
----------------------------------------
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
---------------------

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
--------------------------

**Usage**
::
   /api/3/committees/{session}/{chamber}

   Optional Params:
   full (boolean) - Set to true to see the full committee responses (default false)

**Example**
::
   /api/3/committees/2013/senate?&full=true  (Get full responses for all current senate committees for session 2013
