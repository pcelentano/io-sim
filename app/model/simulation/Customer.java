package model.simulation;

/**
 * Customer Simulation.
 */


public class Customer {

    private final CustomerType type;
    private double permanence;
    private double waitTime;
    private final int customerNumber;
    private boolean interrupted;
    private final double arrivalTime;
    private boolean wasCaged;


    /** Set Customer interrupted. */
    public boolean isInterrupted() { return interrupted; }

    /** Customer priority type. */
    public CustomerType getType() { return type; }

    /** Customer with given priority. */
    public Customer(CustomerType type, int customerNumber, double arrivalTime) {
        this.type = type;
        this.customerNumber = customerNumber;
        this.arrivalTime = arrivalTime;
        interrupted = false;
    }

    public double getPermanence() { return permanence; }

    public Customer setPermanence(double permanence) {
        this.permanence = permanence;
        return this;
    }

    public double getWaitTime() { return waitTime; }

    public int getCustomerNumber() { return customerNumber; }

    public Customer interrupted() {
        interrupted = true;
        return this;
    }

    public Customer waitTime(double waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public enum CustomerType {
        A, B
    }

    public boolean wasCaged() { return wasCaged; }

    /** For tolerance option set that the customer was caged for future metrics */
    public void caged() { wasCaged = true; }
}
