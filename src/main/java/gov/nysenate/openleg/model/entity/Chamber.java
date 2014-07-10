package gov.nysenate.openleg.model.entity;

/**
 * Enumeration of the different legislative chambers.
 */
public enum Chamber
{
    SENATE,
    ASSEMBLY;

    public String asSqlEnum(){
        return this.toString().toLowerCase();
    }
    public static Chamber valueOfSqlEnum(String sqlEnum){
        return Chamber.valueOf(sqlEnum.toUpperCase());
    }
}
