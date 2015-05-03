package model.simulation;

/**
 * Simulation Events.
 */
public class Event implements Comparable<Event> {

    private final EventType type;
    private final Customer customer;
    private final double time;

    /** Event Type. */
    public EventType getType() { return type; }

    /** Event Customer. */
    public Customer getCustomer() { return customer; }

    /** Event start time. */
    public double getTime() { return time; }

    /** Event for type customer and start time. */
    public Event(EventType t, Customer c, double time) {
        type = t;
        customer = c;
        this.time = time;
    }

    @Override public int compareTo(Event o) {
        if (time > o.getTime()) return 1;
        else if (time == o.getTime()) return 0;
        else return -1;
    }

    @Override public String toString() {
        return type.name + "  " + (customer != null ? customer.getType().toString()  : "none" ) + "  :  " + time;
    }

    public enum EventType {

        INITIATION("INICIO"), ARRIVAL("ARRIBO"), DEPARTURE("SALIDA");

        private final String name;

        EventType(String name) {
            this.name = name;
        }

        @Override public String toString() {
            return name;
        }

    }




}
