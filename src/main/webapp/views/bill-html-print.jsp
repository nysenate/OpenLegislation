<%@ page language="java" import="java.util.ArrayList, java.util.List, java.util.Collections, java.util.StringTokenizer, java.util.Iterator, java.text.*,gov.nysenate.openleg.*,gov.nysenate.openleg.search.*,gov.nysenate.openleg.util.*,gov.nysenate.openleg.model.*,org.codehaus.jackson.map.ObjectMapper" contentType="text/html" pageEncoding="utf-8"%>
<%
	String appPath = request.getContextPath();
	
	Bill bill = (Bill)request.getAttribute("bill");
	
	String titleText = "(no title)";
	if (bill.getTitle()!=null)
		titleText = bill.getTitle();
	
	String senateBillNo = bill.getSenateBillNo();
	
	String title = senateBillNo + " - NY Senate Open Legislation - " + titleText;
%>
<style>
pre{	
	overflow: auto;
	font-family: “Consolas”,monospace;
	font-size: 9pt;
	text-align:left;
	overflow-x: auto;
	white-space: -moz-pre-wrap !important;
	word-wrap: break-word; 
	margin: 0px 0px 0px 0px;
	padding:5px 5px 3px 5px;
	white-space : pre-line; 
	max-width:100%;
}
.item-actions{
	display:none;
}
pre .billHeader{
	text-align:center;
	padding:10px 0;
	font-weight:bold;
	border-bottom:1px solid #ccc;
	margin: 0 0 20px 0;
}
</style>
<jsp:include page="/templates/bill.jsp" />
