package edots.models;

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
