package edots.models;
import java.util.ArrayList;

/**
 * Created by jfang on 1/6/15.
 */
public class Locale {
    private String name;
    private ArrayList<Promoter> localePromoters = new ArrayList<Promoter>();

    public Locale(){

    }
    public String getName(){
        return name;
    }

    public void setName(String n){
        name=n;
    }

    public void addPromoter(Promoter p){
        localePromoters.add(p);
    }
    public ArrayList<Promoter> getLocalePromoters(){
        return localePromoters;
    }

}
