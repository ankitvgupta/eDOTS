package edots.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/**
 * Created by jfang on 1/6/15.
 */
public class Patient {
    private String name;
    private Date birthDate;
    private Long nationalID;
    private String sex;
    private ArrayList<Project> enrolledProjects = new ArrayList<Project>();

    public Patient(){

    }

    public Patient (String n, Date d, Long id, String s, ArrayList<Project> projects){
        name=n;
        birthDate = d;
        nationalID = id;
        sex = s;
        enrolledProjects = projects;
    }
    // For testing only
    public Patient(String n){
        name=n;
        birthDate= new Date();
        nationalID= Long.MAX_VALUE;
        sex="Female";
        Project testProject = new Project("testProject1");
        Project testProject2 = new Project("testProject2");
        enrolledProjects= new ArrayList<Project>(Arrays.asList(testProject, testProject2));

    }

    /** Added to parse a string back into the JSON form.
     *  Done by ankitgupta
     */
    public Patient (JSONObject n) {
        try {

            name = n.get("name").toString();
            birthDate = new Date(Long.valueOf(n.get("birthDate").toString()));
            nationalID = Long.valueOf(n.get("nationalID").toString());
            sex = n.get("sex").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // TODO: Need to figure out how to parse the arraylist representation (probably easy just haven't done it yet)



    }

    @Override
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("name", getName());
            temp.put("birthDate", Long.toString(getBirthDate().getTime()));
            temp.put("nationalID", getNationalID());
            temp.put("sex", getSex());
            temp.put("enrolledProjects", getEnrolledProjects());
        } catch (JSONException e) {
            Log.v("JSON Exception", "Found a JSON Exception");
            e.printStackTrace();
        }

        return temp.toString();
    }

    public String getName(){
        return name;
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
