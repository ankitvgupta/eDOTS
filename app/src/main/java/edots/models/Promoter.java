package edots.models;

/**
 * Created by jfang on 1/6/15.
 */
public class Promoter {
    private String username;
    private String name;
    private Site site;

    public Promoter(){

    }
    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public Site getSite(){
        return site;
    }

    public void setUsername(String u){
        username=u;
    }

    public void setName(String n){
        name=n;
    }

    public void setSite(Site s){
        site=s;
    }

}
