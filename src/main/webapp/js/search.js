var searchType = '';

$(function () {
	
	$("#txtSearchBox").keyup(function() 
	{
       // lastKeyPressCode = e.keyCode;

        var searchbox = $(this).val();
        
        var dataString = "pageIdx=1&pageSize=10&searchType=" + searchType + "&format=html-list&term=" + escape(searchbox);

        if(searchbox=='')
        {
        	document.getElementById("quickresult").style.visibility = "hidden";
                        $("#quickresult").html("");//.show();
        }
        else
        {
        	document.getElementById("quickresult").style.visibility = "visible";
                $.ajax({
                        type: "GET",
                        url: "/legislation/search/",
                        data: dataString,
                        cache: false,
                        async: true,
                        dataType: "html",
                        success: function(resulthtml)
                        {

                                $("#quickresult").html(resulthtml);

                        },
                         error:function (xhr, ajaxOptions, thrownError){
                        	   $("#quickresult").html("");
                        }
                });
        }

        return true;
	});

	
});