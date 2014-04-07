<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:include page="/admin/header.jsp">
    <jsp:param value="Data Viewer - OpenLeg Admin" name="title"/>
</jsp:include>
<div id="section-header">

    <div style="float:right;padding:10px;">
        <form id="search-form" action="">
            <select name="docType">
                <option value="agenda">Agenda</option>
                <option value="bill" selected>Bills</option>
                <option value="calendar">Calendars</option>
            </select>
            <input type="number" name="searchYear" value="2013"/>
            <input type="text" name="searchStr" />
            <button type="submit">Search</button>
        </form>
    </div>

    <div id="section-title">Data Viewer</div>
</div>
<div id="results"></div>

<script>
    $("#search-form").submit(function(event){
        event.preventDefault();
        var postData = $(this).serialize();
        $.post('<%= request.getContextPath() + "/admin/data" %>', postData)
            .done(function(data){
                $("#results").html(data);
            });
    });

    $("#results").height($(window).height() - $("#results").offset().top);
</script>
<jsp:include page="/admin/footer.jsp" />
