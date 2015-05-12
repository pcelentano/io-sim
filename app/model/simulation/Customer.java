package model.simulation;

/**
 * Customer Simulation.
 */


public class Customer {

    private final CustomerType type;
    private double permanence;
    private final int customerNumber;
    private boolean attended;

    /** Customer priority type. */
    public CustomerType getType() { return type; }

    /** Customer with given priority. */
    public Customer(CustomerType type, int customerNumber) {
        this.type = type;
        this.customerNumber = customerNumber;
    }

    public double getPermanence() { return permanence; }

    public Customer setPermanence(double permanence) {
        this.permanence = permanence;
        return this;
    }

    public int getCustomerNumber() { return customerNumber; }

    public Customer attended(boolean attended) {
        this.attended = attended;
        return this;

    }


    public enum CustomerType {
        A, B
    }

}
