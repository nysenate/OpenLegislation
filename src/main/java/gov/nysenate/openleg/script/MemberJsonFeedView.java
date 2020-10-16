package gov.nysenate.openleg.script;

public class MemberJsonFeedView {
    private int open_leg_id;
    private int senate_district;
    private boolean is_active;
    private String full_name;
    private String first_name;
    private String last_name;
    private String short_name;
    private String email;
    private String img;

    public MemberJsonFeedView() {
    }

    public int getOpen_leg_id() {
        return open_leg_id;
    }

    public int getSenate_district() {
        return senate_district;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getEmail() {
        return email;
    }

    public String getImg() {
        return img;
    }
}
