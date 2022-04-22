import React from 'react'
import Accordion from "app/shared/Accordion";

export default function AdvancedSearchTips() {
  const headerRowClass = "bg-gray-100 font-medium"
  const headerCellClass = "py-1"
  return (
    <Accordion title="Advanced Query Search Tips">
      <div className="m-5">
        <div>
          <p className="text text--small m-2 mb-6">You can combine the field definitions documented below to perform
            targeted
            searches.
            You can string together multiple search term fields with the following operators: <code>AND, OR, NOT</code>
            as well as parenthesis for grouping. For more information refer to the&nbsp;
            <a href="http://lucene.apache.org/core/2_9_4/queryparsersyntax.html" className="link">Lucene query docs</a>.
          </p>
        </div>
        <table className="text text--small block w-full overflow-x-auto">
          <thead>
          <tr>
            <th>To search for</th>
            <th>Use the field</th>
            <th>With value type</th>
            <th>Examples</th>
          </tr>
          </thead>
          <tbody>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>Basic Details</td>
          </tr>
          <tr>
            <td>Original Print No</td>
            <td>basePrintNo</td>
            <td>text</td>
            <td>basePrintNo:S1234</td>
          </tr>
          <tr>
            <td>Session Year</td>
            <td>session</td>
            <td>number</td>
            <td>session:2015</td>
          </tr>
          <tr>
            <td>Title</td>
            <td>title</td>
            <td>text</td>
            <td>title:moose elk</td>
          </tr>
          <tr>
            <td>Chamber</td>
            <td>billType.chamber</td>
            <td>enum</td>
            <td>billType.chamber:SENATE<br />billType.chamber:ASSEMBLY</td>
          </tr>
          <tr>
            <td>Is a Resolution</td>
            <td>billType.resolution</td>
            <td>boolean</td>
            <td>billType.resolution:true</td>
          </tr>
          <tr>
            <td>Active Amendment Version</td>
            <td>activeVersion</td>
            <td>text</td>
            <td>activeVersion:A</td>
          </tr>
          <tr>
            <td>Published Year</td>
            <td>year</td>
            <td>number</td>
            <td>year:2014</td>
          </tr>
          <tr>
            <td>Published Date/Time</td>
            <td>publishedDateTime</td>
            <td>date-time</td>
            <td>publishedDateTime:2015-01-02<br />publishedDateTime:[2015-01-02 TO 2015-01-04]</td>
          </tr>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>Sponsor</td>
          </tr>
          <tr>
            <td>Summary</td>
            <td>summary</td>
            <td>text</td>
            <td>summary:moose, elk, or deer</td>
          </tr>
          <tr>
            <td>Sponsor Last Name</td>
            <td>sponsor.member.shortName</td>
            <td>text</td>
            <td>sponsor.member.shortName:martins</td>
          </tr>
          <tr>
            <td>Sponsor Full Name</td>
            <td>sponsor.member.fullName</td>
            <td>text</td>
            <td>sponsor.member.fullName:jack</td>
          </tr>
          <tr>
            <td>Is Budget Bill</td>
            <td>sponsor.budget</td>
            <td>boolean</td>
            <td>sponsor.budget:true</td>
          </tr>
          <tr>
            <td>Is Rules Sponsored</td>
            <td>sponsor.rules</td>
            <td>boolean</td>
            <td>sponsor.rules:true</td>
          </tr>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>Status</td>
          </tr>
          <tr>
            <td>Bill Signed Into Law</td>
            <td>signed</td>
            <td>boolean</td>
            <td>signed:true</td>
          </tr>
          <tr>
            <td>Resolution Adopted</td>
            <td>adopted</td>
            <td>boolean</td>
            <td>adopted:true</td>
          </tr>
          <tr>
            <td>Status</td>
            <td>status.statusType</td>
            <td>enum</td>
            <td>status.statusType:"INTRODUCED"<br />
              status.statusType:"IN_SENATE_COMM"<br />status.statusType:"IN_ASSEMBLY_COMM"<br />status.statusType:"SENATE_FLOOR"<br />
              status.statusType:"ASSEMBLY_FLOOR"<br />status.statusType:"PASSED_SENATE"<br />status.statusType:"PASSED_ASSEMBLY"<br />
              status.statusType:"DELIVERED_TO_GOV"<br />status.statusType:"SIGNED_BY_GOV"<br />status.statusType:"VETOED"<br />
              status.statusType:"STRICKEN"<br />
            </td>

          </tr>
          <tr>
            <td>Status Action Date</td>
            <td>status.actionDate</td>
            <td>date</td>
            <td>status.actionDate:[2015-02-01 TO 2015-02-02]</td>
          </tr>
          <tr>
            <td>Current Committee</td>
            <td>status.committeeName</td>
            <td>text</td>
            <td>status.committeeName:Finance</td>
          </tr>
          <tr>
            <td>Current Calendar No</td>
            <td>status.billCalNo</td>
            <td>number</td>
            <td>status.billCalNo:123</td>
          </tr>
          <tr>
            <td>Associated Program</td>
            <td>programInfo.name</td>
            <td>text</td>
            <td>programInfo.name:Governor</td>
          </tr>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>The fields below are associated with each amendment and
              are always prefixed with '\*.'
            </td>
          </tr>
          <tr>
            <td>Sponsor's Memo</td>
            <td>\*.memo</td>
            <td>text</td>
            <td>\*.memo:Yogurt</td>
          </tr>
          <tr>
            <td>Full Text</td>
            <td>\*.fullText</td>
            <td>text</td>
            <td>\*.fullText:(cats OR kittens OR puppies)</td>
          </tr>
          <tr>
            <td>Law Section</td>
            <td>\*.lawSection</td>
            <td>text</td>
            <td>\*.lawSection:"Agriculture and Markets Law"</td>
          </tr>
          <tr>
            <td>Law Code</td>
            <td>\*.lawCode</td>
            <td>text</td>
            <td>\*.lawCode:Amd?12</td>
          </tr>
          <tr>
            <td>Enacting Clause</td>
            <td>\*.actClause</td>
            <td>text</td>
            <td>\*.actClause:lemon</td>
          </tr>
          <tr>
            <td>Is Uni Bill</td>
            <td>\*.uniBill</td>
            <td>boolean</td>
            <td>\*.uniBill:true</td>
          </tr>
          <tr>
            <td>Cosponsor Last Name</td>
            <td>\*.coSponsors.\*.shortName</td>
            <td>text</td>
            <td>\*.coSponsors.\*.shortName:martins</td>
          </tr>
          <tr>
            <td>Multi Sponsor Last Name</td>
            <td>\*.multiSponsors.\*.shortName</td>
            <td>text</td>
            <td>\*.multiSponsors.\*.shortName:barron</td>
          </tr>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>Vote Roll Data</td>
          </tr>
          <tr>
            <td>Vote Count</td>
            <td>votes.size</td>
            <td>number</td>
            <td>votes.size:>0</td>
          </tr>
          <tr>
            <td>Vote Type</td>
            <td>votes.\*.voteType</td>
            <td>enum</td>
            <td>votes.\*.voteType:COMMITTEE<br />votes.\*.voteType:FLOOR</td>
          </tr>
          <tr>
            <td colSpan="4" className="font-medium">There are 6 vote codes: AYE, NAY, AYEWR (Aye with reservations), ABS
              (Absent), EXC
              (Excused), ABD (Abstained)<br />
              Only AYE is shown in the examples below but you can use any of them.
            </td>
          </tr>
          <tr>
            <td>Ayes Count</td>
            <td>votes.\*.AYE.size</td>
            <td>number</td>
            <td>votes.\*.AYE.size:>10</td>
          </tr>
          <tr>
            <td>Member that voted Aye</td>
            <td>votes.\*.AYE.\*.shortName</td>
            <td>text</td>
            <td>votes.\*.AYE.\*.shortName:Funke</td>
          </tr>
          <tr className={headerRowClass}>
            <td colSpan="4" className={headerCellClass}>Bill Actions</td>
          </tr>
          <tr>
            <td>Action Count</td>
            <td>actions.size</td>
            <td>number</td>
            <td>actions.size:>10</td>
          </tr>
          <tr>
            <td>Action Date</td>
            <td>actions.\*.date</td>
            <td>date</td>
            <td>actions.\*.date:>2015-02-01</td>
          </tr>
          <tr>
            <td>Action Text</td>
            <td>actions.\*.text</td>
            <td>text</td>
            <td>actions.\*.text:"Signed Chap"</td>
          </tr>
          </tbody>
        </table>
      </div>
    </Accordion>
  )
}