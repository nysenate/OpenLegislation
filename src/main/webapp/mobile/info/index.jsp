<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*" pageEncoding="UTF-8"%>

<%

String userAgent = request.getHeader("user-agent").toLowerCase();
String header = "/header.jsp";

if (userAgent.indexOf("mobile") != -1 || userAgent.indexOf("wap") != -1 || userAgent.indexOf("blackberry")!=-1
|| userAgent.indexOf("wml")!=-1 || userAgent.indexOf("nokia")!=-1|| userAgent.indexOf("midp")!=-1 ||userAgent.indexOf("mobi")!=-1)
 {

      header = "/views/mobile-header.jsp";
}

 %>
<jsp:include page="<%=header%>"/>


 <div id="content">
<p>
The NY Senate OpenLeg services provides a variety of ways to access the latest information on the legislation you are interested using only a mobile phone.
Whether you just want to send a text message or browse the full site on the go, you can! The list below outlines the various methods for accessing the services.

<br/><br/>
<em>A number of the services below were developed by using the OpenLeg Developer API by a third-party developer. <a href="http://www.voiceingov.org/blog/?p=1136&cpage=1">Learn more...</a></em>
</p>

 
<h2>Interactive Voice Phone Call</h2>
<ul>
<li>Call (646) 736-2439
<p>
<em>Dial the number, then when asked, enter the bill number you are looking for. You will hear a digital voice read back the latest information about the legislation you are interested in. </em>
</p>
</li>
</ul>

<h2>SMS Text Messaging</h2>
<ul>

<li>Send a text message to 41411 with the keyword 'nysenate' followed by the bill number or Senator last name

<p>
<em>Example: text 'nysenate S1234' or 'nysenate Adams' to 41411</em>
</p></li>
<li>Send a text message to (315) 308-1943</li>
<em>Example: Send a bill number to the phone number above - 'S2111' to 3153081943</em>
</ul>
<h2>iPhone, Android and other Smartphones</h2>
<ul>
<li>Browse to <a href="http://open.nysenate.gov/openleg">http://open.nysenate.gov/openleg</a> on your mobile web browser
<p><em>The web content is formatted to be compatible with mobile webkit browsers on modern smartphones.</em></p>
</li>
<li>Subscribe via a Mobile RSS Reader: <a href="http://open.nysenate.gov/openleg/feed/">http://open.nysenate.gov/openleg/feed/</a>
<p><em>The RSS feed will contain the latest bill actions from the Senate floor.</em></p>
</li>
<li>Instant Messaging Client (Jabber): opensenate@bot.im
<p><em>Example: add opensenate@bot.im as a buddy, then send 'S2111' via an IM message using Google Talk or any other Jabber client.</em></p>
</li>
<li>Twitter Notifications: Follow <a href="http://twitter.com/nysenateopenleg">@nysenateopenleg</a>
<p><em>Bill action updates will be broadcast via Twitter as they happen.</em></p>
</li>
<li>Twitter Search: Send a tweet formatted as a @reply to <a href="http://twitter.com/openseante">@opensenate</a>
<p><em>Example: @opensenate S2111</em></p>
</li>
</ul>
</div>

 
   
 <jsp:include page="../../footer.jsp"/>
   
    
