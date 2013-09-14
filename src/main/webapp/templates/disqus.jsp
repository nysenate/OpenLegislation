<%@ page language="java" contentType="text/html" pageEncoding="utf-8"%>
<%
	String title = request.getParameter("title");
	String disqusUrl = request.getParameter("disqusUrl");

	if (disqusUrl != null) {
%>
<div id="content">
   	<h3 class="section" ><a id="Comments" href="#Comments" class="anchor ui-icon ui-icon-link"></a>Comments</h3>
    <div id="comments">    
		<p class='comment_terms'>
	       Open Legislation comments facilitate discussion of New York State legislation. All comments are subject to moderation.
	       Comments deemed off-topic, commercial, campaign-related, self-promotional; or that contain profanity or hate speech;
	       or that link to sites outside of the nysenate.gov domain are not permitted, and will not be published. Comment moderation
	       is generally performed Monday through Friday.<br/><br/>
	       <b>By contributing or voting you agree to the <a href = "http://nysenate.gov/legal">Terms of Participation</a> and verify you are over 13.</b>
		</p>
		<h3><a>Discuss!</a></h3>
		<div id="disqus_thread">
            <script type="text/javascript">
			    /* * * CONFIGURATION VARIABLES: EDIT BEFORE PASTING INTO YOUR WEBPAGE * * */
			    var disqus_shortname = 'nysenateopenleg'; // required: replace example with your forum shortname

			    // The following are highly recommended additional parameters. Remove the slashes in front to use.
			    var disqus_identifier = '<%=disqusUrl%>';
			    var disqus_url = '<%=disqusUrl%>';
			    var disqus_developer = 0; // developer mode is off
			    var disqus_title = '<%=title%>';

			    /* * * DON'T EDIT BELOW THIS LINE * * */
			    (function() {
			        var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
			        dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js';
			        (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
			    })();
			</script>
			<noscript>Please enable JavaScript to view the <a href="http://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
			<a href="http://disqus.com" class="dsq-brlink">blog comments powered by <span class="logo-disqus">Disqus</span></a>
        </div>
	</div>
</div>

</div>
<% } %>

