package edots.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * Created by jfang on 1/6/15.
 */
public class Patient extends Object{
    private Long id;
    private String name;
    private String fathersName;
    private String mothersName;
    private Date birthDate;
    private Long nationalID;
    private String sex;
    private ArrayList<Project> enrolledProjects = new ArrayList<Project>();

    public Patient(){

    }

    public Patient (String n, Date d, Long id, String s, ArrayList<Project> projects, String mother, String father){
        name = n;
        birthDate = d;
        nationalID = id;
        sex = s;
        enrolledProjects = projects;
        mothersName = mother;
        fathersName = father;
    }
    // For testing only

    public Patient(Long n){
        name ="Brendan";
        birthDate = new Date();
        nationalID = n;
        sex ="Female";
        mothersName = "Mary";
        fathersName = "John";
        Project testProject = new Project();
        Project testProject2 = new Project();
        enrolledProjects = new ArrayList<Project>(Arrays.asList(testProject, testProject2));
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
            birthDate = new Date(Long.valueOf(n.get("birthDate").toString()));
            nationalID = Long.valueOf(n.get("nationalID").toString());
            sex = n.get("sex").toString();
            enrolledProjects = new ArrayList<Project>();
            JSONArray arry = new JSONArray(n.get("enrolledProjects").toString());
            for (int i = 0; i < arry.length(); i++){
                enrolledProjects.add(new Project(arry.getString(i)));
            }
        }
        catch (JSONException e) {
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
            temp.put("birthDate", Long.toString(getBirthDate().getTime()));
            temp.put("nationalID", getNationalID());
            temp.put("sex", getSex());
            temp.put("enrolledProjects", getEnrolledProjects());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp.toString();

    }

    public String getName(){
        return name;
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
}
