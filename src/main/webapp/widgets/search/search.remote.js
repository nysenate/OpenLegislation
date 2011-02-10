var searchType = '';

$(function () {

	var p = $("#txtSearchBox").position();
	$("#quickresult").css({top:p.top+35, left:p.left});
	
	$("#txtSearchBox").keyup(function() 
	{

        var searchbox = $(this).val();
        
        var dataString = "pageIdx=1&pageSize=10&searchType=" + searchType + "&format=html-list&term=" + escape(searchbox);

        if(searchbox=='')
        {
                        $("#quickresult").html("");//.show();
        }
        else
        {
        //               $("#quickresult").html("").load("http://open.nysenate.gov/legislation/search/?" + dataString);
                $.ajax({
                        type: "POST",
                        url: "http://open.nysenate.gov/legislation/search/",
                        data: dataString,
                        cache: false,
                        async: true,
                        dataType: "html",
                        success: function(resulthtml)
                        {

                                $("#quickresult").html(resulthtml);

                        },
                         error:function (xhr, ajaxOptions, thrownError){
				alert(thrownError);
                        }
                });
        }

        return true;
	});

	
});