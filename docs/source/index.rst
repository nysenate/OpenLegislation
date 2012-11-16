The OpenLegislation API
============================

The OpenLegislation API exposes methods for retrieval of individual documents
by object id as well as retrieval of arbitrary feeds by supported by the `Lucene`_
document search engine. The API has full support for ``XML`` and ``JSON``
standard formats as well as support for ``RSS`` and ``ATOM`` formats for all
search queries.

.. note::

    ``JSONP`` format now supported. Use the ``callback`` argument to specify the call
    back function to wrap the result in.

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
| **ALL**         |                  |                                                        |
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
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **action**      |                  |                                                        |
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
|                 | sponsor          | sponsor of bill                                        |
+-----------------+------------------+--------------------------------------------------------+
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **bill**        |                  |                                                        |
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
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **calendar**    |                  |                                                        |
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
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **meeting**     |                  |                                                        |
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
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **transcript**  |                  |                                                        |
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
|                 |                  |                                                        |
+-----------------+------------------+--------------------------------------------------------+
| **vote**        |                  |                                                        |
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
|                 | sponsor          | sponsor of bill                                        |
+-----------------+------------------+--------------------------------------------------------+




Result Structure
~~~~~~~~~~~~~~~~~~~~

All request results are returned within a response object with the following
structure.

::

    {
        "response": {
            "metadata": {
                "totalresults": <number>,
            },
            "results": [
                {
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
                }
            ]
        }
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

::

    {
        "year":"2011",
        "senateBillNo":"S607-2011",
        "title":"Relates to the definition of alternate energy production facilities",
        "lawSection":"Public Service Law",
        "sameAs":"A3536",
        "previousVersions":["S8310-2009"],
        "sponsor":{"fullname":"MAZIARZ"},
        "coSponsors":null,
        "multiSponsors":null,
        "summary":"Adds lithium ion energy batteries to the definition of alternate energy production facilities.",
        "currentCommittee":null,
        "actions":[
		    {
		        "date":"1294185600000",
		        "text":"REFERRED TO ENERGY AND TELECOMMUNICATIONS"
		    }
        ],
        "fulltext": "A really long string",
        "memo": "A much shorter string",
        "law":"Amd S2, Pub Serv L ",
        "votes":[
		    {
		        "voteType":"2",
		        "voteDate":"1295947800000",
		        "ayes":["Maziarz","Alesi","Fuschillo","Ritchie","O'Mara","Ranzenhofer","Robach","Parker","Gianaris","Kennedy"],
		        "nays":null,
		        "abstains":null,
		        "excused":null,
		        "ayeswr":["Adams","Kruger"],
		        "description":"Energy and Telecommunications"
		    }
        ]
    }

Action
**********

::

	{
		"date":"1316736000000",
		"text":"enacting clause stricken",
		"bill":
			{
				"year":"2011",
				"senateBillNo":"A8591-2011",
				"title":"Criminalizes unlawful conduct of a farm products dealer in certain circumstances",
				"sameAs":null,
				"sponsor":
					{
						"fullname":"Rabbitt"
					},
				"summary":"Criminalizes unlawful conduct of a farm products dealer in certain circumstances."}
			}
	} 

Vote
*******

::

	{
		"voteType":"1",
		"voteDate":"1308268800000",
		"ayes":["Adams","Addabbo","Alesi","Avella","Ball","Bonacic","Breslin","Carlucci","DeFrancisco","Diaz","Dilan","Duane","Espaillat","Farley","Flanagan","Fuschillo","Gallivan","Gianaris","Golden","Griffo","Grisanti","Hannon","Hassell-Thompson","Huntley","Johnson","Kennedy","Klein","Krueger","Kruger","Lanza","Larkin","LaValle","Libous","Little","Marcellino","Martins","Maziarz","McDonald","Montgomery","Nozzolio","O'Mara","Oppenheimer","Parker","Peralta","Perkins","Ranzenhofer","Ritchie","Rivera","Robach","Saland","Sampson","Savino","Serrano","Seward","Skelos","Smith","Squadron","Stavisky","Stewart-Cousins","Valesky","Young","Zeldin"],
		"nays":[],
		"abstains":[],
		"excused":[],
		"bill":
			{
				"year":"2011",
				"senateBillNo":"S2628A-2011",
				"title":"Relates to the practice of public accountancy by accountants who are not licensed in New York state; repealer",
				"sameAs":"A4881B",
				"sponsor":
					{
						"fullname":"LAVALLE"
					},
					"summary":"Relates to the practice of public accountancy by accountants who are not licensed in New York state; allows accountants licensed in other states to have practice privileges in New York."
			},
		"ayeswr":null,
		"description":null
	}

