package edots.models;

/**
 * Created by Ankit on 1/12/15.
 */
public class Visit {

    private String LocaleCode;
    private String ProjectCode;
    private String VisitGroupCode;
    private String NombreGrupoVisita; // for example, "Tamizaje" or "Enrolamiento"
    private String VisitCode;
    private String DescripcionVisita; // for example, "Tamizaje" or "Enrolamiento"
    private String PacientCode;
    private String VisitDate;
    private String VisitTime;
    private String UserCode;

    // for testing only
    public Visit() {
        LocaleCode="test1";
        ProjectCode="test2";
        VisitGroupCode = "test3";
        VisitCode = "test4";
        PacientCode = "test5";
        VisitDate = "test6";
        VisitTime = "test7";
        UserCode = "test8";
        NombreGrupoVisita="test9";
        DescripcionVisita="test10";
    }

    // for production
    public Visit(String site, String project, String visitGroup,String nombreGroupoVisita,
                 String vis, String descripcionVisita, String patient, String date, String time, String promoter){
        LocaleCode=site;
        ProjectCode=project;
        VisitGroupCode = visitGroup;
        NombreGrupoVisita = nombreGroupoVisita;
        VisitCode = vis;
        DescripcionVisita = descripcionVisita;
        PacientCode = patient;
        VisitDate = date;
        VisitTime = time;
        UserCode = promoter;
    }

    public String getLocaleCode() {return LocaleCode;}

    public void setLocaleCode(String localeCode) {LocaleCode = localeCode;}

    public String getProjectCode() {return ProjectCode;}

    public String getVisitGroupCode() {return VisitGroupCode;}

    public String getNombreGroupoVisita() {return NombreGrupoVisita;}

    public String getVisitCode() {return VisitCode;}

    public String getDescripcionVisita() {return DescripcionVisita;}

    public String getPacientCode() {return PacientCode;}

    public String getVisitDate() {return VisitDate;}

    public void setVisitDate(String date) {VisitDate = date;}

    public String getVisitTime() {return VisitTime;}

    public void setVisitTime(String time) {VisitTime = time;}

    public String getUserCode() {return UserCode;}

}
