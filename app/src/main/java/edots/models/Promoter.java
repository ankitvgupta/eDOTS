package edots.models;

/**
 * Created by jfang on 1/6/15.
 */
public class Promoter {
    private String username;
    private String name;
    private Locale locale;
    private String password;

    public Promoter(){

    }

    public Promoter(String u, String n, Locale l, String p){
        username = u;
        name=n;
        locale = l;
        password= p;

    }

    public String getUsername(){
        return username;
    }

    public String getName(){
        return name;
    }

    public Locale getLocale(){
        return locale;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String u){
        username=u;
    }

    public void setName(String n){
        name=n;
    }

    public void setLocale(Locale l){locale=l;
    }

    public void setPassword(String p){
        password = p;
    }

}
