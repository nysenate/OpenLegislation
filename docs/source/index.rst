The OpenLegislation API
============================

The OpenLegislation API exposes methods for retrieval of individual documents
by object id as well as retrieval of arbitrary feeds by supported by the `Lucene`_
document search engine. The API has full support for ``XML`` and ``JSON``
standard formats as well as support for ``RSS`` and ``ATOM`` formats for all
search queries.

The available toplevel document types are currently:

* Bill_
* Meeting_
* Calendar_
* Transcript_

Additionally, we have produced json files for `senator` and `committee` data
to provide limited support for information from the NYSenate.gov site. A
deeper integration would require using the SenateServices library which can
be provided as necessary.


Document Requests
~~~~~~~~~~~~~~~~~~~~

The API offers a series of document requests in the following general format::

    legislation/2.0/<object type>/<object id>.<format>

Available object types and the format of their object ids.

+-------------+--------------------------------------+----------------------------+
| Object Type | Object Id Format                     | Example                    |
+=============+======================================+============================+
| Bill        | <bill id>-YYYY                       | S1234-2011                 |
+-------------+--------------------------------------+----------------------------+
| Meeting     | <committee>-MM-DD-YYY                | Finance-06-24-2011         |
+-------------+--------------------------------------+----------------------------+
| Calendar    | <floor|active>-MM-DD-YYYY            | floor-06-24-2011           |
+-------------+--------------------------------------+----------------------------+
| Transcript  | <regular|special>-session-MM-DD-YYYY | regular-session-08-03-2011 |
+-------------+--------------------------------------+----------------------------+

These requests are really just specialized cases of the search request for which
the details are handled internally in the OpenLegislation system.

Search Requests
~~~~~~~~~~~~~~~~~~~~~~~

The API's primary offering is the search request that enables custom lucene queries::

    legislation/2.0/search.<format>?term=<lucene query>

The lucene query can be any `valid lucene query`_ composed of any logically valid
combination of the documents fields in the section below.

Search Parameters
-----------------------

Search requests can take advantage of several other parameters as well.

+-----------+--------------------+--------------------------------------------------------+
| Parameter | Values             | Description                                            |
+===========+====================+========================================================+
| pageSize  | 1-1000             | Limits the number of results returned by each request. |
+-----------+--------------------+--------------------------------------------------------+
| pageIdx   | 1+                 | Indicates the which page of results you want.          |
+-----------+--------------------+--------------------------------------------------------+
| sortOrder | true/false         | True - Descending, False -Decending by the sort field. |
+-----------+--------------------+--------------------------------------------------------+
| sort      | any document field | sorts the result set by the indicated document field.  |
+-----------+--------------------+--------------------------------------------------------+


The Fields Table
--------------------

.. note:: 

    - Dates are in Mon DD, YYYY format and usually correspond to the document when field.
    - <XXXX> refers to field XXXX of the same document.

Becuase documents are organized into different structures based on otype you must be careful
that all the fields you reference in your (sub)query are present in all the document types
that you intend to retrieve. Mixing fields incorrectly will give you empty set results.

