.. _getting-started:

**Getting Started**
===================

API Usage
---------

**Obtaining a key**

To register and obtain an API key, visit our main page `here <https://legislation.nysenate.gov>`_.

**Making requests**

All endpoints are under ``https://legislation.nysenate.gov/api/3``. To make an API request using your key, set the
key string as the value of the ``key`` request parameter in the request URL.

For example, if you wanted to get data for bill S1 of the 2015 session, your request would look something like this::

    GET https://legislation.nysenate.gov/api/3/bills/2015/S1?key=YOUR_API_KEY

Because your key is part of each request URL, error messages and logs that include the URL also include your
key. Remove it before you share them.

Every endpoint follows the same rules for responses, errors, paging, dates and parameters. See
:doc:`conventions`.

.. _rate-limits:

Rate limits
-----------

Starting December 1, 2026, requests made with an API key are limited per key:

- **Rate:** 10 requests per second. Short bursts above this rate are tolerated, but plan your use around 10
  requests per second.
- **Concurrency:** at most 4 requests in progress at the same time.

There is no daily quota. All requests made with the same key share its limits, whatever IP address or session they
come from.

The limits apply to every request under ``/api/3``, except for ``OPTIONS`` requests (such as CORS preflight
requests).

A request counts against the concurrency limit until the server finishes it, even if your client has stopped
waiting for it. Timing out and resending a slow request only adds to your requests in progress, so give slow requests
time to finish instead.

If your use needs more capacity than these limits allow, contact us at openlegislation@nysenate.gov.

**When a limit is exceeded**

A request over either limit is rejected with HTTP status 429 (Too Many Requests). The response has a
``Retry-After`` header with the number of seconds to wait before retrying (a whole number, at least 1), and an error
body with ``errorCode`` 703:

.. code-block:: javascript

    {
      "success": false,
      "message": "...",              // Human-readable explanation
      "responseType": "error",
      "errorCode": 703               // Rate limit exceeded
    }

Base your client's handling on the status code, the ``errorCode`` and the ``Retry-After`` header. The ``message`` is
meant for people: it may say which limit was exceeded, and its wording may change.

**Staying within the limits**

- Send your requests one at a time, each started at least 0.1 seconds after the previous one. This keeps you
  within both limits. If you do send requests in parallel, stay within the current concurrency limit and keep
  their combined rate at or below 10 requests per second.
- After a 429, wait at least as long as ``Retry-After`` asks before retrying, and add a little random jitter so
  that retries don't all arrive at once.
- After a 503 (the service is temporarily unavailable), back off exponentially with random jitter, waiting at
  least as long as any ``Retry-After``.
- Give up after a few attempts.
- Don't retry other errors, such as a 400. Sending the same request again won't fix them.

Legislative Content Types
-------------------------

We currently offer the following types of data:

   - Bills and Resolutions
   - Committee Agendas
   - Senate Calendars
   - NYS Laws
   - Senate Floor and Hearing Transcripts
   - Committees
   - Members

Terminology
-----------

First let's define some common legislative terminology

:Session Year:
    A legislative session year in New York State comprises of two years, with the first year being an odd numbered year.

:Bill:
    A bill is passed with the intention of amending or creating a specific portion of NYS Law.

:Resolution:
    A resolution does not necessarily impact law and are often introduced to provide honorable mentions.

:Calendar:
    A legislative calendar is a snapshot of the status of bills that are under discussion at a particular time.
    A calendar will typically contain a floor calendar and an active list, along with supplemental floor calendars and active lists.

:Floor Calendar:
    The floor calendar is a listing of all bills that are under discussion (on the floor).  A bill must be read on the
    senate floor a minimum of three times to be eligible for a vote, and the floor calendar will indicate the number of
    readings for each bill at its time of publication.

:Active List:
    An active list is a listing of bills that are scheduled for discussion during a single senate session.

:Committee:
    Committees are groups of senators that are focused on particular areas of law.  Bills must be approved by a committee
    in order to be reported to the senate floor.

:Uni-Bill:
    A uni bill is a bill that is sent through both chambers concurrently
