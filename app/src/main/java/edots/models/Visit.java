package edots.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ankit on 1/12/15.
 */
public class Visit extends Saveable {

    private String LocaleCode;
    private String ProjectCode;
    private String VisitGroupCode;
    private String NombreGrupoVisita; // for example, "Tamizaje" or "Enrolamiento"
    private String VisitCode;
    private String DescripcionVisita; // for example, "Tamizaje" or "Enrolamiento"
    private String PacientCode;
    private String VisitDate;
    private String VisitTime;
    private String PromoterId;

    // for testing only
    public Visit() {
        LocaleCode="1";
        ProjectCode="2";
        VisitGroupCode = "3";
        VisitCode = "4";
        PacientCode = "30C85C6A-D30E-48D2-949B-0004965E626F";
        VisitDate = "2015-01-19 00:00:00.0";
        VisitTime = "15:46:00.0000000";
        NombreGrupoVisita="test9";
        DescripcionVisita="test10";
        PromoterId = "19";
    }

    // for production
    public Visit(String site, String project, String visitGroup,String nombreGroupoVisita,
                 String vis, String descripcionVisita, String patient, String date, String time, String promoterId){
        LocaleCode=site;
        ProjectCode=project;
        VisitGroupCode = visitGroup;
        NombreGrupoVisita = nombreGroupoVisita;
        VisitCode = vis;
        DescripcionVisita = descripcionVisita;
        PacientCode = patient;
        VisitDate = date;
        VisitTime = time;
        PromoterId = promoterId;
    }

    /**
     *
     * @author JN
     * @param JSONobj
     */
    public Visit(String JSONobj){
        try {
            JSONObject n = new JSONObject(JSONobj);
            LocaleCode = n.get("LocaleCode").toString();
            ProjectCode = n.get("VisitGroupCode").toString();
            VisitGroupCode = n.get("VisitGroupCode").toString();
            VisitCode = n.get("VisitCode").toString();
            PacientCode = n.get("PacientCode").toString();
            VisitDate = n.get("VisitDate").toString();
            VisitTime = n.get("VisitTime").toString();
            NombreGrupoVisita = n.get("NombreGrupoVisita").toString();
            DescripcionVisita = n.get("DescripcionVisita").toString();
            PromoterId = n.get("PromoterId").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    /**
     * @author lili
     * @return the JSON Serialization of the Visit Object
     *
     */
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("LocaleCode", getLocaleCode());
            temp.put("ProjectCode", getProjectCode());
            temp.put("VisitGroupCode", getVisitGroupCode());
            temp.put("VisitCode", getVisitCode());
            temp.put("PacientCode", getPacientCode());
            temp.put("VisitDate", getVisitDate());
            temp.put("VisitTime", getVisitTime());
            temp.put("NombreGrupoVisita", getNombreGrupoVisita());
            temp.put("DescripcionVisita",  getDescripcionVisita());
            temp.put("PromoterId", getPromoterId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }

    public String getLocaleCode() {return LocaleCode;}

    public void setLocaleCode(String localeCode) {LocaleCode = localeCode;}

    public String getProjectCode() {return ProjectCode;}

    public String getVisitGroupCode() {return VisitGroupCode;}

    public String getNombreGrupoVisita() {return NombreGrupoVisita;}

    public String getVisitCode() {return VisitCode;}

    public String getDescripcionVisita() {return DescripcionVisita;}

    public String getPacientCode() {return PacientCode;}

    public String getVisitDate() {return VisitDate;}

    public void setVisitDate(String date) {VisitDate = date;}

    public String getVisitTime() {return VisitTime;}

    public void setVisitTime(String time) {VisitTime = time;}

    public void setPromoterId(String promoterId) { PromoterId = promoterId;}

    public String getPromoterId(){return PromoterId;}

}
