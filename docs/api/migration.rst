.. _api-migration:

**API Migration Notes**
=======================

Some listing, search and update endpoints will stop returning full items on December 1, 2026. The options that
request full items from them are deprecated now and will be removed then. Until then they keep working as before.

Deprecated options:

- ``full`` and ``fullTextFormat`` on :ref:`bill listing and search<bill-collection-full-migration>`
- ``fullBill`` and ``fullTextFormat`` on :ref:`bill update feeds<bill-updates-fullbill-migration>`
- ``full`` on :ref:`calendar listings<calendar-collection-full-migration>`, and ``detail`` on calendar search
- ``full`` on :ref:`agenda search<agenda-search-full-migration>`
- ``full`` on :ref:`hearing listing and search<hearing-collection-full-migration>`
- ``full`` on :ref:`floor transcript listing and search<transcript-collection-full-migration>`

To migrate, list or search for summaries or IDs instead, then request each item you need individually. Requests for
a single item still return the full item, and the ``/api/3`` base URL and API keys stay the same. Each of those
requests counts against your :ref:`rate limits<rate-limits>`, so request only the items you need.

.. _removed-option-errors:

How a removed option is rejected
--------------------------------

Starting December 1, 2026, a request that sets ``full``, ``fullBill`` or ``detail`` to true on these endpoints is
rejected with HTTP 400, for every caller. Omitting the option, or setting it to false, is still accepted.
``fullTextFormat`` is not rejected, but it has no effect without full bills.

The rejection is an error response with ``errorCode`` 1 that names the parameter, and its message links to the
section of this page for that option:

.. code-block:: javascript

    {
      "success": false,
      "message": "Bill search and listing no longer return full bills. Omit full for bill summaries, ...",
      "responseType": "error",
      "errorCode": 1,
      "errorData": {
        "parameterConstraint": {
          "name": "full",
          "type": "boolean",
          "constraint": "Must be false or omitted on bill search and listing. Migration guide: ..."
        },
        "receivedValue": "true"
      },
      "errorDataType": "invalid-parameter"
    }

.. _bill-collection-full-migration:

Bill listing and search
-----------------------

``full`` is deprecated on these endpoints, along with ``fullTextFormat``, which only applies to full bills:

- ``/api/3/bills/{sessionYear}``
- ``/api/3/bills/search``
- ``/api/3/bills/{sessionYear}/search``

Starting December 1, 2026, they reject ``full=true`` with HTTP 400, even when ``idsOnly=true`` is also set.

**How to migrate**

Listing and search return a bill summary for each bill. A summary is not a full bill: it has the bill's
identifiers, title, sponsor, status, milestones and actions, but not its amendments (with their full text, memos,
cosponsors and law sections), votes, or veto and approval messages. If your client reads any of those from
listing or search results, change it to:

1. List or search for bills without ``full``. Use the summaries, or add ``idsOnly=true`` if you only need
   bill IDs.
2. Request each bill you need from ``/api/3/bills/{sessionYear}/{printNo}``, which returns the
   :ref:`full bill<bill-response>` by default. Add ``view=info`` there if the summary is enough.

Each of those requests counts against your :ref:`rate limits<rate-limits>`, so request only the bills you need.

With ``idsOnly=true``, the listing items are the bill IDs themselves::

    GET /api/3/bills/2013?idsOnly=true

.. code-block:: javascript

    "result": {
      "items": [
        {
          "basePrintNo": "S1",           // Base print no; use it with session to request the bill
          "session": 2013,
          "basePrintNoStr": "S1-2013"
        },
        ...
      ],
      "size": 50
    }

In search results, each bill ID is nested under ``result``, next to the result's ``rank`` and ``highlights``::

    GET /api/3/bills/2013/search?term=education&idsOnly=true

.. code-block:: javascript

    "result": {
      "items": [
        {
          "result": {
            "basePrintNo": "S1234",
            "session": 2013,
            "basePrintNoStr": "S1234-2013",
            "printNo": "S1234",          // Also the base print no, without an amendment letter
            "version": ""
          },
          "rank": 1.23,
          "highlights": { }
        },
        ...
      ],
      "size": 25
    }

In both cases, request the full bill with its base print no and session, e.g. ``/api/3/bills/2013/S1234``.

.. _bill-updates-fullbill-migration:

Bill update feeds
-----------------

``fullBill`` is deprecated on these endpoints, along with ``fullTextFormat``, which only applies to full bills:

- ``/api/3/bills/updates``
- ``/api/3/bills/updates/{fromDateTime}``
- ``/api/3/bills/updates/{fromDateTime}/{toDateTime}``

