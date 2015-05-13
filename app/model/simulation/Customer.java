package model.simulation;

/**
 * Customer Simulation.
 */


public class Customer {

    private final CustomerType type;
    private double permanence;
    private final int customerNumber;
    private boolean interrupted;


    public boolean isInterrupted() { return interrupted; }

    /** Customer priority type. */
    public CustomerType getType() { return type; }

    /** Customer with given priority. */
    public Customer(CustomerType type, int customerNumber) {
        this.type = type;
        this.customerNumber = customerNumber;
        interrupted = false;
    }

    public double getPermanence() { return permanence; }

    public Customer setPermanence(double permanence) {
        this.permanence = permanence;
        return this;
    }

    public int getCustomerNumber() { return customerNumber; }

    public Customer interrupted() {
        interrupted = true;
        return this;

    }


    public enum CustomerType {
        A, B
    }

}
