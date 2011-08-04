var searchType = '';

$(function () {
	
	$("#txtSearchBox").blur(function() {
		$('#quickresult').animate({
			opacity:0.0
		}, 250, 'swing');
	});
	
	$("#txtSearchBox").focus(function() {
		$('#quickresult').animate({
			opacity:1.0
		}, 250, 'swing');
	});
	
	$("#txtSearchBox").keyup(function() 
	{
        var searchbox = $(this).val();
        
        var dataString = "pageIdx=1&pageSize=10&searchType=" + searchType + "&format=html-list&search=" + escape(searchbox);

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
                        cache: true,
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