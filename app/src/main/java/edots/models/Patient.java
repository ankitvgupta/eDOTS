package edots.models;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.tasks.GetHistoryLoadTask;


/**
 *
 * @author jfang
 * @author ankitgupta
 * @since 2015-01-06
 * @see java.util.Date
 *
 * Model for Patients
 *
 */
public class Patient extends Saveable{
    private String pid;
    private int doctype;
    private String name;
    private String fathersName;
    private String mothersName;
    private Date birthDate;
    private String nationalID;
    private String sex;
    private Schema enrolledSchema;

    public Patient(){

    }

    // For production

    /**
     *
     * @param n name of Patient
     * @param d Date of birth (as a date object)
     * @param nid national id
     * @param s sex
     * @param schema ArrayList of projects enrolled in
     * @param mother mothers's name
     * @param father father's name
     * @param patientID Patient ID
     * @param doc document type
     */
    public Patient (String n, Date d, String nid, String s, Schema schema, String mother, String father, String patientID, int doc){
        name = n;
        birthDate = d;
        nationalID = nid;
        sex = s;
        enrolledSchema = schema;
        mothersName = mother;
        fathersName = father;
        pid = patientID;
        doctype = doc;


    }

    /**
     * This is for testing only
     *
     * @deprecated
     * @param n the national id
     *
     */
    public Patient(String n, String type){
        name ="Brendan";
        pid = "01723-X72312-7123";
        birthDate = new Date();
        nationalID = n;
        sex ="Female";
        mothersName = "Mary";
        fathersName = "John";
        enrolledSchema = new Schema();
        doctype = 1;
    }

    /** Added to parse a string back into the JSON form.
     *
     *  @author ankitgupta
     *  @param JSONString A JSON Serialization of the Patient Obkect
     *
     */
    public Patient (String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            name = n.get("name").toString();
            fathersName = n.get("fathersName").toString();
            mothersName = n.get("mothersName").toString();
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            birthDate = parser.parse(n.get("birthDate").toString());
            sex = n.get("sex").toString();
            pid = n.get("pid").toString();
            doctype = Integer.valueOf(n.get("doctype").toString());
            enrolledSchema = new Schema(n.get("enrolledProject").toString());
            /*JSONArray arry = new JSONArray(n.get("enrolledProjects").toString());
            for (int i = 0; i < arry.length(); i++){
                enrolledProjects.add(new Project(arry.getString(i)));
            }*/
            nationalID = n.get("nationalID").toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    /**
     * @author ankitgupta
     * @return the JSON Serialization of the Patient Object
     *
     */
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("name", getName());
            temp.put("fathersName", getFathersName());
            temp.put("mothersName", getMothersName());

            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            String birthday = parser.format(getBirthDate());

            temp.put("birthDate", birthday);
            temp.put("nationalID", getNationalID());
            temp.put("sex", getSex());
            temp.put("enrolledProject", getEnrolledSchema());
            temp.put("pid", getPid());
            temp.put("doctype",  Integer.toString(getDoctype()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp.toString();

    }

    /**
     * @author Ankit
     * @return this patient's schedule
     */
    public Schedule getPatientSchedule(){
        
        return new Schedule(); // TODO: This line is just for testing, for production do the below.
        
        //TODO: The below is the real code
        /*
        String patientCode = pid; // for production
        //String patientCode = "D74CCD37-8DE4-447C-946E-1300E9498577"; // for testing only
        GetPatientScheduleLoadTask newP = new GetPatientScheduleLoadTask();
        AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", patientCode);
        try {
            Schedule s = (Schedule) p.get();
            Log.v("Patient.java: The schedule is", s.toString());
            return s;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        return null;
        */
        
    }

    /**
     * @author ankitgupta
     * @return a list of this patient's visits, as an ArrayList of Visits
     */
    public ArrayList<Visit> getPatientHistory (Context context){
        String patientCode = pid; // for production
        //String patientCode = "D74CCD37-8DE4-447C-946E-1300E9498577"; // for testing only

        GetHistoryLoadTask newP = new GetHistoryLoadTask(context);
        AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", patientCode);
        try {
            ArrayList<Visit> visits = (ArrayList<Visit>) p.get();
            Log.v("Patient.java: The visits are", visits.toString());
            Log.e("Patientjava getPatientHistory number of visits", String.valueOf(visits.size()));
            return visits;
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        return null;


    }

    public String getName(){
        return name;
    }

    public String getPid(){
        return pid;
    }

    public int getDoctype(){
        return doctype;
    }

    public String getFathersName(){
        return fathersName;
    }

    public String getMothersName(){
        return mothersName;
    }

    public Date getBirthDate(){
        return birthDate;
    }

    public String getNationalID(){
        return nationalID;
    }

    public String getSex(){
        return sex;
    }

    public Schema getEnrolledSchema(){
        return enrolledSchema;
    }

    public void setName(String n){
        name=n;
    }

    public void setDoctype(int n) {doctype = n;}

    public void setFathersName(String n){
        fathersName=n;
    }

    public void setMothersName(String n){
        mothersName=n;
    }

    public void setBirthDate(Date d){
        birthDate = d;
    }

    public void setNationalID(String i){
        nationalID=i;
    }

    public void setSex(String s){
        sex=s;
    }

    public void setPid(String s){
        pid=s;
    }

    public void setEnrolledSchema(Schema p){
        enrolledSchema = p;
    }
}
