package edots.models;

/**
 * Created by Ankit on 1/19/15.
 * 
 *  
 */
public class Schedule {
    
    private String codigoPaciente;
    private String lunes;
    private String martes;
    private String miercoles;
    private String jueves;
    private String viernes;
    private String sabado;
    private String domingo;
    private String startDate;
    private String endDate;
    
    public Schedule(){
        codigoPaciente = "1";
        this.lunes = "1";
        this.martes = "1";
        this.miercoles = "0";
        this.jueves = "0";
        this.viernes = "1";
        this.sabado = "0";
        this.domingo = "0";
        this.startDate = "01/12/2014";
        this.endDate = "15/02/2015";
    };
    
    public Schedule (String code, String l, String ma, String mi, String j, String v, String s, String d, String start, String end){
        this.codigoPaciente = code;
        this.lunes = l;
        this.martes = ma;
        this.miercoles = mi;
        this.jueves = j;
        this.viernes = v;
        this.sabado = s;
        this.domingo = d;
        this.startDate = start;
        this.endDate = end;
        
    }
    
   public String getCodigoPaciente(){
       return codigoPaciente;    
   }

    public String getLunes() {
        return lunes;
    }

    public String getMartes() {
        return martes;
    }

    public String getMiercoles() {
        return miercoles;
    }

    public String getJueves() {
        return jueves;
    }

    public String getViernes() {
        return viernes;
    }

    public String getSabado() {
        return sabado;
    }

    public String getDomingo() {
        return domingo;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setCodigoPaciente(String codigoPaciente) {
        this.codigoPaciente = codigoPaciente;
    }

    public void setLunes(String lunes) {
        this.lunes = lunes;
    }

    public void setMartes(String martes) {
        this.martes = martes;
    }

    public void setMiercoles(String miercoles) {
        this.miercoles = miercoles;
    }

    public void setJueves(String jueves) {
        this.jueves = jueves;
    }

    public void setViernes(String viernes) {
        this.viernes = viernes;
    }

    public void setSabado(String sabado) {
        this.sabado = sabado;
    }

    public void setDomingo(String domingo) {
        this.domingo = domingo;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    
    public Boolean scheduledLunes(){
        return this.lunes == "1";
    }
    public Boolean scheduledMartes(){
        return this.martes == "1";
    }
    public Boolean scheduledMiercoles(){
        return this.miercoles == "1";
    }
    public Boolean scheduledJueves(){
        return this.jueves == "1";
    }
    public Boolean scheduledViernes(){
        return this.viernes == "1";
    }
    public Boolean scheduledSabado(){
        return this.sabado == "1";
    }
    public Boolean scheduledDomingo(){
        return this.domingo == "1";
    }

}
