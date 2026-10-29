**Open Legislation API**
========================

.. toctree::
    :hidden:
    :maxdepth: 0

    self
    conventions
    migration
    keeping_up
    bills
    calendars
    agendas
    committees
    laws
    transcripts_floor
    transcripts_ph
    members
    agg_updates
    search_api

.. important:: **API changes starting December 1, 2026**

   - Requests made with an API key are limited to 10 requests per second and 4 concurrent requests per key.
     See :ref:`rate-limits`.
   - The deprecated options that return full items from some listing, search and update endpoints, such as ``full``
     on bill listing and search, are removed. Requests that set them to true are rejected with HTTP 400. See the
     :ref:`API migration notes<api-migration>`.

   The ``/api/3`` base URL and API keys stay the same, and requests for a single item still return the full item.

What is this?
-------------

`Open Legislation`_ is a web service that delivers legislative information from the New York State Senate and Assembly
to the public in near-real time. It is used to serve legislative data for `nysenate.gov`_ and other various services.

Legislative data is sent via the Legislative Bill Drafting Commission (LBDC) in a raw file format. The data is
ingested and cleaned up internally by Open Leg and made available for consumption through a REST API.

This documentation explains how to utilize the REST API to retrieve bills, resolutions, laws, committee agendas, and more.
You will need to sign up for a free API key from the Open Legislation homepage in order to use this service.

The code is open source and available on `Github`_.

.. _Open Legislation: https://legislation.nysenate.gov
.. _nysenate.gov:    https://www.nysenate.gov
.. _Github: https://github.com/nysenate/OpenLegislation

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
waiting for it. Timing out and resending a slow request only adds to your requests in progress, so instead send at
most 4 requests at a time.

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

**Example client**

This Python example, using the `requests <https://requests.readthedocs.io>`_ library, stays within the limits. It
sends one request at a time and starts each at least 0.1 seconds after the previous one, so it never sends more than
10 requests per second. It retries GET requests that were rate limited or that failed because the service was
temporarily unavailable (HTTP 503). Before each retry it waits at least as long as ``Retry-After`` asks: after a
429 it adds random jitter to that wait, and after a 503 it backs off exponentially with random jitter. It gives up
after a few attempts. It never retries other errors, such as a 400, because sending the same request again won't fix
them. It waits up to 5 minutes for a response, and doesn't resend a request that timed out.

.. code-block:: python

    import random
    import time

    import requests

    API_ROOT = "https://legislation.nysenate.gov/api/3"
    API_KEY = "YOUR_API_KEY"
    MAX_ATTEMPTS = 5
    MIN_INTERVAL = 0.1  # Seconds between requests, to stay at 10 requests per second

    session = requests.Session()
    last_request = 0.0


    def get(path, **params):
        """GET an API path, pacing requests and retrying when rate limited or temporarily unavailable."""
        global last_request
        params["key"] = API_KEY
        for attempt in range(1, MAX_ATTEMPTS + 1):
            time.sleep(max(0, last_request + MIN_INTERVAL - time.monotonic()))
            last_request = time.monotonic()
            response = session.get(API_ROOT + path, params=params, timeout=(10, 300))
            if response.status_code not in (429, 503) or attempt == MAX_ATTEMPTS:
                response.raise_for_status()  # Never retry other errors, such as 400
                return response.json()
            retry_after = response.headers.get("Retry-After", "")
            if response.status_code == 429:
                # Wait at least as long as the server asks
                delay = int(retry_after or 1) + random.random()
            else:
                # Back off exponentially, with jitter, and at least as long as any Retry-After
                delay = random.uniform(1, 2 ** attempt)
                if retry_after.isdigit():
                    delay = max(delay, int(retry_after))
            time.sleep(delay)


    bill = get("/bills/2025/S100", view="info")
    print(bill["result"]["title"])

If you change your client to send requests in parallel, keep at most 4 in progress at a time and keep their combined
rate at or below 10 requests per second.

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
