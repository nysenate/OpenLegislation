<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Page Does Not Exist</title>
</head>
<body style="margin:0;">
    <img width="100%"
         src="<%=request.getContextPath()%>/static/img/business_assets/stakeholders/<%= (int) (Math.random()*10) + 1 %>.jpg"/>
    <div style="z-index: 10;background-color: rgba(67,172,106,0.2); height: 100%; width: 100%; position:fixed;top: 0; left: 0; margin: auto;">
        <div style="border-radius:20rem;line-height:25rem;padding:50px; width: 25rem;margin:100px auto;
                background: rgba(0, 158, 191, 0.3);font-size:13rem; text-align:center;text-shadow: 2px 2px 3px #333;
                font-family:sans-serif;font-weight: bold;color:white;">404
        </div>
    </div>
</body>
</html>
