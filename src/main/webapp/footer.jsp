<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

		<div id="footer">
			<div id="footer-message"><p>
				<a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">
				<img class="cc-logo" alt="Creative Commons License" src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png" align='left' />
				</a> This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/us/">Creative Commons Attribution-Noncommercial-No Derivative Works 3.0 United States License</a>.<br /> Permissions beyond the scope of this license are available at <a cc="http://creativecommons.org/ns#" href="http://www.nysenate.gov/copyright-policy" rel="morePermissions">http://www.nysenate.gov/copyright-policy</a>.
	 		</p></div>
			The software and services provided under this site are offered under the BSD License and the GPL v3 License.<br/>
		</div>
		<script type="text/javascript">
			var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
			document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
		</script>
		<script type="text/javascript">
			try {
				var pageTracker = _gat._getTracker("UA-8171983-6");
				pageTracker._trackPageview();
			} catch(err) {}
		</script>
		<br/><br/>
		<script type="text/javascript">
			var _sf_async_config={uid:2873,domain:"open.nysenate.gov"};
			(function(){
			  function loadChartbeat() {
			    window._sf_endpt=(new Date()).getTime();
			    var e = document.createElement('script');
			    e.setAttribute('language', 'javascript');
			    e.setAttribute('type', 'text/javascript');
			    e.setAttribute('src',
			       (("https:" == document.location.protocol) ? "https://s3.amazonaws.com/" : "http://") +
			       "static.chartbeat.com/js/chartbeat.js");
			    document.body.appendChild(e);
			  }
			  var oldonload = window.onload;
			  window.onload = (typeof window.onload != 'function') ?
			     loadChartbeat : function() { oldonload(); loadChartbeat(); };
			})();
		</script>
		
		<!-- Percent Mobile Support. For mobile viewing, place as far down as possible -->
		
		<% if (session.getAttribute("mobile")!=null) { %>
			<script>
				<!--
				percent_mobile_track('89984697771243267044235791550489069012');
				-->
			</script>
			<noscript>
				<img src="http://tracking.percentmobile.com/pixel/89984697771243267044235791550489069012/pixel.gif?v=271009_js" width="2" height="2" alt="" />
			</noscript>
		<% } %>
		<!-- End Percent Mobile Support -->
		
	</body>
</html>
