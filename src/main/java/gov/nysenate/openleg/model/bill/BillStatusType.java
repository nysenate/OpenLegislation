package gov.nysenate.openleg.model.bill;

/**
 * An enumeration of the different stages a bill can be in. This listing is not meant to be
 * granular but rather provides a high level bill status to the end user. This status should
 * be determined by parsing the actions list. It is worth noting that bills will generally
 * cycle through these statuses as they pass/die in a house and move to the other one.
 */
public enum BillStatusType
{
    INTRODUCED("Introduced"),
    IN_ASSEMBLY_COMM("In Assembly Committee"),
    IN_SENATE_COMM("In Senate Committee"),
    ASSEMBLY_FLOOR("Assembly Floor Calendar"),
    SENATE_FLOOR("Senate Floor Calendar"),
    PASSED_ASSEMBLY("Passed Assembly"),
    PASSED_SENATE("Passed Senate"),
    DELIVERED_TO_GOV("Delivered to Governor"),
    SIGNED_BY_GOV("Signed by Governor"),
    VETOED("Vetoed"),
    STRICKEN("Stricken"),
    LOST("Lost"),
    SUBSTITUTED("Substituted"),
    ADOPTED("Adopted");

    String desc;

    BillStatusType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}