<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="HearingTranscriptViewCtrl">
  <div class="padding-20 gray3-bg">
    <a class="gray-2-blue" href="${ctxPath}/transcripts?txtype=hearing">Search for transcripts</a>
    <h2 class="text-normal padding-20">
      <strong>{{hearingDetails.title}}</strong>
      <br/>
      <br/>
      <strong>Date:</strong> {{hearingDetails.date | moment:'LL'}} <strong>Time:</strong> {{hearingDetails.startTime}} - {{hearingDetails.endTime}}
      <br/>
      <strong>Address:</strong> {{hearingDetails.address}}
    </h2>
    <h4 ng-if="comm" class="padding-20 text-normal"
        ng-repeat="comm in hearingDetails.committees">
      {{comm.name}}
    </h4>
  </div>
  <div class="margin-left-20 bill-full-text">
    {{hearingDetails.text}}
  </div>
</section>

