**Senate Members API**
======================

Get a single Member
-------------------

**Usage**

Retrieve member by session year and member id
::

    (GET) /api/3/members/{sessionYear}/{id}

**Optional Params**

+-----------+---------+---------------------------------------------------------------------------------------------+
| Parameter | Values  | Description                                                                                 |
+===========+=========+=============================================================================================+
| full      | boolean | (default true) Set to true to get a full member response instead of a summary.            |
+-----------+---------+---------------------------------------------------------------------------------------------+

**Examples**

Get member with id 371 during 2013 session year.
::
    /api/3/members/2013/371

**Sample Response**

.. code-block:: javascript
    {
  "success" : true,
  "message" : "",
  "responseType" : "member-sessions",
  "result" : {
    "memberId" : 371,
    "chamber" : "SENATE",
    "incumbent" : true,
    "fullName" : "James L. Seward",
    "shortName" : "SEWARD",
    "sessionShortNameMap" : {
      "2013" : [ {
        "sessionMemberId" : 127,
        "shortName" : "SEWARD",
        "sessionYear" : 2013,
        "districtCode" : 51,
        "alternate" : false,
        "memberId" : 371
      } ]
    },
    "person" : {
      "personId" : 190,
      "fullName" : "James L. Seward",
      "firstName" : "James",
      "middleName" : "L.",
      "lastName" : "Seward",
      "email" : "seward@senate.state.ny.us",
      "prefix" : "Senator",
      "suffix" : null,
      "verified" : true,
      "imgName" : "371_james_l._seward.jpg"
    }
  }
}


Get a list of members
---------------------

**Usage**

List members for a session year.
::
    (GET) /api/3/members/{sessionYear}

List members in a chamber for a session year
::
    (GET) /api/3/members/{sessionYear}/{chamber}

**Optional Params**

+-----------+--------------------+----------------------------------------------------------------------+
| Parameter | Values             | Description                                                          |
+===========+====================+======================================================================+
| limit     | 1 - 1000           | (default = 50) Number of results to return                           |
+-----------+--------------------+----------------------------------------------------------------------+
| offset    | > 1                | (default = 1) Result number to start from                            |
+-----------+--------------------+----------------------------------------------------------------------+
| full      | boolean            | (default = false) Set to true to see the full member responses.      |
+-----------+--------------------+----------------------------------------------------------------------+
| sort      | string             | (default = shortName:asc) Sort by any field from the response.       |
+-----------+--------------------+----------------------------------------------------------------------+

**Examples**

List all members from session year 2013
::
    /api/3/members/2013

List full member info for session members during session year 2011. Limit to 5 results.
::
    /api/3/members/2011/senate?full=true&limit=5

Search for members
------------------

Read our :doc:`search API docs<search_api>` for info on how to construct search terms.

**Usage**

Search across all session years
::
    (GET) /api/3/members/search?term=YOUR_TERM

Search within a session year
::
    (GET) /api/3/members/{sessionYear}/search?term=YOUR_TERM

Note: given a sessionMemberId = #### in a session year yyyy, you can get the member that sessionMemberId is used by with:
::
    (GET) /api/3/members/search?term=sessionShortNameMap.yyyy.sessionMemberId=####

**Required Params**

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| term      | string             | :ref:`ElasticSearch query string<search-term>`         |
+-----------+--------------------+--------------------------------------------------------+

**Optional Params**

+-----------+--------------------+----------------------------------------------------------------------+
| Parameter | Values             | Description                                                          |
+===========+====================+======================================================================+
| limit     | 1 - 1000           | (default = 50) Number of results to return                           |
+-----------+--------------------+----------------------------------------------------------------------+
| offset    | > 1                | (default = 1) Result number to start from                            |
+-----------+--------------------+----------------------------------------------------------------------+
| full      | boolean            | (default = false) Set to true to see the full member responses.      |
+-----------+--------------------+----------------------------------------------------------------------+
| sort      | string             | (default = "") Sort by any field from the response.                  |
+-----------+--------------------+----------------------------------------------------------------------+

**Examples**

List all members who have served district code 20
::
    /api/3/members/search?term=districtCode:20
