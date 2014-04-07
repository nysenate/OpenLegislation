
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<style>
    .json-key {
        color: brown;
    }
    .json-value {
        color: navy;
    }
    .json-string {
        color: rgb(66, 50, 0);
    }

    .bill-info-header {
        padding: 5px;
        border-bottom: 1px solid #ddd;
    }

    .sobi-line-num {
        padding-right: 5px;
        margin-right: 10px;
        color: teal;
        border-right: 1px solid #ccc;
    }

    .sobi-file-header {
        padding: 5px;
        text-align: center;
        border: 1px #ddd;
        border-style: solid none solid none;
        background: #fafafa;
        color: teal;
    }

    .sobi-text {
        margin: 10px 0;
    }

    .date-hint {
        margin-left: 20px;
        color: brown;
    }
</style>

<div class="pane ui-layout-west" style="float:left;width:40%;height:100%;border-right:1px solid #ddd;overflow-y:scroll;">
    <h3 class="bill-info-header"><%= request.getAttribute("billId") %></h3>
    <pre style="font-size:12px;font-weight:normal;" id="json"></pre>
</div>
<div class="pane ui-layout-center" style="width:59.5%;height:100%;border-right:1px solid #ddd;overflow-y:scroll;">
    <div style="font-size:12px;font-weight:normal;" id="sobis"></div>
</div>

<script>
    if (!library)
        var library = {};

    library.json = {
        replacer: function(match, pIndent, pKey, pVal, pEnd) {
            var key = '<span class=json-key>';
            var val = '<span class=json-value>';
            var str = '<span class=json-string>';
            var r = pIndent || '';
            if (pKey)
                r = r + key + pKey.replace(/[": ]/g, '') + '</span>: ';
            if (pVal)
                r = r + (pVal[0] == '"' ? str : val) + pVal + '</span>';
            return r + (pEnd || '');
        },
        prettyPrint: function(obj) {
            var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
            return JSON.stringify(obj, null, 3)
                    .replace(/&/g, '&amp;').replace(/\\"/g, '&quot;')
                    .replace(/</g, '&lt;').replace(/>/g, '&gt;')
                    .replace(jsonLine, library.json.replacer);
        }
    };
</script>

<script>
    var billJson = <%= request.getAttribute("billJson") %>;
    var sobisJson = <%= request.getAttribute("sobiListJson") %>;
    $("#json").html(library.json.prettyPrint(billJson));
    $.each(sobisJson, function(i,v) {
        var parsedDate = parseDateFromSobi(i);
        parsedDate = (parsedDate) ? parsedDate.toLocaleString() : 'Unknown date';
        $("#sobis").append("<h3 class='sobi-file-header'>" + i + "<span class='date-hint'>" + parsedDate +
            "</span></h3><pre class='sobi-text'>" + v + "</pre>");
    });

    function parseDateFromSobi(sobiFile) {
        if (sobiFile) {
            var matches = sobiFile.match(/SOBI.D(\d+).T(\d+)/);
            if (matches[1] && matches[2]) {
                var year = '20' + matches[1].substring(0,2);
                var month = matches[1].substring(2,4) - 1;
                var day = matches[1].substring(4,6);
                var hour = matches[2].substring(0,2);
                var minute = matches[2].substring(2,4);
                var second = matches[2].substring(4,6);
                return new Date(year, month, day, hour, minute, second);
            }
        }
    }

</script>
