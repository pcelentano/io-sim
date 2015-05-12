package model.simulation;

/**
 * Customer Simulation.
 */


public class Customer {

    private final CustomerType type;
    private double permanence;
    private final int customerNumber;

    /** Customer priority type. */
    public CustomerType getType() { return type; }

    /** Customer with given priority. */
    public Customer(CustomerType type, int customerNumber) {
        this.type = type;
        this.customerNumber = customerNumber;
    }

    public double getPermanence() { return permanence; }

    public void setPermanence(double permanence) { this.permanence = permanence; }

    public int getCustomerNumber() { return customerNumber; }


    public enum CustomerType {
        A, B
    }

}
