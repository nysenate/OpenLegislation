package gov.nysenate.openleg.processors;

public enum MemberType {
    PERSON,
    MEMBER,
    SESSION;

    public static MemberType getMemberType(String action) {
        try {
            return MemberType.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
