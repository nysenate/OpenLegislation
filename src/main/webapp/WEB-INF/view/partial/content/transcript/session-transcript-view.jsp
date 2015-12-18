<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-section" ng-controller="SessionTranscriptViewCtrl">
  <div class="padding-20 gray3-bg">
    <a class="gray-2-blue" href="${ctxPath}/transcripts">Search for transcripts</a>
    <h2 class="text-normal padding-20">
      <strong>{{sessionTranscriptDetails.sessionType}}</strong>
      <br/>{{sessionTranscriptDetails.dateTime | moment:'LLL'}}, {{sessionTranscriptDetails.location}}
    </h2>
  </div>
  <div class="margin-left-20 bill-full-text">
    {{sessionTranscriptDetails.text}}
  </div>
</section>