package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillAmendment;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.util.OutputUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BillActionParserTests
{
    private static final Logger logger = LoggerFactory.getLogger(BillActionParserTests.class);

    private static BillId sampleBillId = new BillId("A1234", 2013);
    private static String actionsList1 =
        "01/28/09 referred to correction\n" +
        "03/17/09 reported referred to ways and means\n" +
        "04/28/09 reported\n" +
        "04/30/09 advanced to third reading cal.453\n" +
        "05/04/09 passed assembly\n" +
        "05/04/09 delivered to senate\n" +
        "05/04/09 REFERRED TO CODES\n" +
        "05/26/09 SUBSTITUTED FOR S4366\n" +
        "05/26/09 3RD READING CAL.391\n" +
        "06/02/09 recalled from senate\n" +
        "06/03/09 SUBSTITUTION RECONSIDERED\n" +
        "06/03/09 RECOMMITTED TO CODES\n" +
        "06/03/09 RETURNED TO ASSEMBLY\n" +
        "06/04/09 vote reconsidered - restored to third reading\n" +
        "06/04/09 amended on third reading 3664a\n" +
        "06/15/09 repassed assembly\n" +
        "06/16/09 returned to senate\n" +
        "06/16/09 COMMITTED TO RULES\n" +
        "07/17/09 SUBSTITUTED FOR S4366A\n" +
        "07/17/09 3RD READING CAL.391\n" +
        "07/16/09 RECOMMITTED TO RULES\n" +
        "01/06/10 DIED IN SENATE\n" +
        "01/06/10 RETURNED TO ASSEMBLY\n" +
        "01/06/10 ordered to third reading cal.276\n" +
        "01/19/10 committed to correction\n" +
        "01/26/10 amend and recommit to correction\n" +
        "01/26/10 print number 3664b";

    private static BillId S3296_2009 = new BillId("S3296", 2009);
    private static String actionsForS3296_2009 = "2009S03296H403/13/09 REFERRED TO ENVIRONMENTAL CONSERVATION\n" +
            "2009S03296H403/19/09 AMEND AND RECOMMIT TO ENVIRONMENTAL CONSERVATION\n" +
            "2009S03296H403/19/09 PRINT NUMBER 3296A\n" +
            "2009S03296H403/24/09 REPORTED AND COMMITTED TO CODES\n" +
            "2009S03296H403/30/09 AMEND AND RECOMMIT TO CODES\n" +
            "2009S03296H403/30/09 PRINT NUMBER 3296B\n" +
            "2009S03296H404/21/09 AMEND AND RECOMMIT TO CODES\n" +
            "2009S03296H404/21/09 PRINT NUMBER 3296C\n" +
            "2009S03296H409/02/09 AMEND AND RECOMMIT TO CODES\n" +
            "2009S03296H409/02/09 PRINT NUMBER 3296D\n" +
            "2009S03296H409/04/09 AMEND AND RECOMMIT TO CODES\n" +
            "2009S03296H409/04/09 PRINT NUMBER 3296E\n" +
            "2009S03296H401/06/10 REFERRED TO ENVIRONMENTAL CONSERVATION\n" +
            "2009S03296H401/22/10 AMEND (T) AND RECOMMIT TO ENVIRONMENTAL CONSERVATION\n" +
            "2009S03296H401/22/10 PRINT NUMBER 3296F\n" +
            "2009S03296H402/02/10 REPORTED AND COMMITTED TO CODES\n" +
            "2009S03296H402/03/10 AMEND AND RECOMMIT TO CODES\n" +
            "2009S03296H402/03/10 PRINT NUMBER 3296G\n" +
            "2009S03296H404/13/10 REPORTED AND COMMITTED TO FINANCE\n" +
            "2009S03296H404/20/10 REPORTED AND COMMITTED TO RULES\n" +
            "2009S03296H404/20/10 ORDERED TO THIRD READING CAL.399\n" +
            "2009S03296H404/20/10 PASSED SENATE\n" +
            "2009S03296H404/20/10 DELIVERED TO ASSEMBLY\n" +
            "2009S03296H404/21/10 referred to environmental conservation\n" +
            "2009S03296H406/02/10 RECALLED FROM ASSEMBLY\n" +
            "2009S03296H406/02/10 returned to senate\n" +
            "2009S03296H406/02/10 VOTE RECONSIDERED - RESTORED TO THIRD READING\n" +
            "2009S03296H406/02/10 AMENDED ON THIRD READING 3296H\n" +
            "2009S03296H406/22/10 REPASSED SENATE\n" +
            "2009S03296H406/22/10 RETURNED TO ASSEMBLY";

    private static BillId S3778D = new BillId("S3778", 201, "D");
    private static String S3778D_actions = "2011S03778D403/03/11 REFERRED TO TRANSPORTATION\n" +
            "2011S03778D403/22/11 AMEND AND RECOMMIT TO TRANSPORTATION\n" +
            "2011S03778D403/22/11 PRINT NUMBER 3778A\n" +
            "2011S03778D406/10/11 AMEND (T) AND RECOMMIT TO TRANSPORTATION\n" +
            "2011S03778D406/10/11 PRINT NUMBER 3778B\n" +
            "2011S03778D406/13/11 COMMITTEE DISCHARGED AND COMMITTED TO RULES\n" +
            "2011S03778D406/14/11 ORDERED TO THIRD READING CAL.1219\n" +
            "2011S03778D406/14/11 PASSED SENATE\n" +
            "2011S03778D406/14/11 DELIVERED TO ASSEMBLY\n" +
            "2011S03778D406/14/11 referred to corporations, authorities and commissions\n" +
            "2011S03778D401/04/12 died in assembly\n" +
            "2011S03778D401/04/12 returned to senate\n" +
            "2011S03778D401/04/12 REFERRED TO TRANSPORTATION\n" +
            "2011S03778D401/09/12 REPORTED AND COMMITTED TO FINANCE\n" +
            "2011S03778D403/01/12 AMEND AND RECOMMIT TO FINANCE\n" +
            "2011S03778D403/01/12 PRINT NUMBER 3778C\n" +
            "2011S03778D405/01/12 AMEND (T) AND RECOMMIT TO FINANCE\n" +
            "2011S03778D405/01/12 PRINT NUMBER 3778D\n" +
            "2011S03778D405/08/12 1ST REPORT CAL.725\n" +
            "2011S03778D405/09/12 2ND REPORT CAL.\n" +
            "2011S03778D405/14/12 ADVANCED TO THIRD READING\n" +
            "2011S03778D405/15/12 PASSED SENATE\n" +
            "2011S03778D405/15/12 DELIVERED TO ASSEMBLY\n" +
            "2011S03778D405/15/12 referred to ways and means\n" +
            "2011S03778D406/18/12 RECALLED FROM ASSEMBLY\n" +
            "2011S03778D406/18/12 returned to senate\n" +
            "2011S03778D406/18/12 VOTE RECONSIDERED - RESTORED TO THIRD READING\n" +
            "2011S03778D406/18/12 AMENDED ON THIRD READING 3778E\n" +
            "2011S03778D406/20/12 ENACTING CLAUSE STRICKEN";

    private static BillId A5060 = new BillId("A5060", 2013);
    private static String A5060_actions =
        "2013A05060E402/15/13 referred to energy\n" +
        "2013A05060E405/13/13 amend (t) and recommit to energy\n" +
        "2013A05060E405/13/13 print number 5060a\n" +
        "2013A05060E405/16/13 amend (t) and recommit to energy\n" +
        "2013A05060E405/16/13 print number 5060b\n" +
        "2013A05060E405/30/13 reported referred to ways and means\n" +
        "2013A05060E406/04/13 reported referred to rules\n" +
        "2013A05060E406/06/13 amend and recommit to rules 5060c\n" +
        "2013A05060E406/11/13 reported\n" +
        "2013A05060E406/11/13 rules report cal.193\n" +
        "2013A05060E406/11/13 ordered to third reading rules cal.193\n" +
        "2013A05060E406/17/13 amended on third reading 5060d\n" +
        "2013A05060E406/20/13 passed assembly\n" +
        "2013A05060E406/20/13 delivered to senate\n" +
        "2013A05060E406/20/13 REFERRED TO RULES\n" +
        "2013A05060E401/08/14 DIED IN SENATE\n" +
        "2013A05060E401/08/14 RETURNED TO ASSEMBLY\n" +
        "2013A05060E401/08/14 ordered to third reading cal.224\n" +
        "2013A05060E402/03/14 amended on third reading 5060e\n" +
        "2013A05060E406/10/14 passed assembly\n" +
        "2013A05060E406/10/14 delivered to senate\n" +
        "2013A05060E406/10/14 REFERRED TO ENERGY AND TELECOMMUNICATIONS";

    private static BillId S5052 = new BillId("S5052", 2013);
    private static String S5052_actions = "2013S05052A405/07/13 REFERRED TO HOUSING, CONSTRUCTION AND COMMUNITY DEVELOPMENT\n" +
            "2013S05052A406/04/13 1ST REPORT CAL.1025\n" +
            "2013S05052A406/05/13 AMENDED (T) 5052A\n" +
            "2013S05052A406/05/13 2ND REPORT CAL.\n" +
            "2013S05052A406/10/13 ADVANCED TO THIRD READING\n" +
            "2013S05052A406/21/13 PASSED SENATE\n" +
            "2013S05052A406/21/13 DELIVERED TO ASSEMBLY\n" +
            "2013S05052A406/21/13 referred to real property taxation\n" +
            "2013S05052A401/08/14 died in assembly\n" +
            "2013S05052A401/08/14 returned to senate\n" +
            "2013S05052A401/08/14 REFERRED TO HOUSING, CONSTRUCTION AND COMMUNITY DEVELOPMENT\n";

    @Test
    public void testBasicParse() throws Exception {
        BillActionParser parser = new BillActionParser(S5052, S5052_actions, Optional.of(new PublishStatus(true, LocalDateTime.now())));
        parser.parseActions();
        parser.getBillActions().forEach(a -> logger.info("{}", a.toString()));
        logger.info("Current Committee {}", parser.getCurrentCommittee());
        logger.info("All committees {}", parser.getPastCommittees());
        logger.info("Active Version {}", parser.getActiveVersion());
        logger.info("Publish Status Map {}", parser.getPublishStatusMap());
        logger.info("Same As Map {}", parser.getSameAsMap());
        logger.info("Stricken {}", parser.isStricken());
    }

    @Test
    public void testUnPublish() throws Exception {

    }
}
