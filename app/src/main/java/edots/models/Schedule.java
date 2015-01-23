package edots.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ankit on 1/19/15.
 * 
 *  
 */
public class Schedule {
    
    private String lunes;
    private String lunesTarde;
    private String martes;
    private String martesTarde;
    private String miercoles;
    private String miercolesTarde;
    private String jueves;
    private String juevesTarde;
    private String viernes;
    private String viernesTarde;
    private String sabado;
    private String sabadoTarde;
    private String domingo;
    private String domingoTarde;

    private String startDate;
    private String endDate;
    
    public Schedule(){
        this.lunes = "0";
        this.lunesTarde = "0";
        this.martes = "1";
        this.martesTarde = "0";
        this.miercoles = "1";
        this.miercolesTarde = "1";
        this.jueves = "0";
        this.juevesTarde = "1";
        this.viernes = "0";
        this.viernesTarde = "0";
        this.sabado = "0";
        this.sabadoTarde = "0";
        this.domingo = "0";
        this.domingoTarde = "0";
        this.startDate = "01/12/2014";
        this.endDate = "15/02/2015";
    };
    
    public Schedule (String l, String lT, String ma, String maT, String mi, String miT, String j, String jT,
                     String v, String vT, String s, String sT, String d, String dT, String start, String end){
        this.lunes = l;
        this.lunesTarde = lT;
        this.martes = ma;
        this.martesTarde = maT;
        this.miercoles = mi;
        this.miercolesTarde = miT;
        this.jueves = j;
        this.juevesTarde = jT;
        this.viernes = v;
        this.viernesTarde = vT;
        this.sabado = s;
        this.sabadoTarde = sT;
        this.domingo = d;
        this.domingoTarde = dT;
        this.startDate = start;
        this.endDate = end;
        
    }


    /**
     * @param JSONString a JSON object representing the project
     */
    public Schedule(String JSONString){
        // TODO: check it is 0101010 format
        try {
            JSONObject n = new JSONObject(JSONString);
            String days = n.get("days").toString();
            lunes = Character.toString(days.charAt(0));
            lunesTarde = Character.toString(days.charAt(1));
            martes = Character.toString(days.charAt(2));
            martesTarde = Character.toString(days.charAt(3));
            miercoles = Character.toString(days.charAt(4));
            miercolesTarde = Character.toString(days.charAt(5));
            jueves = Character.toString(days.charAt(6));
            juevesTarde = Character.toString(days.charAt(7));
            viernes = Character.toString(days.charAt(8));
            viernesTarde = Character.toString(days.charAt(9));
            sabado = Character.toString(days.charAt(10));
            sabadoTarde = Character.toString(days.charAt(11));
            domingo = Character.toString(days.charAt(12));
            domingoTarde = Character.toString(days.charAt(13));
            startDate = n.get("startDate").toString();
            endDate = n.get("endDate").toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    /**
     * @return JSON representation of the schedule
     *
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("days", toOneHotCoding());
            temp.put("startDate", getStartDate());
            temp.put("endDate", getEndDate());
        } catch (JSONException e) {
            Log.v("JSON Exception", "Found a JSON Exception");
            e.printStackTrace();
        }
        return temp.toString();
    }

    
    /**
     * @return 14 digit one-hot coding for the schedule
     *
     */
    public String toOneHotCoding(){
        return lunes+lunesTarde+martes+martesTarde+miercoles+miercolesTarde+jueves+juevesTarde+viernes+viernesTarde+sabado+sabadoTarde+domingo+domingoTarde;
    }
    
    public String getLunes() {
        return lunes;
    }

    public String getLunesTarde() { return lunesTarde; }

    public String getMartes() {
        return martes;
    }

    public String getMartesTarde() { return martesTarde; }

    public String getMiercoles() {
        return miercoles;
    }

    public String getMiercolesTarde() { return miercolesTarde; }

    public String getJueves() {
        return jueves;
    }

    public String getJuevesTarde() { return juevesTarde; }

    public String getViernes() {
        return viernes;
    }

    public String getViernesTarde() { return viernesTarde; }

    public String getSabado() {
        return sabado;
    }

    public String getSabadoTarde() {return sabadoTarde; }

    public String getDomingo() {
        return domingo;
    }

    public String getDomingoTarde() { return domingoTarde; }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public void setLunesTarde(String lunesTarde) { this.lunesTarde = lunesTarde; }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public void setMartesTarde(String martesTarde) { this.martesTarde = martesTarde; }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public void setMiercolesTarde(String miercolesTarde) { this.miercolesTarde = miercolesTarde; }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public void setJuevesTarde(String juevesTarde) { this.juevesTarde = juevesTarde; }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public void setViernesTarde(String viernesTarde) { this.viernesTarde = viernesTarde; }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public void setSabadoTarde(String sabadoTarde) { this.sabadoTarde = sabadoTarde; }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }

    public void setDomingoTarde(String domingoTarde) { this.domingoTarde = domingoTarde; }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    
    public Boolean scheduledLunes(){ return this.lunes.equals("1"); }
    public Boolean scheduledLunesTarde() { return this.lunesTarde.equals("1"); }
    public Boolean scheduledMartes(){ return this.martes.equals("1"); }
    public Boolean scheduledMartesTarde() { return this.martesTarde.equals("1"); }
    public Boolean scheduledMiercoles(){ return this.miercoles.equals("1"); }
    public Boolean scheduledMiercolesTarde() {return this.miercolesTarde.equals("1"); }
    public Boolean scheduledJueves(){ return this.jueves.equals("1"); }
    public Boolean scheduledJuevesTarde() {return this.juevesTarde.equals("1"); }
    public Boolean scheduledViernes(){return this.viernes.equals("1"); }
    public Boolean scheduledViernesTarde() {return this.viernesTarde.equals("1"); }
    public Boolean scheduledSabado(){ return this.sabado.equals("1"); }
    public Boolean scheduledSabadoTarde() {return this.sabadoTarde.equals("1"); }
    public Boolean scheduledDomingo(){ return this.domingo.equals("1"); }
    public Boolean scheduledDomingoTarde() {return this.domingoTarde.equals("1"); }

}
