package model.simulation;

/**
 * Simulation Events.
 */
public class Event implements Comparable<Event> {

    private final EventType type;
    private final Customer customer;
    private final double initTime;
    private final int queueLength;
    private final Status attentionChanelStatus;
    private final double deltaTime;

    /** Event Type. */
    public EventType getType() { return type; }

    /** Event Customer. */
    public Customer getCustomer() { return customer; }

    /** Event start initTime. */
    public double getInitTime() { return initTime; }

    /** Event for type customer and start initTime. */
    public Event(EventType t, Customer c, double time, int queueLength, Status attentionChanelStatus, double deltaTime) {
        type = t;
        customer = c;
        initTime = time;
        this.queueLength = queueLength;
        this.attentionChanelStatus = attentionChanelStatus;
        this.deltaTime = deltaTime;
    }

    @Override public int compareTo(Event o) {
        if (initTime > o.getInitTime()) return 1;
        else if (initTime == o.getInitTime()) return 0;
        else return -1;
    }

    @Override public String toString() {
        return type.name + "  " + (customer != null ? customer.getType().toString()  : "none" ) + "  :  " + initTime;
    }

    public int getQueueLength() {
        return queueLength;
    }

    public Status getAttentionChanelStatus() {
        return attentionChanelStatus;
    }

    public double getDeltaTime() {
        return deltaTime;
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


    public enum Status {
        EMPTY, OCCUPIED;
    }
}
