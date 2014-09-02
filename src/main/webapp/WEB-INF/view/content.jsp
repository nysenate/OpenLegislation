<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Content"/>
<open-layout:body pageId="content">
    <div class="row collapsed" style="margin-top:1.5em">
        <div class="small-2 columns side-menu-bg">
            <nav>
                <ul class="side-nav">
                    <li class='heading'>Content Types</li>
                    <li><a href="#">Agendas</a></li>
                    <li class="active"><a href="#">Bills and Resolutions</a></li>
                    <li><a href="calendars">Calendars</a></li>
                    <li><a href="#">Committees</a></li>
                    <li><a href="#">Members</a></li>
                    <li><a href="#">Laws of NY</a></li>
                    <li><a href="#">Transcripts</a></li>
                    <li><a href="#">Vetos and Approvals</a></li>
                    <li><a href="#"></a></li>
                    <li class='heading'>Source</li>
                    <li><a href="#">Bill Sobi Files</a></li>
                    <li><a href="#">Calendar Sobi Files</a></li>
                    <li><a href="#">Law Documents</a></li>
                </ul>
            </nav>
        </div>
    </div>
</open-layout:body>
<open-layout:footer/>