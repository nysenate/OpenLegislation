<%@tag description="Footer template" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<footer id="footer">
    <div class="row">
        <div class="columns large-6">
            <div style="margin-top:25px;">
                <img height="50px" width="50px" src="https://assets-cdn.github.com/images/modules/logos_page/Octocat.png"/>
                <span class="small">View Source Code at GitHub</span>
            </div>
        </div>
        <div class="columns large-6">
            <div class="columns small-3">
                <img style="float:left;margin-top:37px;" src="http://i.creativecommons.org/l/by-nc-nd/3.0/us/88x31.png"/>
            </div>
            <div class="columns small-9">
                <span class="xsmall" style="display:block;margin-top:15px;">
            This content is licensed under Creative Commons BY-NC-ND 3.0.
            Permissions beyond the scope of this license are available <a href="http://www.nysenate.gov/copyright-policy">here</a>.
            The software and services provided under this site are offered under the BSD License and the GPL v3 License.
                </span>
            </div>
        </div>
    </div>
</footer>

<!-- Close wrapper div -->
</div>

<jsp:doBody/>

</body>
</html>