+-----------------+------------------+--------------------------------------------------------+
| Document Type   | Document Field   | Field Description                                      |
+=================+==================+========================================================+
| **ALL**                                                                                     |
+-----------------+------------------+--------------------------------------------------------+
|                 | modified         | Unix time stamp of when document was last modified.    |
+-----------------+------------------+--------------------------------------------------------+
|                 | active           | Boolean value indicating if the document is active.    |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | A document's unique object id.                         |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | The document type of the bill.                         |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | The default search field. Contect varies by otype.     |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **action** *(sub-document of the bill document)*                                            |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <billno>-<when>-<title>                                |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``action``                                             |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <billno> <title>                                       |
+-----------------+------------------+--------------------------------------------------------+
|                 | billno           | The parent bill number.                                |
+-----------------+------------------+--------------------------------------------------------+
|                 | title            | The action text.                                       |
+-----------------+------------------+--------------------------------------------------------+
|                 | when             | The unix time stamp for the action                     |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **bill**                                                                                    |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <bill number>-<year>                                   |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``bill``                                               |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <bill number> <sameas> <sponsor> <summary> <title>     |
+-----------------+------------------+--------------------------------------------------------+
|                 | actclause        | The bill act clause                                    |
+-----------------+------------------+--------------------------------------------------------+
|                 | actions          | Contains the text for all the bill's previous actions. |
+-----------------+------------------+--------------------------------------------------------+
|                 | committee        | The name of the current committee holding the bill.    |
+-----------------+------------------+--------------------------------------------------------+
|                 | cosponsors       | Contains the short names of all bill cosponsors.       |
+-----------------+------------------+--------------------------------------------------------+
|                 | full             | The full text of the bill.                             |
+-----------------+------------------+--------------------------------------------------------+
|                 | lawsection       | The lawsection of the bill, i.e. General Business Law. |
+-----------------+------------------+--------------------------------------------------------+
|                 | memo             | The bill memo.                                         |
+-----------------+------------------+--------------------------------------------------------+
|                 | pastcommittees   | Contains the names of all the bills past committees.   |
+-----------------+------------------+--------------------------------------------------------+
|                 | sameas           | Specifies the id of the bill's sister document. Senate |
|                 |                  | bills introduced in the assembly and visa versa.       |
+-----------------+------------------+--------------------------------------------------------+
|                 | sponsor          | The short name of the bill sponsor.                    |
+-----------------+------------------+--------------------------------------------------------+
|                 | stricken         | Boolean value indicating if the bill has been stricken.|
+-----------------+------------------+--------------------------------------------------------+
|                 | summary          | The bill summary text.                                 |
+-----------------+------------------+--------------------------------------------------------+
|                 | title            | The bill title.                                        |
+-----------------+------------------+--------------------------------------------------------+
|                 | year             | The bill session year (2009, 2011, etc.)               |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **calendar**                                                                                |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <ctype>-MM-DD-YYYY                                     |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``calendar``                                           |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <title>                                                |
+-----------------+------------------+--------------------------------------------------------+
|                 | bills            | Contains all the oids for the calendar's bills.        |
+-----------------+------------------+--------------------------------------------------------+
|                 | ctype            | Calendar type, either ``floor`` or ``active``          |
+-----------------+------------------+--------------------------------------------------------+
|                 | summary          | <calendar notes or calendar name> - <# of bills>       |
+-----------------+------------------+--------------------------------------------------------+
|                 | title            | <calendar number> - <ctype> - <calendar date>          |
+-----------------+------------------+--------------------------------------------------------+
|                 | when             | unix timestamp of the calendar datetime.               |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **meeting**                                                                                 |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <committee>-MM-DD-YYYY                                 |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``meeting``                                            |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <committee> - <chair> - <location> - <note>            |
+-----------------+------------------+--------------------------------------------------------+
|                 | bills            | Contains the oids of bill the meeting's bills.         |
+-----------------+------------------+--------------------------------------------------------+
|                 | chair            | A freetext field representing the chair person's name. |
+-----------------+------------------+--------------------------------------------------------+
|                 | committee        | The name of the committee that is meeting.             |
+-----------------+------------------+--------------------------------------------------------+
|                 | location         | The name of the room the meeting was held in.          |
+-----------------+------------------+--------------------------------------------------------+
|                 | notes            | A text field for miscellaneous meeting notes.          |
+-----------------+------------------+--------------------------------------------------------+
|                 | title            | <committee - <meeting date>                            |
+-----------------+------------------+--------------------------------------------------------+
|                 | when             | unix timestamp of the meeting datetime.                |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **transcript**                                                                              |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <session-type>-MM-DD-YY                                |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``transcript``                                         |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <full>                                                 |
+-----------------+------------------+--------------------------------------------------------+
|                 | full             | The full text of the transcript.                       |
+-----------------+------------------+--------------------------------------------------------+
|                 | location         | The location the transcript was recorded.              |
+-----------------+------------------+--------------------------------------------------------+
|                 | session-type     | ``regular`` or ``extra-ordinary``                      |
+-----------------+------------------+--------------------------------------------------------+
|                 | summary          | <location>                                             |
+-----------------+------------------+--------------------------------------------------------+
|                 | when             | unix time stamp of when the transcript was recorded.   |
+-----------------+------------------+--------------------------------------------------------+
|                                                                                             |
+-----------------+------------------+--------------------------------------------------------+
| **vote** *(sub-document of the bill document)*                                              |
+-----------------+------------------+--------------------------------------------------------+
|                 | oid              | <when>-<billno>-<count(aye)>-<count(nay)>              |
+-----------------+------------------+--------------------------------------------------------+
|                 | otype            | ``vote``                                               |
+-----------------+------------------+--------------------------------------------------------+
|                 | osearch          | <billno> - <voteType>                                  |
+-----------------+------------------+--------------------------------------------------------+
|                 | abstain          | Contains short names for all abstaining senators.      |
+-----------------+------------------+--------------------------------------------------------+
|                 | aye              | Contains short names for all the senators voting aye.  |
+-----------------+------------------+--------------------------------------------------------+
|                 | billno           | The oid of the bill being voted on.                    |
+-----------------+------------------+--------------------------------------------------------+
|                 | committee        | If it is a committee vote, the name of the committee.  |
+-----------------+------------------+--------------------------------------------------------+
|                 | excused          | Contains short names for all excused senators.         |
+-----------------+------------------+--------------------------------------------------------+
|                 | nay              | Contains short names for all the senators voting nay.  |
+-----------------+------------------+--------------------------------------------------------+
|                 | summary          | <vote date>                                            |
+-----------------+------------------+--------------------------------------------------------+
|                 | title            | <billno> - <vote date> - <voteType>                    |
+-----------------+------------------+--------------------------------------------------------+
|                 | voteType         | A 1 or a 2 for ``FLOOR`` or ``COMMITTEE`` votes.       |
+-----------------+------------------+--------------------------------------------------------+
|                 | when             | unix timestamp of the vote datetime.                   |
+-----------------+------------------+--------------------------------------------------------+




