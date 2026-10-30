.. _keeping-up:

**Keeping Up with Changes**
===========================

To keep your own copy of Open Legislation data current, poll the update APIs. Each poll asks which items changed
since the previous poll ended, then fetches each of those items in full. This page explains how to do that without
missing changes.

The approach in brief:

1. Note where your first poll will start, then load everything you need (see `The initial load`_).
2. Every few minutes, ask the update APIs which items changed between the end of the previous poll and now.
3. Fetch each changed item in full and save it.
4. Remember where this poll ended, and use it as the start of the next one.

Which update APIs to poll
-------------------------

.. list-table::
   :header-rows: 1

   * - Content
     - Update API
     - Reference
   * - Bills, agendas, calendars, laws
     - ``/api/3/updates/{from}/{to}``
     - :doc:`agg_updates`
   * - Hearings
     - ``/api/3/hearings/updates/{from}/{to}``
     - :ref:`hearing updates<hearing-updates>`
   * - Floor transcripts
     - ``/api/3/transcripts/updates/{from}/{to}``
     - :ref:`transcript updates<transcript-updates>`
   * - Committees, members
     - None
     - :doc:`committees`, :doc:`members`

The aggregate update API, ``/api/3/updates``, covers bills, agendas, calendars and laws in one feed, so you don't need
the bill, agenda, calendar or law update APIs as well. It doesn't cover hearings or floor transcripts, which have
update APIs of their own.

Committees and members have no update API, so list them again periodically and compare. See
`Committees and members`_.

How update ranges work
----------------------

Getting these rules wrong is the usual way to miss changes.

**Give both ends of the range.** Always request ``{from}/{to}``. With ``from`` alone, the aggregate API's range ends
now, but the hearing and transcript ranges have no end. Either way, you need to choose where a range ends so the
next poll can start exactly there.

**The range includes** ``from`` **and excludes** ``to``. A change at exactly ``to`` belongs to the next range. So
when each poll's ``from`` is the previous poll's ``to``, consecutive polls meet with no gap and no overlap.

**Times are New York local time, without an offset** (see :ref:`api-dates`). An offset is ignored, not
converted, so a client that sends the current UTC time as ``to`` asks for a range that ends hours in the future.
Changes that OpenLeg processes during those hours fall inside a range the client has already polled, so it misses
them all.

**End each range a couple of minutes in the past.** Use New York now minus 2 minutes as ``to``. OpenLeg records
each change when it saves it, and the margin leaves time for changes being saved as you poll to show up in your
range rather than slip in behind it.

**Use processed times.** The aggregate API's default ``type`` is ``processed``: when OpenLeg processed the change.
Leave it there. The ``published`` type orders changes by when LBDC published the source data, and OpenLeg can
process or reprocess data well after that, so a published-time range you already polled can gain changes later.

**Read the whole range.** A range can hold more changes than fit in one page. Page through it with ``offset`` until
``offsetEnd`` reaches ``total``, using ``limit=100`` to keep the number of requests down. Treat the range as
done only once you have read every page and fetched every changed item.

**Remember** ``to``, **not the time of the last change.** Save the ``to`` you sent, and send it as the next
``from``.

Fetching the changed items
--------------------------

Each update names an item that changed in the range, not the change itself. An item appears once per range,
however many times it changed there, so request each item once. Fetching an item you already have is harmless.

+-----------------------+---------------------------------------+---------------------------------------------------+
| Update                | Id fields                             | Request for the full item                         |
+=======================+=======================================+===================================================+
| ``BILL``              | ``id.basePrintNo``, ``id.session``    | ``/api/3/bills/{session}/{basePrintNo}``          |
+-----------------------+---------------------------------------+---------------------------------------------------+
| ``AGENDA``            | ``id.number``, ``id.year``            | ``/api/3/agendas/{year}/{number}``                |
+-----------------------+---------------------------------------+---------------------------------------------------+
| ``CALENDAR``          | ``id.year``, ``id.calendarNumber``    | ``/api/3/calendars/{year}/{calendarNumber}``      |
+-----------------------+---------------------------------------+---------------------------------------------------+
| ``LAW``               | ``id.lawId``, ``id.activeDate``       | ``/api/3/laws/{lawId}?full=true``                 |
+-----------------------+---------------------------------------+---------------------------------------------------+
| Hearing update        | ``hearingId.id``                      | ``/api/3/hearings/{id}``                          |
+-----------------------+---------------------------------------+---------------------------------------------------+
| Transcript update     | ``transcriptId.dateTime``,            | ``/api/3/transcripts/{dateTime}/{sessionType}``   |
|                       | ``transcriptId.sessionType``          |                                                   |
+-----------------------+---------------------------------------+---------------------------------------------------+

