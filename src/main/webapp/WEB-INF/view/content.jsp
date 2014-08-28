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
    <open:top-nav activeLink="content"/>
    <div class="row collapsed" style="margin-top:1.5em">
    <div class="large-3 columns side-menu-bg">
        <nav>
            <ul class="side-nav">
                <li class='heading'>Content Types</li>
                <li><a href="#">Agendas</a></li>
                <li class="active"><a href="#">Bills and Resolutions</a></li>
                <li><a href="#">Calendars</a></li>
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
    <section id="browse-bills" style="padding:1.2em;" class="large-9 columns main-content-bg">
        <form>
            <div class="row collapse">
                <div class="large-2 columns">
                    <select style="border-right:none">
                        <option>Print No</option>
                        <option>Sponsor</option>
                        <option>Advanced</option>
                    </select>
                </div>
                <div class="large-9 columns">
                    <input class="no-bottom-margin" type="text" placeholder="Search by Print No"/>
                </div>
                <div class="large-1 columns">
                    <a href="#" class="button postfix no-bottom-margin">Search</a>
                </div>
            </div>
            <ul class="breadcrumbs">
                <li><a href="#">Bills</a></li>
                <li><a href="#">Search Results</a></li>
                <li class="unavailable"><a href="#">S1234</a></li>
            </ul>
        </form>
        <div data-magellan-expedition="fixed" data-options="fixed_top:60;destination_threshold:100">
            <dl class="sub-nav">
                <dd class="def"><a>Navigate</a></dd>
                <dd data-magellan-arrival="bill-info"><a href="#bill-info">Bill Info</a></dd>
                <dd data-magellan-arrival="bill-actions"><a href="#bill-actions">Actions</a></dd>
                <dd data-magellan-arrival="bill-sponsor-memo"><a href="#bill-sponsor-memo">Memo</a></dd>
                <dd data-magellan-arrival="bill-fulltext"><a href="#bill-full-text">Full Text</a></dd>
                <dd data-magellan-arrival="bill-source"><a href="#bill-source">Source</a></dd>
            </dl>
        </div>
        <div>
            <a name="bill-info"></a>
            <div class="row">
                <h2 class="large-4 columns" data-magellan-destination="bill-info" title="Print Number">S1234B / <span style="color:teal">2013</span></h2>
                <dl style="top:17px;" class="large-8 columns sub-nav">
                    <dt>Versions:</dt>
                    <dd><a href="#">Default</a></dd>
                    <dd><a href="#">A</a></dd>
                    <dd class="active"><a href="#">B</a></dd>
                </dl>
            </div>
            <h3 title="Title">Creates the office of the taxpayer advocate</h3>
            <hr/>
            <p title="Summary">Creates the office of the taxpayer advocate; directs such office be in the control of the department of taxation and finance; outlines functions and duties of such office; creates mandatory reporting to the governor and legislative leaders.</p>
            <hr/>
            <div class="row">
                <div class="large-2 columns">
                    <label>Sponsor</label>
                    <p><a>Perkins</a></p>
                </div>
                <div class="large-3 columns">
                    <label>Multi-Sponsors</label>
                    <p>None</p>
                </div>
                <div class="large-7 columns">
                    <label>Co-Sponsors</label>
                    <p><a>Hassel-Thompson</a>, <a>Krueger</a>, <a>Serrano</a></p>
                </div>
                <div class="columns"></div>
            </div>
            <div class="row">
                <div class="large-4 columns">
                    <label>Committee</label>
                    <p>Investigations and Government Operations</p>
                </div>
                <div class="large-3 columns">
                    <label>Law Section</label>
                    <p>Tax Law</p>
                </div>
                <div class="large-5 columns">
                    <label>Law Code</label>
                    <p>Add §§3014 & 3015, amd §170, Tax L</p>
                </div>
            </div>
            <hr/>
            <!-- Bill Actions -->
            <a data-magellan-destination="bill-actions" name="bill-actions"></a>
            <div class="row">
                <table class="large-12 columns">
                    <thead>
                    <tr>
                        <th>Chamber</th>
                        <th>Date</th>
                        <th>Print No</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>Senate</td>
                        <td>Jan 9, 2013</td>
                        <td>S1234</td>
                        <td>REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS</td>
                    </tr>
                    <tr>
                        <td>Senate</td>
                        <td>Jan 8, 2014</td>
                        <td>S1234A</td>
                        <td>REFERRED TO INVESTIGATIONS AND GOVERNMENT OPERATIONS</td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <hr/>

            <!-- Bill Sponsor Memo -->
            <div class="row">
                <section class="large-11 columns">
                    <label data-magellan-destination="bill-sponsor-memo" class="label secondary block-label">
                        <a href="#bill-sponsor-memo" name="bill-sponsor-memo">Sponsor Memo</a>
                    </label>
                    <div class="text-block">
                        BILL NUMBER:S1234<br><br>TITLE  OF BILL:   An act to amend the tax law, in relation to creating the office of the taxpayer advocate<br><br>PURPOSE: To create within the Department of Taxation and Finance the Office  of Taxpayer  Advocate,  which will assist taxpayers in resolving problems with the department, identify areas in which taxpayers have  problems, and   propose  solutions  to  the  practices  and  procedures  of  the department.<br><br>SUMMARY OF PROVISIONS: The tax law is amended by adding two new sections 3014 and 3015.<br><br>Section 3014 creates an office to be  known  as  the  "office  of  the taxpayer  advocate"  which will assist taxpayers in resolving problems with the office, identify areas in which taxpayers  have  problems  in dealing  with  the  department, recommend legislative action as may be appropriate to resolve  problems  encountered  by  taxpayers,  and  to preserve  and  promote  the  rights  of  the  taxpayer.  This taxpayer advocate shall be appointed by the governor for a term of  four  years and will report directly to the commissioner.<br><br>Section 3015 creates taxpayer assistance orders. The taxpayer advocate may with or without a formal request from a taxpayer, issue a taxpayer assistance  order if the taxpayer is suffering or is about to suffer a significant hardship as a result of the manner in which the department administers the tax laws.<br><br>JUSTIFICATION: This legislation will help ensure that  state  taxpayers  are  treated professionally  and  fairly by the Department of Tax and Finance. This office will assist taxpayers to resolve problems in a neutral fashion. There are many difficulties that taxpayers endure in relation  to  the tax process and getting their taxes filed. This legislation provides a venue  through which taxpayers can report their problems and have them dealt with fairly and efficiently.<br><br>LEGISLATIVE HISTORY: 2011-12: S.1529 - Referred to Investigations &amp; Government Op. 2010-11: S.1529 - Referred to Investigations &amp; Government Op. 2009-10: S.1168 - Referred to Investigations &amp; Government Op. 2007-08: S.2914 - Referred to Investigations &amp; Government Op. 2005-06: S.1570 - Referred to Investigations &amp; Government Op.<br><br>FISCAL IMPLICATIONS: To be determined.<br><br>EFFECTIVE DATE: Immediately, but no later than April 1 of the year following enactment of this legislation.
                    </div>
                </section>

            </div>


            <hr/>
            <!-- Bill Full Text -->

            <label data-magellan-destination="bill-fulltext" class="label secondary block-label">
                <a href="#bill-full-text" name="bill-full-text">Full Text</a>
            </label>

                    <pre>
            STATE OF NEW YORK
            ________________________________________________________________________

            1234

            2013-2014 Regular Sessions

            IN SENATE

            (PREFILED)

            January 9, 2013
            ___________

            Introduced by Sens. PERKINS, HASSELL-THOMPSON, KRUEGER -- read twice and
            ordered  printed, and when printed to be committed to the Committee on
            Investigations and Government Operations

            AN ACT to amend the tax law, in relation to creating the office  of  the
            taxpayer advocate

            THE  PEOPLE OF THE STATE OF NEW YORK, REPRESENTED IN SENATE AND ASSEM-
            BLY, DO ENACT AS FOLLOWS:

            Section 1. The tax law is amended by adding two new sections 3014  and
            3015 to read as follows:
            S  3014.  OFFICE  OF  THE TAXPAYER ADVOCATE. (A) THERE SHALL BE IN THE
            DEPARTMENT AN OFFICE TO BE KNOWN AS THE "OFFICE OF  THE  TAXPAYER  ADVO-
            CATE".  SUCH  OFFICE  SHALL BE UNDER THE SUPERVISION AND DIRECTION OF AN
            OFFICIAL KNOWN AS THE "COMMISSIONER OF THE OFFICE OF THE TAXPAYER  ADVO-
            CATE".  THE COMMISSIONER OF THE OFFICE OF THE TAXPAYER ADVOCATE SHALL BE
            APPOINTED BY THE GOVERNOR AND SHALL REPORT DIRECTLY TO THE COMMISSIONER.
            THE COMMISSIONER OF THE OFFICE OF THE TAXPAYER ADVOCATE SHALL DEVOTE HIS
            OR HER ENTIRE TIME TO THE DUTIES OF SUCH OFFICE.
            (B) NO PERSON SHALL BE APPOINTED AS THE COMMISSIONER OF THE OFFICE  OF
            THE  TAXPAYER  ADVOCATE  UNLESS AT THE TIME OF SUCH PERSON'S APPOINTMENT
            SUCH PERSON IS A RESIDENT OF THE  STATE  AND  IS  KNOWLEDGEABLE  ON  THE
            SUBJECT  OF TAXATION AND IS SKILLFUL IN MATTERS PERTAINING THERETO. ONCE
            APPOINTED, THE COMMISSIONER OF THE OFFICE OF THE TAXPAYER ADVOCATE SHALL
            CONTINUE IN OFFICE UNTIL THE GOVERNOR'S  TERM  EXPIRES  AND  UNTIL  SUCH
            GOVERNOR'S  SUCCESSOR  HAS BEEN APPOINTED AND HAS QUALIFIED. THE TERM OF
            OFFICE SHALL BE AT THE PLEASURE OF THE GOVERNOR.
            (C) IN THE EVENT OF  A  VACANCY  CAUSED  BY  THE  DEATH,  RESIGNATION,
            REMOVAL  OR DISABILITY OF THE COMMISSIONER OF THE OFFICE OF THE TAXPAYER
            ADVOCATE, THE VACANCY SHALL BE FILLED BY THE GOVERNOR.
            (D) (1) THE OFFICE OF THE TAXPAYER ADVOCATE SHALL HAVE  THE  FOLLOWING
            FUNCTIONS, POWERS, AND DUTIES:
            (I) TO ASSIST TAXPAYERS IN RESOLVING PROBLEMS WITH THE DEPARTMENT;
            (II)  TO  IDENTIFY  AREAS IN WHICH TAXPAYERS HAVE PROBLEMS IN DEALINGS
            WITH THE DEPARTMENT;
            (III) TO PROPOSE SOLUTIONS, INCLUDING ADMINISTRATIVE CHANGES TO  PRAC-

                    </pre>
        </div>
    </section>
    </div>

    <script src="static/bower_components/jquery/dist/jquery.min.js"></script>
    <script src="static/bower_components/foundation/js/foundation.min.js"></script>
    <script src="static/js/app.js"></script>
</body>
</html>
