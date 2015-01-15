package edots.models;

/**
 * Created by Ankit on 1/12/15.
 */
public class Visit {

    private String SiteCode;
    private String ProjectCode;
    private String VisitGroupCode;
    private String NombreGroupoVisita; // for example, "Tamizaje" or "Enrolamiento"
    private String VisitCode;
    private String DescripcionVisita; // for example, "Tamizaje" or "Enrolamiento"
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
        NombreGroupoVisita="test9";
        DescripcionVisita="test10";
    }

    // for production
    public Visit(String site, String project, String visitGroup,String nombreGroupoVisita,
                 String vis, String descripcionVisita, String patient, String date, String time, String promoter){
        SiteCode=site;
        ProjectCode=project;
        VisitGroupCode = visitGroup;
        NombreGroupoVisita = nombreGroupoVisita;
        VisitCode = vis;
        DescripcionVisita = descripcionVisita;
        PacientCode = patient;
        VisitDate = date;
        TimeVal = time;
        UserCode = promoter;
    }

    public String getSiteCode() {return SiteCode;}

    public String getProjectCode() {return ProjectCode;}

    public String getVisitGroupCode() {return VisitGroupCode;}

    public String getNombreGroupoVisita() {return NombreGroupoVisita;}

    public String getVisitCode() {return VisitCode;}

    public String getDescripcionVisita() {return DescripcionVisita;}

    public String getPacientCode() {return PacientCode;}

    public String getVisitDate() {return VisitDate;}

    public String getTimeVal() {return TimeVal;}

    public String getUserCode() {return UserCode;}

}
