<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%

String appPath = request.getContextPath();
session.removeAttribute("term");

String searchPath = appPath + "/sponsor";

Bill bill = null;
String last = null;
DateFormat df = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);


String legTypeFilter = request.getParameter("type");

String stringEventSearch = request.getParameter("action");

if (stringEventSearch == null)
	stringEventSearch = "Passed";
	
Date startDate = null;
Date endDate = null;

if (request.getParameter("year") != null)
{
	int year = Integer.parseInt(request.getParameter("year"));
	int month = Integer.parseInt(request.getParameter("month"));
	int day = Integer.parseInt(request.getParameter("day"));
	
	Calendar now = Calendar.getInstance();
	now.clear();
	now.set(year,month-1,day,0,0,0);
	startDate = now.getTime();
	
	now.set(year,month-1,day,11,59,59);
	endDate = now.getTime();
}

long start = 0;
long end = 25;

String actionSearchKey = "actionSearchKeyFoo";

%>

<jsp:include page="../header.jsp">
	<jsp:param name="title" value="Senators"/>
</jsp:include>
<style>
.views-row
{
float:left;
margin:6px;
width:140px;
height:130px;
font-size:9pt;
line-height:13px;
}

.contact, .social_buttons
{
display:none;
}
</style>

	<h2>Senators</h2>
 <div id="content">
  <div class="views-row views-row-2 views-row-even">

