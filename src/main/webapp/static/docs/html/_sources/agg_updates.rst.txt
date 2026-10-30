**Aggregate Updates API**
=========================


Get aggregate updates
---------------------

To keep a copy of Open Legislation data current with this API, see :ref:`Keeping Up with Changes<keeping-up>`.

**Usage**

List of content that was updated during the given date/time range::

    GET /api/3/updates/{fromDateTime}
    GET /api/3/updates/{fromDateTime}/{toDateTime}

With no ``toDateTime`` the range runs to now. A ``fromDateTime`` is required: ``/api/3/updates`` on its own is not an API route.

**Optional Params**

+--------------+----------------------+--------------------------------------------------------+
| Parameter    | Values               | Description                                            |
+==============+======================+========================================================+
| type         | (processed|published)| Which update date to filter and order by (see below).  |
|              |                      | Default processed.                                     |
+--------------+----------------------+--------------------------------------------------------+
| detail       | boolean              | Set to true to see detailed update digests             |
+--------------+----------------------+--------------------------------------------------------+
| fields       | boolean              | Set to true to get updated fields with detailed        |
|              |                      | digests. Without it, ``fields`` is empty and           |
|              |                      | ``fieldCount`` is 0.                                   |
+--------------+----------------------+--------------------------------------------------------+
| content-type | string[]             | Filter by content type (AGENDA, BILL, CALENDAR, LAW).  |
|              |                      | Repeat to request several. Unrecognized values are     |
|              |                      | ignored; if none are recognized, all types are         |
|              |                      | returned.                                              |
+--------------+----------------------+--------------------------------------------------------+
| order        | string (asc|desc)    | Order the results by update date/time. Default desc,   |
|              |                      | newest first.                                          |
+--------------+----------------------+--------------------------------------------------------+
| limit        | 1 - 1000             | Number of results to return. Default 50.               |
+--------------+----------------------+--------------------------------------------------------+
| offset       | >= 1                 | Result number to start from. Default 1.                |
+--------------+----------------------+--------------------------------------------------------+

There are two types of updates, 'processed' and 'published'. Processed refers to the date that OpenLeg processed
the data which is useful if you are trying to stay synchronized with OpenLeg. Published refers to the date during
which data was intended to be published. This can differ from the processed date because OpenLeg can periodically
reprocess its data to fix issues. By default the type is set to processed.

.. note:: Unlike the content-specific update APIs, this one returns the newest updates first by default.
   To read a range from oldest to newest, set ``order=asc``.

**Example**

Get a detailed view of the 50 most recent agenda, bill, and calendar updates processed on March 4, 2025::

    GET /api/3/updates/2025-03-04T00:00:00/2025-03-05T00:00:00?type=processed
                        &content-type=AGENDA&content-type=BILL&content-type=CALENDAR
                        &detail=true&fields=true

**Response**

With ``detail=true`` the items are update digests and the responseType is ``update-digest list``.
Without it, they are update tokens, with only the fields above the dashed comment below, and the
responseType is ``update-token list``. A page with no items has the responseType ``empty list``.

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-digest list",
      "total" : 5673,
      "offsetStart" : 1,
      "offsetEnd" : 50,
      "limit" : 50,
      "result" : {
        "items" : [
          {
            "id" : {                        // Content identifier
              "basePrintNo" : "S6031",        // See content-specific updates pages
              "session" : 2025,
              "basePrintNoStr" : "S6031-2025"
            },
            "contentType" : "BILL",
            "sourceId" : "2025-03-04-19.06.06.864349_SENMEMO_S06031.XML-1-SENMEMO",   // Id of the source that triggered the update
            "sourceDateTime" : "2025-03-04T19:06:06.864349",  // Published date of the source document
            "processedDateTime" : "2025-03-04T19:11:04.603729", // Time when the update occurred

            // --- The following fields are returned only for update digests -------------------
            "action" : "Update",    // Update action (Insert|Update|Delete)
            "scope" : "Bill Amendment",   // Designates which part of the content was updated
            "fields" : {            // The updated fields, returned only if fields parameter is true
              "Sponsor Memo" : " \nBILL NUMBER: S6031\n\nSPONSOR: PARKER\n..."
            },
            "fieldCount" : 1        // Number of updated fields, 0 unless fields parameter is true
            // ---------------------------------------------------------------------------------
          },
          ...
        ],
        "size" : 50
      },
      "fromDateTime" : "2025-03-04T00:00:00",       // First instant in the range
      "toDateTime" : "2025-03-04T23:59:59.999999"   // Last instant in the range
    }

See the following pages for info on the unique update responses for each content type:

* :ref:`agenda update token response<agenda-update-token-response>`
* :ref:`agenda update digest response<agenda-update-digest-response>`
* :ref:`bill update token response<bill-update-token-response>`
* :ref:`bill update digest response<bill-update-digest-response>`
* :ref:`calendar update token response<calendar-update-token-response>`
* :ref:`calendar update digest response<calendar-update-digest-response>`
* :ref:`law update token response<law-update-token-response>`
* :ref:`law update digest response<law-update-digest-response>`

Hearings and floor transcripts are not part of this API. They have their own update APIs:
:ref:`hearing updates<hearing-updates>` and :ref:`transcript updates<transcript-updates>`.


.. warning:: This example uses processed date time. As we reprocess our data periodically, it's possible this specific api call may not produce the result shown. However, the response you receive will follow the format in the example
