<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*" pageEncoding="UTF-8"%>
<jsp:include page="/header.jsp"/>
<div id="content">
    <div class="content-bg">
    <h2 class='page-title'>Open Legislation Recent Comments.</h2>
		<div style="float:left;width:550px">
            <div id="dsq-recentcomments" class="dsq-widget">
		        <script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/recent_comments_widget.js?num_items=25&avatar_size=48&excerpt_length=300"></script>
		    </div>
		    <a href="http://disqus.com">Powered by Disqus</a>
		</div>
		<div style="float:left;width:250px;margin:20px 0 0 55px;">
		    <div id="dsq-popthreads" class="dsq-widget">
                <h2 class="dsq-widget-title">Popular Threads</h2>
                <script type="text/javascript" src="http://disqus.com/forums/nysenateopenleg/popular_threads_widget.js?num_items=20"></script>
            </div>
            <a href="http://disqus.com">Powered by Disqus</a>
		</div>
		<br style="clear:both;"/>
	 </div>
</div>
<jsp:include page="/footer.jsp"/>
