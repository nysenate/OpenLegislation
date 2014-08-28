<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="open" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Open Legislation 2.0</title>
    <link rel="stylesheet" href="static/css/app.css" />
    <script src="static/bower_components/modernizr/modernizr.js"></script>
</head>
<body>
    <open:top-nav activeLink="report"/>

    <div class="row" style="margin-top:1.5em">
        <div class="large-2 columns side-menu-bg">
            <nav>
                <ul class="side-nav">
                    <li class='heading'>Report Types</li>
                    <li><a href="#">LBDC Daybreak</a></li>
                    <li><a href="#">Agenda/Calendar Check</a></li>
                    <li><a href="#">Memo Dump</a></li>
                </ul>
            </nav>
        </div>
        <div class="large-10 columns" style="padding-left:35px;">
            <div class="row">
                <div class="large-4 columns">
                    <h4>Daybreak Reports</h4>
                </div>
                <div class="large-offset-4 large-4 columns">
                    <div class="row">
                        <select class="columns large-3">
                            <option selected>Mar</option>
                        </select>
                        <select class="columns large-3"></select>
                        <select class="columns large-3">
                            <option selected>Dec</option>
                        </select>
                        <select class="columns large-3"></select>
                    </div>
                </div>
            </div>
            <div class="row">
                <!-- Chart Thing -->
                <section style="height:150px;width:100%;background:#ddd;">

                </section>
            </div>
            <br/>
            <div class="row">
                <table class="columns large-12" style="padding:0;">
                    <thead>
                        <tr>
                            <th>Report Date/Time</th>
                            <th>Total</th>
                            <th>New</th>
                            <th>Existing</th>
                            <th>Resolved</th>
                            <th>Sponsor</th>
                            <th>Co/Mulit-sponsor</th>
                            <th>Title</th>
                            <th>Law/Summary</th>
                            <th>Action</th>
                            <th>Page</th>
                            <th>Versions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>2014-08-22 12:00</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                            <td>100</td>
                            <td>120</td>
                            <td>100</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                        </tr><tr>
                            <td>2014-08-22 12:00</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                            <td>100</td>
                            <td>120</td>
                            <td>100</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                        </tr><tr>
                            <td>2014-08-22 12:00</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                            <td>100</td>
                            <td>120</td>
                            <td>100</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                        </tr><tr>
                            <td>2014-08-22 12:00</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                            <td>100</td>
                            <td>120</td>
                            <td>100</td>
                            <td>100</td>
                            <td>100</td>
                            <td>80</td>
                            <td>100</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script src="static/bower_components/jquery/dist/jquery.min.js"></script>
    <script src="static/bower_components/foundation/js/foundation.min.js"></script>
    <script src="static/js/app.js"></script>
</body>
</html>
