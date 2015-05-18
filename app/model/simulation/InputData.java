package model.simulation;

/**
 * Created by chelen on 03/05/15.
 */
public class InputData {
    private double clientsHourA;
    private double clientsHourB;
    private double muA;
    private double muB;
    private int time;
    private boolean withEvents;
    private String priority;
    private String tolerance;
    private String resumption;
    private String intolerance;

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }

    public boolean isWithEvents() { return withEvents; }

    public String getTolerance() { return tolerance; }

    public void setTolerance(String tolerance) { this.tolerance = tolerance; }

    public String getResumption() { return resumption; }

    public void setResumption(String resumption) { this.resumption = resumption; }

    public String getIntolerance() { return intolerance; }

    public void setIntolerance(String intolerance) { this.intolerance = intolerance; }



    public double getClientsHourA() {
        return clientsHourA;
    }

    public void setClientsHourA(double clientsHourA) {
        this.clientsHourA = clientsHourA;
    }

    public double getClientsHourB() {
        return clientsHourB;
    }

    public void setClientsHourB(double clientsHourB) {
        this.clientsHourB = clientsHourB;
    }

    public double getMuA() {
        return muA;
    }

    public void setMuA(double muA) {
        this.muA = muA;
    }

    public double getMuB() {
        return muB;
    }

    public void setMuB(double muB) {
        this.muB = muB;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
