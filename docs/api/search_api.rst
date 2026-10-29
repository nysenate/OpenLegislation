**Search APIs**
===============

Most of the Open Legislation data APIs include search functionality.  We built our search layer using elasticsearch
and we aim to provide as much elasticsearch functionality as possible through the APIs.

Every search layer API will have a required request parameter "term" and an optional request param "sort".

.. _search-term:

term
----

.. _`elasticsearch query string`: https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#_field_names

The term parameter takes in an `elasticsearch query string`_.

The simplest way to search is to send a general search term.
For example, to search for legislation pertaining to apples, the following query will do the job::

    GET /api/3/bills/search?term=apples

In addition to a simple search term, there are a number of advanced features available.  Our search index is generated
with data in the same JSON format as the API responses, so any response field that is nested under "result" is fair game
for search.  Going back to the previous example, a number of resolutions appear in the results for the apples search query.
Looking back at the :ref:`bill response<bill-response>`, we see that resolutions are designated by the "resolution" boolean under "billType".
In order to filter resolutions out of the search results, a field query can be chained to the original query using "AND"::

    GET /api/3/bills/search?term=apples%20AND%20billType.resolution:false

For a full enumeration of query features see the `elasticsearch query string`_ syntax.

.. _search-sort:

sort
----

Searches can be sorted by any number of fields.  This is accomplished using the sort request parameter,
which takes a comma separated string of fields, each designated with a sort order ("ASC" or "DESC") separated
from the field with a colon.

**Default order.** Without ``sort``, results are not sorted by relevance: they come back in index order, and every
result has a ``rank`` of 1.0. To sort by relevance, most relevant first, use ``sort=_score:DESC``.

**Text fields.** To sort on a text field, use its ``.keyword`` subfield, for example
``sort=status.statusType.keyword:ASC``. Without it, the request fails with HTTP 400 and ``errorCode`` 100. Date,
time, number and boolean fields don't need it. For the following field names, written exactly like this with no
parent field, ``.keyword`` is added for you:

    ``printNo``, ``basePrintNo``, ``basePrintNoStr``, ``version``, ``activeVersion``, ``chamber``, ``location``,
    ``name``, ``docLevelId``, ``docType``, ``lawId``, ``lawName``, ``locationId``, ``email``, ``shortName``,
    ``fullName``, ``prefix``, ``firstName``, ``middleName``, ``lastName``, ``suffix``, ``imgName``,
    ``notificationType``, ``filename``, ``sessionType``

So ``sort=printNo:ASC`` works, but a nested field such as ``billType.chamber`` needs ``billType.chamber.keyword``.

For example, to get the 2013 governor's program bills in canonical order::

    GET /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20NOT%20_exists_:substitutedBy
                                &sort=programInfo.sequenceNo:ASC

Or, you may want to order them by their status and action date::

    GET /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20NOT%20_exists_:substitutedBy
                                &sort=status.statusType.keyword:ASC,status.actionDate:DESC

Agenda search fields
--------------------

The agenda search index isn't structured like the agenda search response. In ``term`` and ``sort``, use these
index paths instead of the response field names:

.. list-table::
   :header-rows: 1

   * - Field
     - Contents
   * - ``agenda.id.number``, ``agenda.id.year``
     - Agenda number and year
   * - ``agenda.weekOf``
     - The week of the agenda, e.g. ``agenda.weekOf:2025-01-13``
   * - ``agenda.publishedDateTime``
     - When the agenda was published
   * - ``agenda.totalAddendum``, ``agenda.totalBillsConsidered``, ``agenda.totalBillsVotedOn``,
       ``agenda.totalCommittees``
     - Totals for the whole agenda
   * - ``committee.committeeId.name``, ``committee.committeeId.chamber``
     - The committee, e.g. ``committee.committeeId.name:Finance``
   * - ``committee.addenda``
     - The committee's addenda, with their meetings and bills

For example, ``committeeId.name:Finance`` matches nothing, while ``committee.committeeId.name:Finance`` matches
the Finance committee's agendas.

Search Response
---------------

Search results are paged like other lists. See :ref:`api-paging`, including the limit on how far into the results a
search can page.

.. code-block:: javascript

    {
      "success": true,
      "message": "",
      "responseType": "search-results list",
      "total": 7,
      "offsetStart": 1,
      "offsetEnd": 7,
      "limit": 10,
      "result": {
        "items": [
          {
            "result": { ... },            // A search result
            "rank": 13.046083,            // Relevance score; 1.0 for every result unless sorted by _score
            "highlights": {               // Matching text, by field, with matches wrapped in <em>
              "printNo": [ "<em>S11</em>" ]
            }
          },
          ...                           // More search results
        ],
        "size": 7
      }
    }

Only some fields are highlighted, so ``highlights`` is often empty (``{}``).

Member search is the exception: its items are member views, the same as the member listing returns, without
``result``, ``rank`` or ``highlights``. Its ``responseType`` is ``"member-session list"``.
