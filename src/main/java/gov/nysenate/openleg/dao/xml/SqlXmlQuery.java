package gov.nysenate.openleg.dao.xml;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlXmlQuery implements BasicSqlQuery {

    private String sql;

    SqlXmlQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

}
