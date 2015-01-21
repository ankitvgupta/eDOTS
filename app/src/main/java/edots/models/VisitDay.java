package edots.models;

import java.util.Date;

/**
 * Created by brendan on 1/21/15.
 */
public class VisitDay {
    private Date date;
    private int morning;
    private int afternoon;

    public VisitDay(Date date, int morning, int afternoon){
        this.date = date;
        this.morning = morning;
        this.afternoon = afternoon;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getMorning() {
        return morning;
    }

    public void setMorning(int morning) {
        this.morning = morning;
    }

    public int getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(int afternoon) {
        this.afternoon = afternoon;
    }
}