<div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/adams"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(02-04-09) Adams-HS-059NEW HEADSHOT_1.JPG" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/adams">Adams, Eric</a><span class="contact"> | <a href="<%=searchPath%>/eric-adams/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/adams">District 20</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/eric-adams/content/feed" class="rss" title="RSS feed of Eric Adams&#039;s content">RSS</a><a href="http://twitter.com/NYSSenAdams" class="twitter" title="Follow Eric Adams on Twitter">Twitter</a><a href="http://www.facebook.com/pages/New-York-State-Senator-Eric-Adams/80559174013" class="facebook" title="Eric Adams&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-2 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/addabbo"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Addabbo.SD15.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/addabbo">Addabbo, Joseph P., Jr</a><span class="contact"> | <a href="<%=searchPath%>/joseph-p-addabbo-jr/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/addabbo">District 15</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/joseph-p-addabbo-jr/content/feed" class="rss" title="RSS feed of Joseph P. Addabbo Jr&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Senator-Joe-Addabbo/157310354545?ref=search&amp;sid=567176665.3736433734..1" class="facebook" title="Joseph P. Addabbo Jr&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-3 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/alesi"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Senator Alesi - 55th- Headshot 6-2008.JPG" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/alesi">Alesi, James S.</a><span class="contact"> | <a href="<%=searchPath%>/james-s-alesi/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/alesi">District 55</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/james-s-alesi/content/feed" class="rss" title="RSS feed of James S. Alesi&#039;s content">RSS</a><a href="http://twitter.com/senatoralesi" class="twitter" title="Follow James S. Alesi on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Jim-Alesi/134704380636" class="facebook" title="James S. Alesi&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-4 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/aubertine"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/2009AubertineHeadshot.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/aubertine">Aubertine, Darrel J.</a><span class="contact"> | <a href="<%=searchPath%>/darrel-j-aubertine/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/aubertine">District 48</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/darrel-j-aubertine/content/feed" class="rss" title="RSS feed of Darrel J. Aubertine&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-5 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/bonacic"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/42ProfilePic Bonacic.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/bonacic">Bonacic, John J.</a><span class="contact"> | <a href="<%=searchPath%>/john-j-bonacic/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/bonacic">District 42</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/john-j-bonacic/content/feed" class="rss" title="RSS feed of John J. Bonacic&#039;s content">RSS</a><a href="http://twitter.com/johnbonacic" class="twitter" title="Follow John J. Bonacic on Twitter">Twitter</a><a href="http://www.facebook.com/profile.php?id=1547091564&amp;ref=profile" class="facebook" title="John J. Bonacic&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-6 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/breslin"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Breslin202009.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/breslin">Breslin, Neil D.</a><span class="contact"> | <a href="<%=searchPath%>/breslin/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/breslin">District 46</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/breslin/content/feed" class="rss" title="RSS feed of Neil D. Breslin&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-7 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/defrancisco"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD50_Defrancisco.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/defrancisco">DeFrancisco, John A.</a><span class="contact"> | <a href="<%=searchPath%>/john-a-defrancisco/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="<%=searchPath%>/defrancisco">District 50</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/defrancisco/content/feed" class="rss" title="RSS feed of John A. DeFrancisco&#039;s content">RSS</a><a href="http://twitter.com/JohnDeFrancisco" class="twitter" title="Follow John A. DeFrancisco on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Senator-John-A-DeFrancisco/8776617150" class="facebook" title="John A. DeFrancisco&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-8 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/diaz"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Diaz-HS-2009.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/diaz">Diaz, Ruben</a><span class="contact"> | <a href="<%=searchPath%>/diaz/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#32">District 32</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/diaz/content/feed" class="rss" title="RSS feed of Ruben Diaz&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-9 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/dilan"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(02-11-09) Dilan-HS-046.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/dilan">Dilan, Martin Malavé</a><span class="contact"> | <a href="<%=searchPath%>/martin-malav%C3%A9-dilan/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#17">District 17</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/dilan/content/feed" class="rss" title="RSS feed of Martin Malavé Dilan&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Martin-Malave-Dilan/80483802077" class="facebook" title="Martin Malavé Dilan&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-10 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/duane"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/DuaneHeadshot07ColorBIO.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/duane">Duane, Thomas</a><span class="contact"> | <a href="<%=searchPath%>/thomas-k-duane/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#29">District 29</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/duane/content/feed" class="rss" title="RSS feed of Thomas K. Duane&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-11 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/espada"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(01-12-09) Espada-HS-010.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/espada">Espada, Pedro, Jr.</a><span class="contact"> | <a href="<%=searchPath%>/espada-majority-leader/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#33">District 33</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/espada/content/feed" class="rss" title="RSS feed of Pedro Espada, Jr., Majority Leader &#039;s content">RSS</a><a href="http://twitter.com/SenEspada" class="twitter" title="Follow Pedro Espada, Jr., Majority Leader  on Twitter">Twitter</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-12 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/farley"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD44_Farley.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/farley">Farley, Hugh T.</a><span class="contact"> | <a href="<%=searchPath%>/farley/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#44">District 44</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/farley/content/feed" class="rss" title="RSS feed of Hugh T. Farley&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-13 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/flanagan"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/flanaganheadshot2.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/flanagan">Flanagan, John J.</a><span class="contact"> | <a href="<%=searchPath%>/flanagan/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#2">District 2</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/flanagan/content/feed" class="rss" title="RSS feed of John J. Flanagan&#039;s content">RSS</a><a href="http://www.youtube.com/user/senatorflanagan" class="youtube" title="Follow John J. Flanagan on YouTube">YouTube</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-14 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/foley"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/FoleyWebBioHeadshot[1]_0.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/foley">Foley, Brian X</a><span class="contact"> | <a href="<%=searchPath%>/foley/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#3">District 3</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/foley/content/feed" class="rss" title="RSS feed of Brian X Foley&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-15 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/fuschillo"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/forwebBROCHURESSENATORFUSCHILLO'SOFFICIALHEADSHOT.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/fuschillo">Fuschillo, Charles J., Jr.</a><span class="contact"> | <a href="<%=searchPath%>/fuschillo/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#8">District 8</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/fuschillo/content/feed" class="rss" title="RSS feed of Charles J. Fuschillo Jr.&#039;s content">RSS</a><a href="http://twitter.com/SenFuschillo" class="twitter" title="Follow Charles J. Fuschillo Jr. on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Senator-Charles-J-Fuschillo-Jr/12764386316" class="facebook" title="Charles J. Fuschillo Jr.&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-16 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/golden"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD22_Golden.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/golden">Golden, Martin J.</a><span class="contact"> | <a href="<%=searchPath%>/golden/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#22">District 22</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/golden/content/feed" class="rss" title="RSS feed of Martin J. Golden&#039;s content">RSS</a><a href="http://twitter.com/senmartygolden" class="twitter" title="Follow Martin J. Golden on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Martin-J-Golden/29214902523" class="facebook" title="Martin J. Golden&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-17 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/griffo"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD47_Griffo.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/griffo">Griffo, Joseph A.</a><span class="contact"> | <a href="<%=searchPath%>/joseph-a-griffo/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#47">District 47</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/griffo/content/feed" class="rss" title="RSS feed of Joseph A. Griffo&#039;s content">RSS</a><a href="http://www.facebook.com/SenatorJoeGriffo" class="facebook" title="Joseph A. Griffo&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-18 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/hannon"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/hannon.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/hannon">Hannon, Kemp</a><span class="contact"> | <a href="<%=searchPath%>/kemp-hannon/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#6">District 6</a></span>

  </div>
  </div>
  <div class="views-row views-row-19 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/hassell-thompson"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/WebBio Hassell-ThompsonHS.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/hassell-thompson">Hassell-Thompson, Ruth</a><span class="contact"> | <a href="<%=searchPath%>/ruth-hassell-thompson/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#36">District 36</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/ruth-hassell-thompson/content/feed" class="rss" title="RSS feed of Ruth Hassell-Thompson&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-20 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/huntley"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(03-03-09) Huntley-HS-015.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/huntley">Huntley, Shirley L.</a><span class="contact"> | <a href="<%=searchPath%>/shirley-l-huntley/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#10">District 10</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/shirley-l-huntley/content/feed" class="rss" title="RSS feed of Shirley L. Huntley&#039;s content">RSS</a><a href="http://www.facebook.com/people/Shirley-L-Huntley/1210706953" class="facebook" title="Shirley L. Huntley&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-21 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/johnson c"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Senator_johnson_headshotWeb.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/johnson c">Johnson, Craig M.</a><span class="contact"> | <a href="<%=searchPath%>/johnson c/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#7">District 7</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/johnson c/content/feed" class="rss" title="RSS feed of Craig M. Johnson&#039;s content">RSS</a><a href="http://twitter.com/HonCraigJohnson" class="twitter" title="Follow Craig M. Johnson on Twitter">Twitter</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-22 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/johnson o"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD4_Johnson.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/johnson o">Johnson, Owen H.</a><span class="contact"> | <a href="<%=searchPath%>/johnson o/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#4">District 4</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/johnson o/content/feed" class="rss" title="RSS feed of Owen H. Johnson&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-23 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/klein"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/JK_Headshot_2009mp.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/klein">Klein, Jeffrey D.</a><span class="contact"> | <a href="<%=searchPath%>/klein/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#34">District 34</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/klein/content/feed" class="rss" title="RSS feed of Jeffrey D. Klein&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-24 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/krueger"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/fall 07 color.JPG" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/krueger">Krueger, Liz</a><span class="contact"> | <a href="<%=searchPath%>/krueger/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#26">District 26</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/krueger/content/feed" class="rss" title="RSS feed of Liz Krueger&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-25 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/kruger"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/HEADSHOT-CK 2009_0.JPG" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/kruger">Kruger, Carl</a><span class="contact"> | <a href="<%=searchPath%>/kruger/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#27">District 27</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/kruger/content/feed" class="rss" title="RSS feed of Carl Kruger&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-26 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/lanza"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/headshotweb.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/lanza">Lanza, Andrew J</a><span class="contact"> | <a href="<%=searchPath%>/lanza/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#24">District 24</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/lanza/content/feed" class="rss" title="RSS feed of Andrew J Lanza&#039;s content">RSS</a><a href="http://www.twitter.com/senatorlanza" class="twitter" title="Follow Andrew J Lanza on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Andrew-J-Lanza/45298833298" class="facebook" title="Andrew J Lanza&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-27 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/larkin"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/head.JPG" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/larkin">Larkin, William J., Jr.</a><span class="contact"> | <a href="<%=searchPath%>/larkin/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#39">District 39</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/larkin/content/feed" class="rss" title="RSS feed of William J. Larkin Jr.&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-28 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/lavalle"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD1_Lavalle-.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/lavalle">LaValle, Kenneth P.</a><span class="contact"> | <a href="<%=searchPath%>/lavalle/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#1">District 1</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/lavalle/content/feed" class="rss" title="RSS feed of Kenneth P. LaValle&#039;s content">RSS</a><a href="http://twitter.com/senatorlavalle" class="twitter" title="Follow Kenneth P. LaValle on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Senator-Kenneth-P-LaValle/58246288663?v=info" class="facebook" title="Kenneth P. LaValle&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-29 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/leibell"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD40_Leibell.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/leibell">Leibell, Vincent L.</a><span class="contact"> | <a href="<%=searchPath%>/leibell/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#40">District 40</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/leibell/content/feed" class="rss" title="RSS feed of Vincent L. Leibell&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-30 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/libous"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Libous.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/libous">Libous, Tom</a><span class="contact"> | <a href="<%=searchPath%>/libous/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#52">District 52</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/libous/content/feed" class="rss" title="RSS feed of Tom Libous&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-31 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/little"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD45_Little.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/little">Little, Elizabeth</a><span class="contact"> | <a href="<%=searchPath%>/betty-little/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#45">District 45</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/little/content/feed" class="rss" title="RSS feed of Betty Little&#039;s content">RSS</a><a href="http://www.facebook.com/profile.php?id=1433096364&amp;ref=profile" class="facebook" title="Betty Little&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-32 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/marcellino"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/headshotWEB.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/marcellino">Marcellino, Carl L</a><span class="contact"> | <a href="<%=searchPath%>/marcellino/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#5">District 5</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/marcellino/content/feed" class="rss" title="RSS feed of Carl L Marcellino&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-33 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/maziarz"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD62_Maziarz_0.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/maziarz">Maziarz, George D.</a><span class="contact"> | <a href="<%=searchPath%>/maziarz/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#62">District 62</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/maziarz/content/feed" class="rss" title="RSS feed of George D. Maziarz&#039;s content">RSS</a><a href="http://twitter.com/senatormaziarz" class="twitter" title="Follow George D. Maziarz on Twitter">Twitter</a><a href="http://www.facebook.com/people/George-D-Maziarz/711688571" class="facebook" title="George D. Maziarz&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-34 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/mcdonald"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD43_McDonald.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/mcdonald">McDonald, Roy J.</a><span class="contact"> | <a href="<%=searchPath%>/mcdonald/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#43">District 43</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/mcdonald/content/feed" class="rss" title="RSS feed of Roy J. McDonald&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Senator-Roy-McDonald/154638870286" class="facebook" title="Roy J. McDonald&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-35 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/peralta"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/peralta_0.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/peralta">Peralta, José P.</a><span class="contact"> | <a href="<%=searchPath%>/peralta/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#13">District 13</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/peralta/content/feed" class="rss" title="RSS feed of Hiram Monserrate&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-36 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/montgomery"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Montgomeryheadshotbiography.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/montgomery">Montgomery, Velmanette</a><span class="contact"> | <a href="<%=searchPath%>/montgomery/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#18">District 18</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/montgomery/content/feed" class="rss" title="RSS feed of Velmanette Montgomery&#039;s content">RSS</a><a href="http://www.facebook.com/pages/New-York-State-Senator-Velmanette-Montgomery/103064926679" class="facebook" title="Velmanette Montgomery&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-37 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/morahan"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/MORAHAN headshot_0.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/morahan">Morahan, Thomas P.</a><span class="contact"> | <a href="<%=searchPath%>/morahan/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#38">District 38</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/morahan/content/feed" class="rss" title="RSS feed of Thomas P. Morahan&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-38 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/nozzolio"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/2.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/nozzolio">Nozzolio, Michael F.</a><span class="contact"> | <a href="<%=searchPath%>/nozzolio/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#54">District 54</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/nozzolio/content/feed" class="rss" title="RSS feed of Michael F. Nozzolio&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Senator-Mike-Nozzolio/53492028933" class="facebook" title="Michael F. Nozzolio&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-39 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/onorato"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/onoratorz.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/onorato">Onorato, George</a><span class="contact"> | <a href="<%=searchPath%>/onorato/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#12">District 12</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/onorato/content/feed" class="rss" title="RSS feed of George Onorato&#039;s content">RSS</a><a href="http://twitter.com/SenOnorato" class="twitter" title="Follow George Onorato on Twitter">Twitter</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-40 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/oppenheimer"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/BIOShotOppenheimer2007newheadshotcolor.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/oppenheimer">Oppenheimer, Suzi</a><span class="contact"> | <a href="<%=searchPath%>/oppenheimer/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#37">District 37</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/oppenheimer/content/feed" class="rss" title="RSS feed of Suzi Oppenheimer&#039;s content">RSS</a><a href="http://twitter.com/SuziOppenheimer" class="twitter" title="Follow Suzi Oppenheimer on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Suzi-Oppenheimer/26007246918?ref=ts" class="facebook" title="Suzi Oppenheimer&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-41 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/padavan"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD11_Padavan.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/padavan">Padavan, Frank</a><span class="contact"> | <a href="<%=searchPath%>/padavan/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#11">District 11</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/padavan/content/feed" class="rss" title="RSS feed of Frank Padavan&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-42 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/parker"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Parker2007Biography.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/parker">Parker, Kevin S.</a><span class="contact"> | <a href="<%=searchPath%>/parker/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#21">District 21</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/parker/content/feed" class="rss" title="RSS feed of Kevin S. Parker&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-43 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/perkins"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Sen_PerkinsBIOheadshot.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/perkins">Perkins, Bill</a><span class="contact"> | <a href="<%=searchPath%>/perkins/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#30">District 30</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/perkins/content/feed" class="rss" title="RSS feed of Bill Perkins&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-44 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/ranzenhofer"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/hs.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/ranzenhofer">Ranzenhofer, Michael H.</a><span class="contact"> | <a href="<%=searchPath%>/ranzenhofer/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#61">District 61</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/ranzenhofer/content/feed" class="rss" title="RSS feed of Michael H. Ranzenhofer&#039;s content">RSS</a><a href="http://www.facebook.com/profile.php?id=1477429982" class="facebook" title="Michael H. Ranzenhofer&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-45 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/robach"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD56_Robach.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/robach">Robach, Joseph E.</a><span class="contact"> | <a href="<%=searchPath%>/robach/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#56">District 56</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/robach/content/feed" class="rss" title="RSS feed of Joseph E. Robach&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Joe-Robach/22775973089" class="facebook" title="Joseph E. Robach&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-46 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/saland"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD41_Saland.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/saland">Saland, Stephen M.</a><span class="contact"> | <a href="<%=searchPath%>/saland/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#41">District 41</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/saland/content/feed" class="rss" title="RSS feed of Stephen M. Saland&#039;s content">RSS</a><a href="http://www.new.facebook.com/pages/Senator-Steve-Saland/79851684092?ref=mf" class="facebook" title="Stephen M. Saland&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-47 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/sampson"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Sampson202007.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/sampson">Sampson, John L.</a><span class="contact"> | <a href="<%=searchPath%>/sampson/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#19">District 19</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/sampson/content/feed" class="rss" title="RSS feed of John L. Sampson&#039;s content">RSS</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-48 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/savino"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(03-03-09) Savino-HS-019.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>

  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/savino">Savino, Diane J.</a><span class="contact"> | <a href="<%=searchPath%>/savino/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#23">District 23</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/savino/content/feed" class="rss" title="RSS feed of Diane J. Savino&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-49 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/schneiderman"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/BIOSchneidermannewheadshotcolor2007.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/schneiderman">Schneiderman, Eric T.</a><span class="contact"> | <a href="<%=searchPath%>/schneiderman/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#31">District 31</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/schneiderman/content/feed" class="rss" title="RSS feed of Eric T. Schneiderman&#039;s content">RSS</a><a href="http://www.facebook.com/profile.php?id=630823745&amp;ref=profile" class="facebook" title="Eric T. Schneiderman&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-50 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/jose-m-serrano"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Official_Headshot1[2].jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/serrano">Serrano, Jose M.</a><span class="contact"> | <a href="<%=searchPath%>/jos%C3%A9-m-serrano/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#28">District 28</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/serrano/content/feed" class="rss" title="RSS feed of José M. Serrano&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-51 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/seward"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/2009 Sen Seward Head Shot.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/seward">Seward, James L.</a><span class="contact"> | <a href="<%=searchPath%>/seward/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#51">District 51</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/seward/content/feed" class="rss" title="RSS feed of James L. Seward&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Senator-James-L-Seward/85058127082?ref=ts" class="facebook" title="James L. Seward&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-52 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/skelos"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/SD9_Skelos.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/skelos">Skelos, Dean G.</a><span class="contact"> | <a href="<%=searchPath%>/skelos/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#9">District 9</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/skelos/content/feed" class="rss" title="RSS feed of Dean G. Skelos&#039;s content">RSS</a><a href="http://www.facebook.com/dean.skelos" class="facebook" title="Dean G. Skelos&#039;s Facebook profile">Facebook</a><a href="http://www.youtube.com/nysd09" class="youtube" title="Follow Dean G. Skelos on YouTube">YouTube</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-53 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/smith"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Smith-HS-024.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/smith">Smith, Malcolm A.</a><span class="contact"> | <a href="<%=searchPath%>/malcolm-a-smith/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#14">District 14</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/smith/content/feed" class="rss" title="RSS feed of Malcolm A. Smith&#039;s content">RSS</a><a href="http://twitter.com/malcolmasmith" class="twitter" title="Follow Malcolm A. Smith on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Malcolm-A-Smith/74081452742" class="facebook" title="Malcolm A. Smith&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-54 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/squadron"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(03-05-09) Squadron HS-040.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/squadron">Squadron, Daniel L</a><span class="contact"> | <a href="<%=searchPath%>/squadron/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#25">District 25</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/squadron/content/feed" class="rss" title="RSS feed of Daniel L. Squadron&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-55 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/stachowski"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Stachowski headshot.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/stachowski">Stachowski, William T.</a><span class="contact"> | <a href="<%=searchPath%>/stachowski/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#58">District 58</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/stachowski/content/feed" class="rss" title="RSS feed of William T. Stachowski&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Senator-William-T-Stachowski/98309792160" class="facebook" title="William T. Stachowski&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-56 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/stavisky"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Official Website Headshot_0.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/stavisky">Stavisky, Toby Ann</a><span class="contact"> | <a href="<%=searchPath%>/stavisky/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#16">District 16</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/stavisky/content/feed" class="rss" title="RSS feed of Toby Ann Stavisky&#039;s content">RSS</a><a href="http://www.facebook.com/people/Toby-Ann-Stavisky/1533723561#" class="facebook" title="Toby Ann Stavisky&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-57 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/stewart-cousins"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/(03-09-09) Stewart-Cousins HS-016.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/stewart-cousins">Stewart-Cousins, Andrea</a><span class="contact"> | <a href="<%=searchPath%>/stewart-cousins/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#35">District 35</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/stewart-cousins/content/feed" class="rss" title="RSS feed of Andrea Stewart-Cousins&#039;s content">RSS</a><a href="http://www.facebook.com/pages/Andrea-Stewart-Cousins/24808277897" class="facebook" title="Andrea Stewart-Cousins&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-58 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/thompson"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Thompson-HSweb.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/thompson">Thompson, Antoine M</a><span class="contact"> | <a href="<%=searchPath%>/thompson/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#60">District 60</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/thompson/content/feed" class="rss" title="RSS feed of Antoine M Thompson&#039;s content">RSS</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-59 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/valesky"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/Valesky-HSmp.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/valesky">Valesky, David J.</a><span class="contact"> | <a href="<%=searchPath%>/valesky/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#49">District 49</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/valesky/content/feed" class="rss" title="RSS feed of David J. Valesky&#039;s content">RSS</a><a href="https://twitter.com/SenDavidValesky" class="twitter" title="Follow David J. Valesky on Twitter">Twitter</a><a href="http://www.facebook.com/pages/Senator-David-Valesky/165417682920" class="facebook" title="David J. Valesky&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-60 views-row-even">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/volker"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/volker5x7.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/volker">Volker, Dale M.</a><span class="contact"> | <a href="<%=searchPath%>/volker/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#59">District 59</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/volker/content/feed" class="rss" title="RSS feed of Dale M. Volker&#039;s content">RSS</a><a href="http://www.facebook.com/profile.php?id=1390085908" class="facebook" title="Dale M. Volker&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>
  </div>
  <div class="views-row views-row-61 views-row-odd">
      
  <div class="views-field-field-profile-picture-fid">
                <span class="field-content"><a href="<%=searchPath%>/winner"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/BudgetNewsletter07-PhotoA.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/winner">Winner, George H., Jr.</a><span class="contact"> | <a href="<%=searchPath%>/george-winner/contact">Contact</a></span></span>

  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#53">District 53</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/winner/content/feed" class="rss" title="RSS feed of George Winner&#039;s content">RSS</a><a href="http://www.facebook.com/pages/New-York-State-Senator-George-Winner/77930419708" class="facebook" title="George Winner&#039;s Facebook profile">Facebook</a></div></div></span>
  </div>
  </div>
  <div class="views-row views-row-62 views-row-even views-row-last">
      
  <div class="views-field-field-profile-picture-fid">

                <span class="field-content"><a href="<%=searchPath%>/young"><img src="http://www.nysenate.gov/files/imagecache/senator_teaser/profile-pictures/young headshot-ret.jpg" alt="" title=""  class="imagecache imagecache-senator_teaser imagecache-default imagecache-senator_teaser_default" width="55" height="71" /></a></span>
  </div>
  
  <div class="views-field-field-last-name-value">
                <span class="field-content"><a href="<%=searchPath%>/young">Young, Catharine</a><span class="contact"> | <a href="<%=searchPath%>/young/contact">Contact</a></span></span>
  </div>
  
  <div class="views-field-field-senators-district-nid">
                <span class="field-content"><a href="#57">District 57</a><div class="social_buttons"><div id="social_buttons"><a href="<%=searchPath%>/young/content/feed" class="rss" title="RSS feed of Catharine Young&#039;s content">RSS</a><a href="http://twitter.com/SenatorYoung" class="twitter" title="Follow Catharine Young on Twitter">Twitter</a><a href="http://www.facebook.com/profile.php?id=1129822498&amp;ref=ts" class="facebook" title="Catharine Young&#039;s Facebook profile">Facebook</a></div></div></span>

  </div>

<br style="clear:both;"/>
</div>

<br style="clear:both;"/>

 
   
 <jsp:include page="/footer.jsp"/>
   
    
