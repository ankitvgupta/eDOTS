package edots.models;
import java.util.ArrayList;

/**
 * Created by jfang on 1/6/15.
 */
public class Site {
    private String name;
    private ArrayList<Promoter> sitePromoters = new ArrayList<Promoter>();

    public Site(){

    }
    public String getName(){
        return name;
    }

    public void setName(String n){
        name=n;
    }

    public void addPromoter(Promoter p){
        sitePromoters.add(p);
    }
    public ArrayList<Promoter> getSitePromoters(){
        return sitePromoters;
    }

}
