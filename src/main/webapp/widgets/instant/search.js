var searchType = '';
var maxResults = 25

$(function () {

	var p = $("#txtSearchBox").position();
	$("#quickresult").css({top:p.top+25, left:p.left});
	
	$("#txtSearchBox").keyup(function() 
	{

        var searchbox = $(this).val();
        
        var dataString = "pageIdx=1&pageSize=" + maxResults + "&searchType=" + searchType + "&format=json&term=" + escape(searchbox);

        if(searchbox=='')
        {
                        $("#content").html("");//.show();
        }
        else
        {
                   //     $("#quickresult").html("").load("http://open.nysenate.gov/legislation/search/?" + dataString);
			
                $.ajax({
                        type: "GET",
                        url: "/legislation/search/",
                        data: dataString,
                        cache: true,
                        async: true,
                        dataType: "json",
                        success: function(resultdata)
                        {

                               $("#content").html("");
                        		
                        	 $.each(resultdata, function(i,result){
                        		 

                        		 var resultPath = '/legislation/' + result.type + '/' + result.id;
                                 var content = '<div class="billSummary" onmouseover="this.style.backgroundColor=#FFFFCC" onmouseout="this.style.backgroundColor=#FFFFFF">';
                                 content += '<a href="' + resultPath + '">' + result.type.toUpperCase() + ': ' + result.title + '</a>';
                                 content += '<div style="font-size:90%;color:#777777;">';
                                 
                                 if (result.sponsor)
                                	 content += 'Sponsor: ' + result.sponsor + ' ';
                                 
                                 if (result.committee)
                                	 content += 'Committee: ' + result.committee + ' ';
                                 
                                 if (result.chair)
                                	 content += 'Chair: ' + result.chair + ' ';
                                 
                                 if (result.location)
                                	 content += 'Location: ' + result.location + ' ';
                                	 
                                 content += '</div></div>';
                                 $(content).appendTo("#content");
                               });
                        	 
                        	
                        },
                         error:function (xhr, ajaxOptions, thrownError){
				//alert(thrownError);
                        }
                });
	
        }

        return true;
	});

	
});
