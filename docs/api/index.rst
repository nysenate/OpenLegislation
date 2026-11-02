**Open Legislation API**
========================

.. toctree::
    :hidden:
    :maxdepth: 0

    self
    getting_started
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

`Open Legislation`_ is a web service that delivers legislative information from the New York State Senate to the
public in near-real time. It is used to serve legislative data for `nysenate.gov`_ and other various services.

Legislative data is sent via the Legislative Bill Drafting Commission (LBDC) in a raw file format. The data is
ingested and cleaned up internally by Open Leg and made available for consumption through a REST API.

This documentation explains how to utilize the REST API to retrieve bills, resolutions, laws, committee agendas, and more.
You will need to sign up for a free API key from the Open Legislation homepage in order to use this service.
To get a key and make your first request, see :doc:`getting_started`. The rules every endpoint shares are in
:doc:`conventions`.

The code is open source and available on `Github`_.

.. _Open Legislation: https://legislation.nysenate.gov
.. _nysenate.gov:    https://www.nysenate.gov
.. _Github: https://github.com/nysenate/OpenLegislation