Meeting
----------

::

    {
        "meetingDateTime":"1308873600000",
        "meetday":"Wednesday",
        "location":null,
        "committeeName":"Rules",
        "committeeChair":"Dean G. Skelos",
        "bills":[
            {
                "year":"2011",
                "senateBillNo":"S553-2011",
                "title":"Authorizes the forest ranger force to establish a training program for volunteer search and rescue personnel to assist the forest rangers",
                "sameAs":"A5016",
                "sponsor":{"fullname":"LITTLE"},
                "summary":"Authorizes the forest ranger force to establish a training program for volunteer search and rescue personnel to assist the forest rangers in wild, remote and forested areas of the state."
            }
         ],
         "notes":"*ALL BILLS REPORTED DIRECT TO 3RD READING*\n\nMEETING TO BE CALLED OFF THE FLOOR",
         "addendums":[
            {
                "addendumId":"Q",
                "weekOf":"2011-06-20",
                "publicationDateTime":"1308939965000",
                "agenda":{
                    "number":"20",
                    "sessionYear":"2011",
                    "year":"2011"
                }
            }
        ]
    }


Calendar
-----------


Active calendars use sequences.

::

    {
        "year":"2011",
        "type":"active",
        "sessionYear":"2011",
        "no":"60",
        "supplementals":[
            {
                "calendarDate":null,
                "releaseDateTime":null,
                "sections":null,
                "sequence":{
                    "no":"",
                    "actCalDate":"1308873600000",
                    "releaseDateTime":"1308937283000",
                    "calendarEntries":[
                        {
                            "no":"545",
                            "bill":{
                                "year":"2011",
                                "senateBillNo":"S3907A-2011",
                                "title":"Includes the Advanced Energy Research and Technology Center (AERTC) at the State University of New York at Stony Brook in the center for excellence program",
                                "sameAs":"A4476A",
                                "sponsor":{"fullname":"LAVALLE"},
                                "summary":"Includes the Advanced Energy Research and Technology Center (AERTC) at the State University of New York at Stony Brook in the center for excellence program."
                            },
                            "billHigh":null,
                            "subBill":null,
                            "motionDate":null
                        }
                    ]
                }
            }
        ],
        "id":"cal-active-00060-2011-2011"
    }

Floor calendars use sections.

::

    {
        "year":"2011",
        "type":"floor",
        "sessionYear":"2011",
        "no":"60",
        "supplementals":[
            {
                "calendarDate":"1308873600000",
                "releaseDateTime":"1308871140000",
                "sections":[
                    {
                        "name":"BILLS ON THIRD READING",
                        "type":"C",
                        "cd":"0400",
                        "calendarEntries":[
                            {
                                "no":"48",
                                "bill":{
                                    "year":"2011",
                                    "senateBillNo":"S922-2011",
                                    "title":"Exempts operators of law enforcement vessels from laws which regulate vessels on the navigable waters of the state while responding to emergencies",
                                    "sameAs":null,
                                    "sponsor":{"fullname":"MARCELLINO"},
                                    "summary":"Exempts operators of law enforcement vessels from laws which regulate vessels on the navigable waters of the state while such operators are in the course of responding to emergencies."
                                },
                                "billHigh":null/true/false,
                                "subBill":null,
                                "motionDate":null
                            },
                        ]
                    }
                ],
                "sequence":null,
            }
        ]
    }


Transcript
------------

::

    {
        "timeStamp":"1312369200000",
        "location":"ALBANY, NEW YORK",
        "type":"REGULAR SESSION",
        "transcriptText": "Really Really long String Here"
    }


Examples
=============

Coming Soon!

.. _senator: http://open.nysenate.gov/legislation/senators.json
.. _committee: http://open.nysenate.gov/legislation/committees.json
.. _Lucene: http://lucene.apache.org
.. _valid lucene query: http://lucene.apache.org/java/3_3_0/queryparsersyntax.html
.. _let us know: williams@nysenate.gov
