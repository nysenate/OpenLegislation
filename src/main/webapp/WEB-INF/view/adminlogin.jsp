<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open-layout" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<% request.setAttribute("ctxPath", request.getContextPath()); %>

<open-layout:head title="Admin Login"/>
<open-layout:body pageId="adminlogin">
    <style>
        .cent {
            text-align:center;
        }

        .formdiv div {
            padding: 5px;
            margin-top: 10px;
            margin-bottom: 10px;
        }
        #error {
            color:red;
        }
    </style>

    <h1 class="cent"> Admin Login Form </h1>
    <hr>
    <div class="formiv">
        <form action="" method="post">
            <input type="text" placeholder="username" name="user"> <br>
            <input type="password" placeholder="password" name="pass"> <br>
            <input type="submit" value="login">
        </form>
        <p id="error">
        ${errormessage}
        </p>
    </div>
</open-layout:body>
