<%@ page language="java" import="java.util.*, java.text.*,java.io.*,gov.nysenate.openleg.*,gov.nysenate.openleg.model.*" pageEncoding="UTF-8"%>
<%

String appPath = request.getContextPath();
session.removeAttribute("term");

String searchPath = appPath + "/legislation/legislation/committee";

%>

<jsp:include page="/header.jsp">
	<jsp:param name="title" value="Committees"/>
</jsp:include>
 	<h2>Senate Committees</h2>

<style>
ul
{
margin-left:12px;
font-size:16pt;

}

li
{
	line-height:20px;
}

#content-area
{
	margin:6px;
}
</style>
 <div id="content">
 
   <div id="content-area" class="clearfix"> 
          <div id="block-views-committees-block_1" class="block block-views region-odd even region-count-1 count-6"> 
	<div class="top"></div> 
	<div class="block-inner clearfix"> 
 
      <h2 class="title block_title">Standing Committees</h2> 
  
  <div class="content clearfix"> 
    <div class="view view-committees view-id-committees view-display-id-block_1 view-dom-id-3"> 
    
  
  
      <div class="view-content"> 
      <div class="item-list"> 
    <ul> 
          <li class="views-row views-row-1 views-row-odd views-row-first">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/aging">Aging</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-2 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/agriculture">Agriculture</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-3 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/banks">Banks</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-4 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/children-and-families">Children and Families</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-5 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/cities">Cities</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-6 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/civil-service-and-pensions">Civil Service and Pensions</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-7 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/codes">Codes</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-8 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/commerce-economic-development-and-small-business">Commerce, Economic Development and Small Business</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-9 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/consumer-protection">Consumer Protection</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-10 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/corporations-authorities-and-commissions">Corporations, Authorities and Commissions</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-11 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/crime-victims-crime-and-correction">Crime Victims, Crime and Correction</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-12 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/cultural-affairs-tourism-parks-and-recreation">Cultural Affairs, Tourism, Parks and Recreation</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-13 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/education">Education</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-14 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/elections">Elections</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-15 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/energy-and-telecommunications">Energy and Telecommunications</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-16 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/environmental-conservation">Environmental Conservation</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-17 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/ethics">Ethics</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-18 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/finance">Finance</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-19 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/health">Health</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-20 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/higher-education">Higher Education</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-21 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/housing-construction-and-community-development">Housing, Construction, and Community Development</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-22 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/insurance">Insurance</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-23 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/investigations-and-government-operations">Investigations and Government Operations</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-24 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/judiciary">Judiciary</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-25 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/labor">Labor</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-26 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/local-government">Local Government</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-27 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/mental-health-and-developmental-disabilities">Mental Health and Developmental Disabilities</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-28 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/racing-gaming-and-wagering">Racing, Gaming and Wagering</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-29 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/rules">Rules</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-30 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/social-services">Social Services</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-31 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/transportation">Transportation</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-32 views-row-even views-row-last">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/veterans-homeland-security-and-military-affairs">Veterans, Homeland Security and Military Affairs</a></span> 
  </div> 
</li> 
      </ul> 
</div>    </div> 
  
  
  
  
  
  
</div>   </div> 
 
  
</div> 
 
<div class="bottom"></div> 
 
</div> <!-- /block-inner, /block --><div id="block-views-committees-block_3" class="block block-views region-even odd region-count-2 count-7"> 
	<div class="top"></div> 
	<div class="block-inner clearfix"> 
 
      <h2 class="title block_title">Task Forces &amp; Other Entities</h2> 
  
  <div class="content clearfix"> 
    <div class="view view-committees view-id-committees view-display-id-block_3 view-dom-id-4"> 
    
  
  
      <div class="view-content"> 
      <div class="item-list"> 
    <ul> 
          <li class="views-row views-row-1 views-row-odd views-row-first">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/administrative-regulations-review-commission-arrc">Administrative Regulations Review Commission (ARRC)</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-2 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/hudson-valley-delegation">Hudson Valley Delegation</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-3 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/mta-capital-program-review-board-cprb">MTA Capital Program Review Board (CPRB)</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-4 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/new-york-state-conference-black-senators">New York State Conference of Black Senators</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-5 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/puerto-ricanlatino-caucus">Puerto Rican/Latino Caucus</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-6 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/task-force-government-efficiency">Task Force on Government Efficiency</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-7 views-row-odd">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/new-york-state-black-puerto-rican-hispanic-and-asian-legislative-caucus">The New York State Black, Puerto Rican, Hispanic and Asian Legislative Caucus</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-8 views-row-even views-row-last">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/upstate-caucus">Upstate Caucus</a></span> 
  </div> 
</li> 
      </ul> 
</div>    </div> 
  
  
  
  
  
  
</div>   </div> 
 
  
</div> 
 
<div class="bottom"></div> 
 
</div> <!-- /block-inner, /block --><div id="block-views-committees-block_5" class="block block-views region-odd even region-count-3 count-8"> 
	<div class="top"></div> 
	<div class="block-inner clearfix"> 
 
      <h2 class="title block_title">Legislative Commissions</h2> 
  
  <div class="content clearfix"> 
    <div class="view view-committees view-id-committees view-display-id-block_5 view-dom-id-5"> 
    
  
  
      <div class="view-content"> 
      <div class="item-list"> 
    <ul> 
          <li class="views-row views-row-1 views-row-odd views-row-first views-row-last">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/legislative-commission-rural-resources">Legislative Commission on Rural Resources</a></span> 
  </div> 
</li> 
      </ul> 
</div>    </div> 
  
  
  
  
  
  
</div>   </div> 
 
  
</div> 
 
<div class="bottom"></div> 
 
</div> <!-- /block-inner, /block --><div id="block-views-committees-block_2" class="block block-views region-even odd region-count-4 count-9"> 
	<div class="top"></div> 
	<div class="block-inner clearfix"> 
 
      <h2 class="title block_title">Temporary Committees</h2> 
  
  <div class="content clearfix"> 
    <div class="view view-committees view-id-committees view-display-id-block_2 view-dom-id-6"> 
    
  
  
      <div class="view-content"> 
      <div class="item-list"> 
    <ul> 
          <li class="views-row views-row-1 views-row-odd views-row-first">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/budget-and-tax-reform">Budget and Tax Reform</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-2 views-row-even">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/select-committee-investigate-facts-and-circumstances-surrounding-conviction-hiram-monserra">Select Committee To Investigate The Facts And Circumstances Surrounding The Conviction Of Hiram Monserrate</a></span> 
  </div> 
</li> 
          <li class="views-row views-row-3 views-row-odd views-row-last">  
  <div class="views-field-title"> 
                <span class="field-content"><a href="/legislation/committee/temporary-committee-rules-and-administration-reform">Temporary Committee on Rules and Administration Reform</a></span> 
  </div> 
</li> 
      </ul> 
</div>    </div> 
  
  
  
  
  
  
</div>   </div> 
 
  
</div> 
 
<div class="bottom"></div> 
 
</div> <!-- /block-inner, /block -->        </div> 
 
        
        
      </div> 
 
</div> 
 
</div>
 <jsp:include page="/footer.jsp"/>
   
    
