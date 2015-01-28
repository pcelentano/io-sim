package model;

/**
 * Customer Simulation.
 */


public class Customer {

    private final CustomerType type;

    /** Customer priority type. */
    public CustomerType getType() { return type; }

    /** Customer with given priority. */
    public Customer(CustomerType type) {
        this.type = type;
    }


    public enum CustomerType {
        A, B
    }

}
