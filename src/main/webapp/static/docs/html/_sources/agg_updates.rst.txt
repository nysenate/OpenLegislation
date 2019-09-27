**Aggregate Updates API**
=========================

----------

Get aggregate updates
---------------------

**Usage**

List of content that was updated during the given date/time range
::
    /api/3/updates/{fromDateTime}
    /api/3/updates/{fromDateTime}/{toDateTime}

.. note:: The fromDateTime and toDateTime should be formatted as the ISO Date Time format. For example December 10, 2014, 1:30:02 PM should be inputted as 2014-12-10T13:30:02

**Optional Params**

+--------------+----------------------+--------------------------------------------------------+
| Parameter    | Values               | Description                                            |
+==============+======================+========================================================+
| type         | (processed|published)| The type of bill update (see below for explanation)    |
+--------------+----------------------+--------------------------------------------------------+
| detail       | boolean              | Set to true to see detailed update digests             |
+--------------+----------------------+--------------------------------------------------------+
| fields       | boolean              | Set to true to get updated fields with detailed digests|
+--------------+----------------------+--------------------------------------------------------+
| content-type | string[]             | Filter by content type (AGENDA, BILL, CALENDAR, LAW)   |
+--------------+----------------------+--------------------------------------------------------+
| order        | string (asc|desc)    | Order the results by update date/time                  |
+--------------+----------------------+--------------------------------------------------------+
| limit        | integer              | Number of results to return                            |
+--------------+----------------------+--------------------------------------------------------+
| offset       | integer              | Result number to start from                            |
+--------------+----------------------+--------------------------------------------------------+

There are two types of updates, 'processed' and 'published'. Processed refers to the date that OpenLeg processed
the data which is useful if you are trying to stay synchronized with OpenLeg. Published refers to the date during
which data was intended to be published. This can differ from the processed date because OpenLeg can periodically
reprocess it's data to fix issues. By default the type is set to published.

**Example**

Get a detailed view of the first 50 agenda, bill, and calendar updates processed on March 17 2015
::
    /api/3/updates/2019-01-17T00:00:00/2019-01-18T00:00:00?type=processed
                    &content-type=AGENDA&content-type=BILL&content-type=CALENDAR
                    &detail=true&fields=true&limit=50

**Response**

See the following pages for info on the unique updates responses for each content type:

| :ref:`agenda update token response<agenda-update-token-response>`, :ref:`agenda update digest response<agenda-update-digest-response>`
| :ref:`bill update token response<bill-update-token-response>`, :ref:`bill update digest response<bill-update-digest-response>`
| :ref:`calendar update token response<calendar-update-token-response>`, :ref:`calendar update digest response<calendar-update-digest-response>`
| :ref:`law update token response<law-update-token-response>`, :ref:`law update digest response<law-update-digest-response>`

----

.. code-block:: javascript

    {
      "success" : true,
      "message" : "",
      "responseType" : "update-token list",
      "total" : 11836,
      "offsetStart" : 1,
      "offsetEnd" : 50,
      "limit" : 50,
      "result" : {
        "items" : [
          {
            "id" : {                        // Content identifier
              "basePrintNo": "S1948",         // See content-specific updates pages
                "session": 2019,
                "basePrintNoStr": "S1948-2019"
            },
            "contentType" : "BILL",
            "sourceId": "2019-01-17-20.22.35.839069_SENMEMO_S01948.XML-1-SENMEMO",,   // Id of the source that triggered the update
            "sourceDateTime" : "2019-01-17T20:22:35.839069",  // Published date of the source document
            "processedDateTime" : "2019-01-17T20:26:24.673778", // Time when the update occurred

            // --- The following fields are returned only for update digests -------------------
            "action" : "Update",    // Update action (Insert|Update|Delete)
            "scope" : "Bill Amendment",   // Designates which part of the content was updated
            "fields" : { }  // The updated fields, returned only if fields parameter is true
            "fieldCount": 1
            // ---------------------------------------------------------------------------------
          },
          ...
        ],
        "size" : 50
      },
      "fromDateTime" : "2014-01-10T00:00",
      "toDateTime" : "2015-03-18T13:16:17.383999999"
    }

.. warning:: This example uses processed date time. As we reprocess our data periodically, it's possible this specific api call may not produce the result shown. However, the response you receive will follow the format in the example