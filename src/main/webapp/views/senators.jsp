<%@ page language="java" import="java.util.ArrayList, gov.nysenate.services.model.Senator" pageEncoding="UTF-8"%>
<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Senators"/>
</jsp:include>
<style>

.senator-block {
  width:275px;
  height:100px;
  font-size:14px;
  line-height:15px;
  margin:10px 3px 0px;
  float:left;
}

.senator-block.a {
    style="text-decoration:none;
}

.senator-portrait {
	float:left;
	height:80px; // 55x71 aspect ratio
	width:62px;
	margin-right:12px;
}

.senator-name {
    font-size:18px;
    padding-bottom:5px;
    text-decoration:underline;
    margin:0px;
}

.senator-openleg-links {
    margin-top:12px;
}

.senator-nysenate-links {
}

.senator-district {
    margin-bottom: 0px;
}
</style>

<div id="content">
	<div class='formats'>
	    Senator Listings are available for: <a href="/legislation/senators/2015">2015</a> | <a href="/legislation/senators/2013">2013</a> | <a href="/legislation/senators/2011">2011</a> | <a href="/legislation/senators/2009">2009</a>
	</div>
    <div class="content-bg">
		<h2 class='page-title'>
			<%=request.getAttribute("sessionStart")%>-<%=request.getAttribute("sessionEnd")%> Senator Listings
		</h2>
		<br/>
	    <%
	    @SuppressWarnings("unchecked")
	    ArrayList<Senator> senators = (ArrayList<Senator>)request.getAttribute("senators");
	    for(Senator senator : senators) {
	       String searchUrl = request.getContextPath()+"/sponsor/"+senator.getShortName()+"?filter=year:"+request.getAttribute("sessionStart") +
                                                                                          " AND active:true AND stricken:false";
	       String imageUrl = "http://www.nysenate.gov/files/imagecache/senator_teaser/"+senator.getImageUrl().substring(30);
	       %>
	        <div class="senator-block">
	            <img class="senator-portrait" src="<%=imageUrl%>"/>
	            <div class="senator-name"><%=senator.getName()%></div>
	            <div class="senator-district">
	                District <%=senator.getDistrict().getNumber()%>
	            </div>
	            <div class="senator-nysenate-links">
	            <a href="<%=senator.getUrl()%>">Home</a> | <a href="<%=senator.getUrl()%>/contact">Contact</a>
	            </div>
	            <div class="senator-openleg-links">
	                <a href="<%=searchUrl%>"><%=request.getAttribute("sessionStart")%>-<%=request.getAttribute("sessionEnd")%> Legislation</a>
	            </div>
	        </div>
	    <% } %>
	</div>
</div>
<jsp:include page="/footer.jsp"/>
