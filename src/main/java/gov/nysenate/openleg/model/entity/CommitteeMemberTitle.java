package gov.nysenate.openleg.model.entity;

public enum CommitteeMemberTitle
{
    CHAIR_PERSON,
    VICE_CHAIR,
    MEMBER
    ;

    public String asSqlEnum(){
        return this.toString().toLowerCase();
    }
    public static CommitteeMemberTitle valueOfSqlEnum(String sqlEnum){
        return CommitteeMemberTitle.valueOf(sqlEnum.toUpperCase());
    }
}
