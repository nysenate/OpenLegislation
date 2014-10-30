$(function () {
    
    $("#txtSearchBox").blur(function() {
        $('#quickresult').animate({
            opacity:0.0
        }, 250, 'swing');
    });
    
    $("#txtSearchBox").focus(function() {
        var p = $("#txtSearchBox").position();
        $("#quickresult")
            .css({top:p.top+35, left:p.left})
            .animate({
                opacity:1.0
            }, 250, 'swing');
    });
    
    $("#txtSearchBox").keyup(function()  {
        var searchbox = $(this).val();
        var dataString = "pageIdx=1&pageSize=10&searchType=&format=html-list&search=" + escape(searchbox);

        if(searchbox=='') {
            $("#quickresult").hide().html("");
        }
        else {
            $.ajax({
                type: "GET",
                url: window.ctxPath +"/search/",
                data: dataString,
                cache: false,
                async: true,
                dataType: "html",
                success: function(resulthtml) {
                    $("#quickresult").html("<span class='searchCarrot'></span><span class='searchResults'>"+resulthtml+"</span>").show();;
                },
                error:function (xhr, ajaxOptions, thrownError){
                       $("#quickresult").html("");
                }
            });
        }
        return true;
    });
});
