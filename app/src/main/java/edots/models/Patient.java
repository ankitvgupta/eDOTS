package edots.models;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edots.tasks.GetHistoryLoadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * Created by jfang on 1/6/15.
 * Modified by ankitvgupta since
 */
public class Patient extends Object{
    private String pid;
    private int doctype;
    private String name;
    private String fathersName;
    private String mothersName;
    private Date birthDate;
    private Long nationalID;
    private String sex;
    private ArrayList<Project> enrolledProjects = new ArrayList<Project>();

    public Patient(){

    }

    // For production
    public Patient (String n, Date d, Long nid, String s, ArrayList<Project> projects, String mother, String father, String patientID, int doc){
        name = n;
        birthDate = d;
        nationalID = nid;
        sex = s;
        enrolledProjects = projects;
        mothersName = mother;
        fathersName = father;
        pid = patientID;
        doctype = doc;


    }

    // For testing only
    public Patient(Long n){
        name ="Brendan";
        pid = "01723-X72312-7123";
        birthDate = new Date();
        nationalID = n;
        sex ="Female";
        mothersName = "Mary";
        fathersName = "John";
        Project testProject = new Project();
        Project testProject2 = new Project();
        enrolledProjects = new ArrayList<Project>(Arrays.asList(testProject, testProject2));
        doctype = 1;
    }

    /** Added to parse a string back into the JSON form.
     *  Done by ankitgupta
     */
    public Patient (String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            name = n.get("name").toString();
            fathersName = n.get("fathersName").toString();
            mothersName = n.get("mothersName").toString();

            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            birthDate = parser.parse(n.get("birthDate").toString());
            nationalID = Long.valueOf(n.get("nationalID").toString());
            sex = n.get("sex").toString();
            pid = n.get("pid").toString();
            doctype = Integer.valueOf(n.get("doctype").toString());
            enrolledProjects = new ArrayList<Project>();
            JSONArray arry = new JSONArray(n.get("enrolledProjects").toString());
            for (int i = 0; i < arry.length(); i++){
                enrolledProjects.add(new Project(arry.getString(i)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
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
            temp.put("enrolledProjects", getEnrolledProjects());
            temp.put("pid", getPid());
            temp.put("doctype",  Integer.toString(getDoctype()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp.toString();

    }

    public void getPatientHistory (){
        //String patientCode = pid; // for production
        String patientCode = "0B717865-BD22-47D1-A62F-A5C3883D0D34"; // for testing only
        GetHistoryLoadTask newP = new GetHistoryLoadTask();
        AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", patientCode);


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

    public Long getNationalID(){
        return nationalID;
    }

    public String getSex(){
        return sex;
    }

    public ArrayList<Project> getEnrolledProjects(){
        return enrolledProjects;
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

    public void setNationalID(long i){
        nationalID=i;
    }

    public void setSex(String s){
        sex=s;
    }

    public void setPid(String s){
        pid=s;
    }
}
