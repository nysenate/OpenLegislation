.. _api-conventions:

**API Conventions**
===================

These rules apply to every endpoint in the API. The page for each content type documents only what is specific to it.

Base URL and API key
--------------------

Every endpoint is under this base URL::

    https://legislation.nysenate.gov/api/3

Pass your API key as the ``key`` request parameter on every request::

    GET https://legislation.nysenate.gov/api/3/bills/2025/S100?view=info&key=YOUR_API_KEY

A request with a missing or invalid key returns HTTP status 401 with ``errorCode`` 701. The API doesn't distinguish
between a missing key and an invalid one.

.. _response-envelope:

Response envelope
-----------------

Every JSON response has the same top-level fields:

.. code-block:: javascript

    {
      "success": true,             // false for errors
      "message": "",               // Human-readable; don't parse it
      "responseType": "bill",      // What "result" holds
      "result": { ... }            // The data
    }

A response that returns a list adds paging fields, and its ``result`` holds the items:

.. code-block:: javascript

    {
      "success": true,
      "message": "",
      "responseType": "bill-info list",
      "total": 25520,              // Matching items across all pages
      "offsetStart": 1,            // Position of the first item on this page, starting at 1
      "offsetEnd": 50,             // Position of the last item on this page
      "limit": 50,                 // Page size used for this request
      "result": {
        "items": [ ... ],          // The items on this page
        "size": 50                 // Number of items on this page
      }
    }

When a list has no items, ``responseType`` is ``"empty list"``, not the usual type such as ``"bill-info list"``, and
``result.items`` is empty. Check ``total`` or ``result.size`` rather than ``responseType`` to tell whether there are
results.

.. _api-paging:

Paging
------

Endpoints that return lists take two paging parameters:

.. list-table::
   :header-rows: 1

   * - Parameter
     - Values
     - Description
   * - ``limit``
     - 1 - 1000
     - Number of items to return. The default depends on the endpoint, so always set it.
   * - ``offset``
     - 1 or more
     - Position of the first item to return, starting at 1. Defaults to 1.

``offset`` starts at 1, not 0. A value below 1, or one that isn't a number, is treated as 1.

A ``limit`` that isn't a number is ignored, and the endpoint's default is used instead. A ``limit`` of 0, ``all``, or
more than 1000 returns status 400 with ``errorCode`` 1. There is no way to get every item in one request.

**Reading every page**

Start with ``offset=1`` and request the next page at ``offsetEnd + 1``. Stop when ``offsetEnd`` is greater than or
equal to ``total``. An ``offset`` past the end isn't an error: it returns an ``"empty list"``.

For example, in Python:

.. code-block:: python

    offset = 1
    while True:
        page = get("/bills/2025", limit=100, offset=offset)
        for item in page["result"]["items"]:
            ...
        if page["offsetEnd"] >= page["total"]:
            break
        offset = page["offsetEnd"] + 1

Lists can change while you page through them. To keep a copy of the data current, see :ref:`keeping-up`.

.. _api-dates:

Dates and times
---------------

Dates and times use ISO 8601 and are New York local time. Responses write them without an offset, for example
``2026-10-20T14:30:00`` or ``2026-10-20T14:30:00.285395``.

In requests:

- Write a date and time as ``2026-10-20T14:30:00``. Fractional seconds are allowed.
- An offset such as ``Z`` or ``-04:00`` is ignored, not converted. ``2026-10-20T18:30:00Z`` means 6:30 p.m. in New
  York, not 2:30 p.m. Convert times to New York local time before sending them.
- Where a date and time is expected, a date alone, such as ``2026-10-20``, means midnight at the start of that day.
- A range given by ``from`` and ``to`` includes ``from`` and excludes ``to``. A range whose ``from`` is after its
  ``to`` returns status 400.

.. _api-session-years:

Session years
-------------

A legislative session lasts two years and is named by its first year, which is always odd: the 2025 session covers
2025 and 2026. Wherever a session year is expected, an even year means the session it falls in, so ``2026`` means
``2025``. Responses always give the odd year.

Bill print numbers
------------------

Bill print numbers, such as ``S100`` or ``A2345B``, are case-insensitive: ``s100`` and ``S100`` are the same bill.

Requests for a bill return the whole bill, with all its amendments, whatever amendment letter the print number has.
``S100A`` and ``S100`` return the same response. The amendment letter matters only for a bill's PDF
(``/bills/{sessionYear}/{printNo}.pdf``) and for ``view=only_fulltext``, which return that amendment's text.

.. _api-sorting:

Sorting
-------

Endpoints that sort their results take one of two parameters. Each endpoint's page says which.

- ``sort``, on search endpoints and some listings: a comma-separated list of ``field:ASC`` or ``field:DESC``, most
  significant first, such as ``sort=status.statusType.keyword:ASC,status.actionDate:DESC``. See :ref:`search-sort`
  for which fields can be sorted.
- ``order``, on update endpoints and some listings: ``ASC`` or ``DESC``.

.. _api-booleans:

Booleans
--------

Boolean request parameters take ``true`` or ``false``.

.. _api-errors:

Errors
------

An error response has ``success`` set to ``false``, ``responseType`` set to ``"error"``, and an ``errorCode``. Many
errors also say what was wrong in ``errorData``, with ``errorDataType`` naming its type:

.. code-block:: javascript

    {
      "success": false,
      "message": "One or more of the provided request parameters was not valid",
      "responseType": "error",
      "errorCode": 1,
      "errorData": {
        "parameterConstraint": {
          "name": "limit",
          "type": "int",
          "constraint": "Must be > 0 and <= 1000"
        },
        "receivedValue": "0"
      },
      "errorDataType": "invalid-parameter"
    }

Base your client's handling on the HTTP status. The ``errorCode`` and ``errorData`` say more about what went
wrong. The ``message`` is meant for people, and its wording may change.

**HTTP statuses**

.. list-table::
   :header-rows: 1

   * - Status
     - Meaning
   * - 200
     - Success.
   * - 400
     - The request isn't valid: a parameter is malformed or out of range, a required parameter is missing, or a
       search query can't be run.
   * - 401
     - The API key is missing or invalid, or doesn't allow this request.
   * - 404
     - The requested item doesn't exist.
   * - 429
     - Starting December 1, 2026: a rate limit was exceeded. See :ref:`rate-limits`.
   * - 500
     - An unexpected error on our side.

**Error codes**

These error codes apply across the API:

.. list-table::
   :header-rows: 1

   * - ``errorCode``
     - Status
     - Meaning
   * - 1
     - 400
     - A request parameter isn't valid; ``errorData`` says which
   * - 2
     - 400
     - A required request parameter is missing
   * - 100
     - 400
     - The search couldn't be run
   * - 701
     - 401
     - The API key is missing or invalid
   * - 703
     - 429
     - Starting December 1, 2026: rate limit exceeded

A 404 has an ``errorCode`` for the kind of item that wasn't found. Handle it by status.
