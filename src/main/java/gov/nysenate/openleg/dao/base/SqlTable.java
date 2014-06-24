package gov.nysenate.openleg.dao.base;

public enum SqlTable
{
    BILL                         ("bill"),
    BILL_AMENDMENT               ("bill_amendment"),
    BILL_AMENDMENT_ACTION        ("bill_amendment_action"),
    BILL_AMENDMENT_COSPONSOR     ("bill_amendment_cosponsor"),
    BILL_AMENDMENT_SAME_AS       ("bill_amendment_same_as"),
    BILL_AMENDMENT_VOTE          ("bill_amendment_vote");

    String tableName;

    SqlTable(String tableName) {
        this.tableName = tableName;
    }

    public String table(String schema) {
        return schema + "." + tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