Starting December 1, 2026, they reject ``fullBill`` with HTTP 400 whenever it is true, even when ``detail=true`` or
``summary=true`` is also set. ``fullBill`` counts as true when its value is ``true``, ``1``, ``yes``, ``on``, ``y``
or ``t``, in any case.

**How to migrate**

Each update token already identifies its bill in ``id``. Choose one of these instead of ``fullBill``:

- Add ``summary=true`` to include a bill summary with each update token, under ``item``. A summary has the bill's
  identifiers, title, sponsor and type, but not its status, actions, amendments or votes.
- Request each updated bill you need from ``/api/3/bills/{sessionYear}/{printNo}``, using ``session`` and
  ``basePrintNo`` from the token's ``id``. It returns the :ref:`full bill<bill-response>` by default.

Each of those bill requests counts against your :ref:`rate limits<rate-limits>`.

Bills updated during a time range, with a summary of each bill::

    GET /api/3/bills/updates/2019-02-13T08:00:00/2019-02-13T10:55:48?summary=true

``summary=true`` and ``detail=true`` (with any ``filter``) keep working as before.

.. _calendar-collection-full-migration:

Calendar listings and search
----------------------------

``full`` is deprecated on calendar listings:

- ``/api/3/calendars/{year}``
- ``/api/3/calendars/{year}/activelist``
- ``/api/3/calendars/{year}/supplemental``

``detail`` is deprecated on calendar search (``/api/3/calendars/search`` and ``/api/3/calendars/{year}/search``).

Starting December 1, 2026, they reject ``full=true`` and ``detail=true`` with HTTP 400. The ``full`` parameter of a
single calendar request is not affected.

**How to migrate**

List calendars without ``full`` to get a summary of each one, then request each calendar you need in full:

- A calendar: ``/api/3/calendars/{year}/{calendarNumber}``
- An active list: ``/api/3/calendars/{year}/{calendarNumber}/{sequenceNumber}``
- A supplemental or floor calendar: ``/api/3/calendars/{year}/{calendarNumber}/{versionCharacter}``

Calendar search without ``detail`` returns summaries too; request each calendar you need the same way. Each of
those requests counts against your :ref:`rate limits<rate-limits>`.

.. _agenda-search-full-migration:

Agenda search
-------------

``full`` is deprecated on agenda search:

- ``/api/3/agendas/search``
- ``/api/3/agendas/{year}/search``

Starting December 1, 2026, they reject ``full=true`` with HTTP 400.

**How to migrate**

Search without ``full``. Each result is a committee agenda summary: the agenda and committee it belongs to, the
week, the number of addenda, and the number of bills considered and voted on. Then request each committee agenda
you need, with its meetings and bills, from ``/api/3/agendas/{year}/{agendaNo}/{committeeName}``. Each of those
requests counts against your :ref:`rate limits<rate-limits>`.

.. _hearing-collection-full-migration:

Hearing listing and search
--------------------------

``full`` is deprecated on these endpoints:

- ``/api/3/hearings``
- ``/api/3/hearings/{year}``
- ``/api/3/hearings/search``
- ``/api/3/hearings/{year}/search``

Starting December 1, 2026, they reject ``full=true`` with HTTP 400, even when ``summary=true`` is also set.

**How to migrate**

List or search for hearings without ``full``:

- With ``summary=true``, each hearing has its id, filename, title, date, address, start and end times, and the
  committees that held it, but not its text.
- With ``summary=false``, each hearing has only its id and filename.

Then request each hearing you need, with its text, from ``/api/3/hearings/{id}``. Each of those requests counts
against your :ref:`rate limits<rate-limits>`.

.. _transcript-collection-full-migration:

Floor transcript listing and search
-----------------------------------

``full`` is deprecated on these endpoints:

- ``/api/3/transcripts``
- ``/api/3/transcripts/{year}``
- ``/api/3/transcripts/search``
- ``/api/3/transcripts/{year}/search``

Starting December 1, 2026, they reject ``full=true`` with HTTP 400, even when ``summary=true`` is also set.

**How to migrate**

List or search for transcripts without ``full``:

- With ``summary=true``, each transcript has its dateTime, sessionType, location and dayType, but not its text.
- With ``summary=false``, each transcript has only its dateTime and sessionType.

Then request each transcript you need, with its text, from ``/api/3/transcripts/{dateTime}/{sessionType}``. Each of
those requests counts against your :ref:`rate limits<rate-limits>`.