Bill, agenda, calendar and law updates come from the aggregate API, where ``contentType`` says which kind each one
is. Transcript session types can contain spaces, such as ``Regular Session``, so URL-encode them.

Laws are updated weekly, in one batch that can change thousands of law documents. ``/api/3/laws/{lawId}`` with
``full=true`` returns a law's whole structure and text, so one request per changed law covers all of its changed
documents.

If a request for a changed item returns HTTP 404, the item was removed. Delete your copy.

The initial load
----------------

Before your first poll, load everything you need:

1. Note New York now minus 2 minutes. This is the ``from`` of your first poll.
2. Load the items. List the ids of each kind of item, reading every page of each listing, then request each item
   in full:

   - Bills: ``/api/3/bills/{sessionYear}``
   - Agendas: ``/api/3/agendas/{year}``
   - Calendars: ``/api/3/calendars/{year}``
   - Laws: ``/api/3/laws``
   - Hearings: ``/api/3/hearings/{year}``
   - Floor transcripts: ``/api/3/transcripts/{year}``
   - Committees and members, as described below

3. Start polling from the time you noted in step 1.

Noting the time first matters. A load takes a while, and items can change during it. Polling from the start of the
load fetches those items again, possibly after you loaded them, so you end up with their latest version.

Each item is a request, and every request counts against your :ref:`rate limits<rate-limits>`, so the initial load
takes a while. Once it's done, staying current is quick: each poll fetches only the items that changed.

Committees and members
----------------------

These have no update API. Once a day, list them again for the session years you keep, and save what changed. Set
``full=true``: without it, the listings return summaries, and committee summaries leave out the members. Read every
page, as the listings are paged:

- ``/api/3/committees/{sessionYear}/{chamber}?full=true`` for each chamber
- ``/api/3/members/{sessionYear}?full=true``

Two polls in a row
------------------

This is what two consecutive polls look like when you poll every 15 minutes, using real updates from June 2, 2026.
The previous poll ran at 3:17 p.m. New York time. Its range ended 2 minutes earlier, at 3:15 p.m., so
``2026-06-02T15:15:00`` is saved as the start of the next one.

**First poll, at 3:32 p.m.** Its range ends 2 minutes earlier, at ``2026-06-02T15:30:00``. Read the aggregate
updates in that range. The first page has 100 of 117, so read a second page::

    GET /api/3/updates/2026-06-02T15:15:00/2026-06-02T15:30:00?limit=100
    GET /api/3/updates/2026-06-02T15:15:00/2026-06-02T15:30:00?limit=100&offset=101

The second page ends at 117, which is the ``total``, so the range is complete. Read the same range from the
hearing and transcript update APIs. No hearings or transcripts changed in this range, so both return a ``total``
of 0::

    GET /api/3/hearings/updates/2026-06-02T15:15:00/2026-06-02T15:30:00?limit=100
    GET /api/3/transcripts/updates/2026-06-02T15:15:00/2026-06-02T15:30:00?limit=100

All 117 updates are bill updates. Request each bill, and save it::

    GET /api/3/bills/2025/A10243
    GET /api/3/bills/2025/A6559
    ...
    GET /api/3/bills/2025/S9267

Once every bill is saved, save ``2026-06-02T15:30:00`` as the start of the next poll.

**Second poll, at 3:47 p.m.** Its range starts where the first one ended, and ends at ``2026-06-02T15:45:00``. It
has 101 aggregate updates, so it takes two pages too::

    GET /api/3/updates/2026-06-02T15:30:00/2026-06-02T15:45:00?limit=100
    GET /api/3/updates/2026-06-02T15:30:00/2026-06-02T15:45:00?limit=100&offset=101
    GET /api/3/hearings/updates/2026-06-02T15:30:00/2026-06-02T15:45:00?limit=100
    GET /api/3/transcripts/updates/2026-06-02T15:30:00/2026-06-02T15:45:00?limit=100

A change at exactly 3:30:00 p.m. is in this range, not the first one, so the two ranges meet with no gap and no
overlap. Then fetch the changed items and save ``2026-06-02T15:45:00``, as before.

.. note:: If you try these requests, the totals you get may differ from the ones shown here. The steps are the
   same.

If a poll fails partway, don't save its end. The next poll starts from the same place and reads the whole range
again, which is harmless.

How often to poll is up to you, but there's no need to poll more than once every 5 minutes.
