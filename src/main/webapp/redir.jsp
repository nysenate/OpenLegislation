<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
String path = request.getContextPath();


//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

path = path.replace("openleg","legislation");

response.sendRedirect(path);
%>
