package edots.models;

/**
 * Created by Ankit on 1/12/15.
 */
public class Visit {

    private String SiteCode;
    private String ProjectCode;
    private String VisitGroupCode;
    private String VisitCode;
    private String PacientCode;
    private String VisitDate;
    private String TimeVal;
    private String UserCode;

    // for testing only
    public Visit() {
        SiteCode="test1";
        ProjectCode="test2";
        VisitGroupCode = "test3";
        VisitCode = "test4";
        PacientCode = "test5";
        VisitDate = "test6";
        TimeVal = "test7";
        UserCode = "test8";
    }

    // for production
    public Visit(String site, String project, String visitgroup,
                 String vis, String patient, String date, String time, String promoter){
        SiteCode=site;
        ProjectCode=project;
        VisitGroupCode = visitgroup;
        VisitCode = vis;
        PacientCode = patient;
        VisitDate = date;
        TimeVal = time;
        UserCode = promoter;
    }



}
