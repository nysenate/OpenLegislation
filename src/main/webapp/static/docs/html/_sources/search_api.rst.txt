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
For example, to search for legislation pertaining to apples, the following query will do the job.
::
    /api/3/bills/search?term=apples

In addition to a simple search term, there are a number of advanced features available.  Our search index is generated
with data in the same JSON format as the API responses, so any response field that is nested under "result" is fair game
for search.  Going back to the previous example, a number of resolutions appear in the results for the apples search query.
Looking back at the :ref:`bill response<bill-response>`, we see that resolutions are designated by the "resolution" boolean under "billType".
In order to filter resolutions out of the search results, a field query can be chained to the original query using "AND".
::
    /api/3/bills/search?term=apples%20AND%20billType.resolution:false

For a full enumeration of query features see the `elasticsearch query string`_ syntax.

.. _search-sort:

sort
----

Searches can be sorted by any number valid response fields.  This is accomplished using the sort request parameter,
which takes a comma separated string of response fields, each designated with a sort order ("ASC" or "DESC") separated
from the field with a colon.

For example, to get the 2013 governor's program bills in canonical order:
::
    /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20_missing_:substitutedBy
                            &sort=programInfo.sequenceNo:ASC

Or, you may want to order them by their status and action date:
::
    /api/3/bills/2013/search?term=programInfo.name:Governor%20AND%20_missing_:substitutedBy
                            &sort=status.statusType:ASC,status.actionDate:DESC

Search Response
---------------

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
            "rank": 0.3587615191936493    // The ranking of the search result
          },
          ...                           // More search results
        ],
        "size": 7
      }
    }