Result Structure
~~~~~~~~~~~~~~~~~~~~

All request results are returned within a response object with the following
structure::

    {"response": {
        "metadata": {
            "totalresults": <number>,
        },
        "results": [{
            "type": <object type>,
            "oid": <unique object id>,
            "url": <url for the corresponding webpage>,
            "data": {
                #Object specific data structure
            }
        }, {
            #Next object
        }, {
            ....
        }]
    }

Document requests will have always have a single result object in
the results list. We use the same response structure regardless of the access
method or result count for consistency and because all access paths are really
just specialized shortcuts for the search request.

Metadata
------------------


The metadata response property currently only reports the totalresults returned
from the generated (or supplied) lucene query. For document requests this should
always be 1. The property will be expanded as necessary to include other useful
and relevant metadata in the future.


Results
-----------------

Each result object has type, oid, and html link information in addition to the
complete serialization of the matching document. This is to prevent the need to
perform a search and then perform a series of document requests for further
information.

When possible, please use the provided values for oid, url links, and other
items as they become available. As OpenLegislation changes over time, these
fields will be updated and remain correct while those that you generate will
be depreciated. If you find yourself generating information that you think is
generally useful and could be supplied in the response `let us know`_.


Document Structure
~~~~~~~~~~~~~~~~~~~~~~~~

Each document and subdocument has its own structure which reflects the full
amount of information we have available at current time with exception to
instances where including information could cause cycles in the object
serialization process.

Bill
---------

Bill Stuff

Action
**********

Action stuff

Vote
*******

Vote Stuff

Meeting
----------

Meeting stuff

Calendar
-----------

Calendar Stuff

Transcript
------------

Transcript Stuff


Examples
=============

.. _senator: http://open.nysenate.gov/legislation/senators.json
.. _committee: http://open.nysenate.gov/legislation/committees.json
.. _Lucene: http://lucene.apache.org
.. _valid lucene query: http://lucene.apache.org/java/3_3_0/queryparsersyntax.html
.. _let us know: williams@nysenate.gov
